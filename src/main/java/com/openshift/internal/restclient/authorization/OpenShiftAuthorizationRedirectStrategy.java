/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.authorization;

import java.net.URI;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;

import com.openshift.internal.util.Assert;
import com.openshift.internal.util.URIUtils;
import com.openshift.restclient.IClient;
import com.openshift.restclient.authorization.IAuthorizationContext;
import com.openshift.restclient.authorization.IAuthorizationDetails;
import com.openshift.restclient.authorization.IAuthorizationStrategy;
import com.openshift.restclient.authorization.TokenAuthorizationStrategy;
import com.openshift.restclient.authorization.UnauthorizedException;

/**
 * OpenShift authorization redirect strategy to disable
 * redirects once an access token is granted
 * 
 * @author Jeff Cantrill
 */
public class OpenShiftAuthorizationRedirectStrategy extends DefaultRedirectStrategy{
	// access_token and expires_in fragment params are an OAuth token response
	private static final String ACCESS_TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";

	// error and error_details query params are an OAuth error response
	private static final String ERROR = "error";
	private static final String ERROR_DETAILS = "error_details";

	private IAuthorizationContext authcontext;
	private IClient client;

	public OpenShiftAuthorizationRedirectStrategy(IClient client) {
		Assert.notNull(this.client = client);
	}

	public IAuthorizationContext getAuthorizationContext() {
		return authcontext;
	}

	@Override
	public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
		// 401 response
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
			IAuthorizationDetails details = new AuthorizationDetails(response.getAllHeaders());
			throw new UnauthorizedException(details);
		}

		// 302 response
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
			Header locationHeader = response.getFirstHeader("Location");
			if (locationHeader == null) {
				return false;
			}
			URI locationURI = createLocationURI(locationHeader.getValue());

			// check access_token fragment param
			Map<String, String> pairs = URIUtils.splitFragment(locationURI);
			if (pairs.containsKey(ACCESS_TOKEN)) {
				final String token = pairs.get(ACCESS_TOKEN);
				IAuthorizationStrategy strategy = client.getAuthorizationStrategy();
				client.setAuthorizationStrategy(new TokenAuthorizationStrategy(token));
				this.authcontext = new AuthorizationContext(token, pairs.get(EXPIRES), client.getCurrentUser(), IAuthorizationContext.AUTHSCHEME_BASIC);
				client.setAuthorizationStrategy(strategy);
				return false;
			}

			// check error query param
			Map<String, String> queryParams = URIUtils.splitQuery(locationURI.getQuery());
			if (queryParams.containsKey(ERROR)) {
				IAuthorizationDetails details = new AuthorizationDetails(queryParams.get(ERROR), queryParams.get(ERROR_DETAILS));
				throw new UnauthorizedException(details);
			}
		}

		return super.isRedirected(request, response, context);
	}

}