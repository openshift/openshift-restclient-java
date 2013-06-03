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

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.client.HttpMethod;
import com.openshift.client.IHttpClient;
import com.openshift.client.InvalidCredentialsOpenShiftException;
import com.openshift.client.Message;
import com.openshift.client.NotFoundOpenShiftException;
import com.openshift.client.OpenShiftEndpointException;
import com.openshift.client.OpenShiftException;
import com.openshift.client.OpenShiftRequestException;
import com.openshift.client.OpenShiftTimeoutException;
import com.openshift.internal.client.httpclient.HttpClientException;
import com.openshift.internal.client.httpclient.NotFoundException;
import com.openshift.internal.client.httpclient.UnauthorizedException;
import com.openshift.internal.client.response.Link;
import com.openshift.internal.client.response.LinkParameter;
import com.openshift.internal.client.response.LinkParameterType;
import com.openshift.internal.client.response.ResourceDTOFactory;
import com.openshift.internal.client.response.RestResponse;
import com.openshift.internal.client.utils.StringUtils;
import com.openshift.internal.client.utils.UrlUtils;

/**
 * @author André Dietisheim
 */
public class RestService implements IRestService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestService.class);

	private static final String HTTP = "http";
	
	private static final String SERVICE_PATH = "/broker/rest/";
	private static final char SLASH = '/';

	private String baseUrl;
	private IHttpClient client;

	public RestService(String baseUrl, String clientId, IHttpClient client) {
		this(baseUrl, clientId, new RestServiceProperties(), client);
	}

	RestService(String baseUrl, String clientId, RestServiceProperties properties, IHttpClient client) {
		this(baseUrl, clientId, null,  properties, client);
	}

	RestService(String baseUrl, String clientId, String protocolVersion, RestServiceProperties properties, IHttpClient client) {
		this.baseUrl = UrlUtils.ensureStartsWithHttps(baseUrl);
		this.client = client;
		setupClient(properties.getUseragent(clientId), protocolVersion, client);
	}

	private void setupClient(String userAgent, String protocolVersion, IHttpClient client) {
		if (StringUtils.isEmpty(protocolVersion)) {
			protocolVersion = SERVICE_VERSION;
		}
		client.setAcceptVersion(protocolVersion);
		client.setUserAgent(userAgent);
	}

	public RestResponse request(Link link) throws OpenShiftException {
		return request(link, (Map<String, Object>) null);
	}

	public RestResponse request(Link link, ServiceParameter... serviceParameters) throws OpenShiftException {
		return request(link, toMap(serviceParameters));
	}

	private Map<String, Object> toMap(ServiceParameter... serviceParameters) {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		for (ServiceParameter serviceParameter : serviceParameters) {
			parameterMap.put(serviceParameter.getKey(), serviceParameter.getValue());
		}
		return parameterMap;
	}

	public RestResponse request(Link link, Map<String, Object> parameters)
			throws OpenShiftException {
		validateParameters(parameters, link);
		HttpMethod httpMethod = link.getHttpMethod();
		String response = request(link.getHref(), httpMethod, parameters);
		return ResourceDTOFactory.get(response);
	}

	public String request(String href, HttpMethod httpMethod, Map<String, Object> parameters) throws OpenShiftException {
		URL url = getUrl(href);
		try {
			return request(url, httpMethod, parameters);
		} catch (UnsupportedEncodingException e) {
			throw new OpenShiftException(e, e.getMessage());
		} catch (UnauthorizedException e) {
			throw new InvalidCredentialsOpenShiftException(url.toString(), e);
		} catch (NotFoundException e) {
			throw new NotFoundOpenShiftException(url.toString(), e);
		} catch (HttpClientException e) {
			throw new OpenShiftEndpointException(
					url.toString(), e, e.getMessage(),
					"Could not request {0}: {1}", url.toString(), getResponseMessage(e));
		} catch (SocketTimeoutException e) {
			throw new OpenShiftTimeoutException(url.toString(), e, e.getMessage(), "Could not request url {0}, connection timed out", url.toString());
		}
	}

	private String getResponseMessage(HttpClientException clientException) {
		try {
			RestResponse restResponse = ResourceDTOFactory.get(clientException.getMessage());
			if (restResponse == null) {
				return null;
			}
			StringBuilder builder = new StringBuilder();
			for (Message message : restResponse.getMessages().values()) {
				builder.append(message.getText()).append('\n');
			}
			return builder.toString();
		} catch (OpenShiftException e) {
			// unexpected json content 
			LOGGER.error(e.getMessage());
			return clientException.getMessage();
		} catch(IllegalArgumentException e) {
			// not json
			return clientException.getMessage();
		}
	}

	private String request(URL url, HttpMethod httpMethod, Map<String, Object> parameters)
			throws HttpClientException, SocketTimeoutException, OpenShiftException, UnsupportedEncodingException {
		LOGGER.info("Requesting {} with protocol {} on {}",
				new Object[] { httpMethod.name(), client.getAcceptVersion(), url });
		
		switch (httpMethod) {
		case GET:
			return client.get(url);
		case POST:
			return client.post(parameters, url);
		case PUT:
			return client.put(parameters, url);
		case DELETE:
			return client.delete(parameters, url);
		default:
			throw new OpenShiftException("Unexpected HTTP method {0}", httpMethod.toString());
		}
		
		
	}
	
	private URL getUrl(String href) throws OpenShiftException {
		try {
			if (href == null) {
				throw new OpenShiftException("Invalid empty url");
			}
			if (href.startsWith(HTTP)) {
				return new URL(href);
			}
			if (href.startsWith(SERVICE_PATH)) {
				return new URL(baseUrl + href);
			}
			if (href.charAt(0) == SLASH) {
				href = href.substring(1, href.length());
			}
			return new URL(getServiceUrl() + href);
		} catch (MalformedURLException e) {
			throw new OpenShiftException(e, e.getMessage());
		}
	}

	private void validateParameters(Map<String, Object> parameters, Link link)
			throws OpenShiftRequestException {
		if (link.getRequiredParams() != null) {
			for (LinkParameter requiredParameter : link.getRequiredParams()) {
				validateRequiredParameter(requiredParameter, parameters, link);
			}
		}
		if (link.getOptionalParams() != null) {
			for (LinkParameter optionalParameter : link.getOptionalParams()) {
				validateOptionalParameters(optionalParameter, link);
			}
		}
	}

	private void validateRequiredParameter(LinkParameter parameter, Map<String, Object> parameters, Link link)
			throws OpenShiftRequestException {
		if (parameters == null
				|| !parameters.containsKey(parameter.getName())) {
			throw new OpenShiftRequestException(
					"Requesting {0}: required request parameter \"{1}\" is missing", link.getHref(),
					parameter.getName());
		}

		Object parameterValue = parameters.get(parameter.getName());
		if (parameterValue == null
				|| isEmptyString(parameter, parameterValue)) {
			throw new OpenShiftRequestException("Requesting {0}: required request parameter \"{1}\" is empty",
					link.getHref(), parameter.getName());
		}
		// TODO: check valid options (still reported in a very incosistent way)
	}

	private void validateOptionalParameters(LinkParameter optionalParameter, Link link) {
		// TODO: implement
	}

	private boolean isEmptyString(LinkParameter parameter, Object parameterValue) {
		return parameter.getType() == LinkParameterType.STRING
				&& parameterValue instanceof String
				&& StringUtils.isEmpty((String) parameterValue);
	}

	public String getServiceUrl() {
		return baseUrl + SERVICE_PATH;
	}

	public String getPlatformUrl() {
		return baseUrl;
	}
}
