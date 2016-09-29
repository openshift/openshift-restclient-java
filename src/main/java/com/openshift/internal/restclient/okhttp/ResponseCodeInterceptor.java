/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.restclient.okhttp;

import com.openshift.internal.restclient.DefaultClient;
import com.openshift.internal.restclient.authorization.AuthorizationDetails;
import com.openshift.internal.restclient.model.Status;
import com.openshift.internal.util.URIUtils;
import com.openshift.restclient.BadRequestException;
import com.openshift.restclient.IClient;
import com.openshift.restclient.NotFoundException;
import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.authorization.ResourceForbiddenException;
import com.openshift.restclient.http.IHttpConstants;
import com.openshift.restclient.model.IStatus;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Interpret response codes and handle accordingly
 * 
 * @author jeff.cantrill
 *
 */
public class ResponseCodeInterceptor implements Interceptor, IHttpConstants {
	
	public static final String X_OPENSHIFT_IGNORE_RCI = "X-OPENSHIFT-IGNORE-RCI";

	private static final Logger LOGGER = Logger.getLogger(ResponseCodeInterceptor.class);
	
	private IClient client;

	/**
	 * If a request tag() implements this interface, HTTP errors
	 * will not throw OpenShift exceptions.
	 */
	public interface Ignore{}


	@Override
	public Response intercept(Chain chain) throws IOException {
		Response response = chain.proceed(chain.request());
		if(!response.isSuccessful() && StringUtils.isBlank(response.request().header(X_OPENSHIFT_IGNORE_RCI))) {
			switch(response.code()) {
			case STATUS_UPGRADE_PROTOCOL:
			case STATUS_MOVED_PERMANENTLY:
				break;
			case STATUS_MOVED_TEMPORARILY:
				response = makeSuccessIfAuthorized(response);
				break;
			default:
				if ( response.request().tag() instanceof Ignore == false ) {
					throw createOpenShiftException(client, response, null);
				}
			}
		}
		return response;
	}
	
	private Response makeSuccessIfAuthorized(Response response) {
		String location = response.header(PROPERTY_LOCATION);
		if(StringUtils.isNotBlank(location) && URIUtils.splitFragment(location).containsKey(OpenShiftAuthenticator.ACCESS_TOKEN)) {
			response = response.newBuilder()
				.request(response.request())
				.code(STATUS_OK)
				.headers(response.headers())
				.build();
		}
		return response;
	}

	public void setClient(DefaultClient client) {
		this.client = client;
	}
	
	public static IStatus getStatus(String response) {
		if(response.startsWith("{")) {
			return new Status(response);
		}
		return null;
	}
	
	public static OpenShiftException createOpenShiftException(IClient client, Response response, Throwable e) throws IOException{
		LOGGER.debug(response, e);
		IStatus status = getStatus(response.body().string());
		int responseCode = response.code();
		if(status != null && status.getCode() != 0) {
			responseCode = status.getCode();
		}
		switch(responseCode) {
		case STATUS_BAD_REQUEST:
			return new BadRequestException(e, status, response.request().url().toString());
		case STATUS_FORBIDDEN:
			return new ResourceForbiddenException(status != null ? status.getMessage() : "Resource Forbidden", status, e);
		case STATUS_UNAUTHORIZED:
			String link = String.format("%s/oauth/token/request", client.getBaseURL());
			AuthorizationDetails details = new AuthorizationDetails(response.headers(), link);
			return new com.openshift.restclient.authorization.UnauthorizedException(details, status);
		case IHttpConstants.STATUS_NOT_FOUND:
			return new NotFoundException(status == null ? "Not Found" : status.getMessage());
		default:
			return new OpenShiftException(e, status, "Exception trying to %s %s response code: %s", response.request().method(), response.request().url().toString(), responseCode);
		}
	}

	public static OpenShiftException createOpenShiftException(IClient client, int responseCode, String message, String response, Throwable e) throws IOException{
		LOGGER.debug(response, e);
		IStatus status = getStatus(response);
		if(status != null && status.getCode() != 0) {
			responseCode = status.getCode();
		}
		switch(responseCode) {
		case STATUS_BAD_REQUEST:
			return new BadRequestException(e, status, response);
		case STATUS_FORBIDDEN:
			return new ResourceForbiddenException(status != null ? status.getMessage() : "Resource Forbidden", status, e);
		case STATUS_UNAUTHORIZED:
			return new com.openshift.restclient.authorization.UnauthorizedException(client.getAuthorizationContext().getAuthorizationDetails(), status);
		case IHttpConstants.STATUS_NOT_FOUND:
			return new NotFoundException(status == null ? "Not Found" : status.getMessage());
		default:
			return new OpenShiftException(e, status, "Exception trying to fetch %s response code: %s", response, responseCode);
		}
	}
	
	
}
