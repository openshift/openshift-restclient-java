/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client;

import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.client.HttpMethod;
import com.openshift.client.IHttpClient;
import com.openshift.client.InvalidCredentialsOpenShiftException;
import com.openshift.client.Messages;
import com.openshift.client.NotFoundOpenShiftException;
import com.openshift.client.OpenShiftEndpointException;
import com.openshift.client.OpenShiftException;
import com.openshift.client.OpenShiftTimeoutException;
import com.openshift.internal.client.httpclient.EncodingException;
import com.openshift.internal.client.httpclient.HttpClientException;
import com.openshift.internal.client.httpclient.NotFoundException;
import com.openshift.internal.client.httpclient.UnauthorizedException;
import com.openshift.internal.client.httpclient.request.IMediaType;
import com.openshift.internal.client.httpclient.request.Parameter;
import com.openshift.internal.client.response.IRestResponseFactory;
import com.openshift.internal.client.response.Link;
import com.openshift.internal.client.response.RestResponse;
import com.openshift.internal.client.utils.Assert;
import com.openshift.internal.client.utils.StringUtils;
import com.openshift.internal.client.utils.UrlUtils;

/**
 * @author André Dietisheim
 */
public class RestService implements IRestService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestService.class);

	private String server;
	private IMediaType defaultRequestMediaType;
	private IHttpClient client;
	private IRestResponseFactory factory;

	public RestService(String server, String clientId, IMediaType defaultRequestMediaType, String acceptedMediaType,
			IRestResponseFactory factory, IHttpClient client) {
		this(server, clientId, defaultRequestMediaType, acceptedMediaType, factory, client, new RestServiceProperties());
	}

	protected RestService(String server, String clientId, IMediaType defaultRequestMediaType, String acceptedMediaType,
			IRestResponseFactory factory, IHttpClient client, RestServiceProperties properties) {
		this(server, clientId, null, defaultRequestMediaType, acceptedMediaType, factory, client, properties);
	}

	protected RestService(String server, String clientId, String protocolVersion, IMediaType defaultRequestMediaType,
			String acceptedMediaType, IRestResponseFactory factory, IHttpClient client, RestServiceProperties properties) {
		Assert.notEmpty(server);
		Assert.notNull(defaultRequestMediaType);
		Assert.notEmpty(acceptedMediaType);
		Assert.notNull(factory);
		Assert.notNull(properties);
		Assert.notNull(client);

		this.server = UrlUtils.ensureStartsWithHttps(server);
		this.defaultRequestMediaType = defaultRequestMediaType;
		this.factory = factory;
		this.client = client;
		setupClient(properties.getUseragent(clientId), protocolVersion, acceptedMediaType, client);
	}

	private void setupClient(String userAgent, String protocolVersion, String acceptedMediaType, IHttpClient client) {
		if (StringUtils.isEmpty(protocolVersion)) {
			protocolVersion = SERVICE_VERSION;
		}
		client.setAcceptedMediaType(acceptedMediaType);
		client.setAcceptVersion(protocolVersion);
		client.setUserAgent(userAgent);
	}
	
	@Override
	public RestResponse request(Link link, int timeout, List<Parameter> urlPathParameters,
			List<Parameter> urlParameters, Parameter... parameters)	throws OpenShiftException {
		return request(link, timeout, defaultRequestMediaType, factory, urlPathParameters, urlParameters, parameters);
	}

	@Override
	public RestResponse request(Link link, int timeout, IRestResponseFactory responseFactory, List<Parameter> urlPathParameters,
			List<Parameter> urlParameters, Parameter... parameters) throws OpenShiftException {
		return request(link, timeout, defaultRequestMediaType, responseFactory, urlPathParameters, urlParameters, parameters);
	}
	
	@Override
	public RestResponse request(Link link, int timeout, IMediaType requestMediaType, IRestResponseFactory responseFactory, List<Parameter> urlPathParameter,
			List<Parameter> urlParameters, Parameter... parameters) throws OpenShiftException {
		// link.validateParameters(parameters);
        String url = link.getHref(server, SERVICE_PATH, urlPathParameter, urlParameters);
        try {
            String response = request(new URL(url), link.getHttpMethod(), requestMediaType, timeout, parameters);
            return responseFactory.get(response);
        } catch (EncodingException e) {
            throw new OpenShiftException(e, e.getMessage());
		} catch (MalformedURLException e) {
			throw new OpenShiftException(e, e.getMessage());
        } catch (UnauthorizedException e) {
            throw new InvalidCredentialsOpenShiftException(url, e, getRestResponse(e));
        } catch (NotFoundException e) {
            throw new NotFoundOpenShiftException(url, e, getRestResponse(e));
        } catch (HttpClientException e) {
        	RestResponse restResponse = getRestResponse(e);
        	String message = getMessage(restResponse, e);
			throw new OpenShiftEndpointException(
					url.toString(), e, restResponse, "Could not request {0}: {1}", url, message);
        } catch (SocketTimeoutException e) {
            throw new OpenShiftTimeoutException(url, e,
                    "Could not request url {0}, connection timed out", url);
        }
		
	}

	private RestResponse getRestResponse(HttpClientException clientException) {
		try {
			return factory.get(clientException.getMessage());
		} catch (OpenShiftException e) {
			// unexpected json content 
			LOGGER.error(e.getMessage());
			return null;
		} catch(IllegalArgumentException e) {
			// not json
			return null;
		}
	}

	private String getMessage(RestResponse restResponse, HttpClientException e) {
		if (restResponse == null) {
			return e.getMessage();
		}
		Messages messages = restResponse.getMessages();
		if (messages == null) {
			return "";
		}
		return messages.toString();
	}

	private String request(URL url, HttpMethod httpMethod, IMediaType mediaType, int timeout, Parameter... parameters)
			throws HttpClientException, SocketTimeoutException, OpenShiftException, EncodingException {
		LOGGER.info("Requesting {} with protocol {} on {}",
				new Object[] { httpMethod.name(), SERVICE_VERSION, url });
		
		switch (httpMethod) {
		case GET:
			return client.get(url, timeout);
		case HEAD:
			return client.head(url, timeout);
		case POST:
			return client.post(url, mediaType, timeout, parameters);
		case PUT:
			return client.put(url, mediaType,timeout, parameters);
		case DELETE:
			return client.delete(url, mediaType, timeout, parameters);
		case PATCH:
			return client.patch(url, mediaType, timeout, parameters);
		default:
			throw new OpenShiftException("Unexpected HTTP method {0}", httpMethod.toString());
		}
		
		
	}
	
	public String getServiceUrl() {
		return server + SERVICE_PATH;
	}

	public String getPlatformUrl() {
		return server;
	}
}
