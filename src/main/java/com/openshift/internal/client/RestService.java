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
import com.openshift.internal.client.response.Message;
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

	private static final String SYSPROPERTY_PROXY_PORT = "proxyPort";
	private static final String SYSPROPERTY_PROXY_HOST = "proxyHost";
	private static final String SYSPROPERTY_PROXY_SET = "proxySet";

	private static final String SERVICE_VERSION = "1.0";

	private String baseUrl;
	private IHttpClient client;

	public RestService(String baseUrl, String clientId, IHttpClient client) {
		this.baseUrl = UrlUtils.ensureStartsWithHttps(baseUrl);
		this.client = client;
		client.setUserAgent(new RestServiceProperties().getUseragent(clientId));
		client.setVersion(SERVICE_VERSION);
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

	public String request(String url, HttpMethod httpMethod, Map<String, Object> parameters) throws OpenShiftException {
		try {
			return request(getUrl(url), httpMethod, parameters);
		} catch (UnauthorizedException e) {
			throw new InvalidCredentialsOpenShiftException(url, e);
		} catch (NotFoundException e) {
			throw new NotFoundOpenShiftException(url, e);
		} catch (HttpClientException e) {
			throw new OpenShiftEndpointException(
					url, e, e.getMessage(),
					"Could not request {0}: {1}", url, getResponseMessage(e));
		} catch (UnsupportedEncodingException e) {
			throw new OpenShiftException(e, e.getMessage());
		} catch (MalformedURLException e) {
			throw new OpenShiftException(e, e.getMessage());
		} catch (SocketTimeoutException e) {
			throw new OpenShiftTimeoutException("Could not request url {0}, connection timed out", url);
		}
	}

	private String getResponseMessage(HttpClientException clientException) {
		try {
			StringBuilder builder = new StringBuilder();
			RestResponse restResponse = ResourceDTOFactory.get(clientException.getMessage());
			for (Message message : restResponse.getMessages()) {
				builder.append(message.toString()).append('\n');
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
		LOGGER.trace("Requesting {} on {}", httpMethod.name(), url);
		LOGGER.info("Requesting {} on {}", httpMethod.name(), url);
		
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
	
	public String request(Link link, String acceptedMediaType,  ServiceParameter... serviceParameters)
			throws MalformedURLException, HttpClientException, SocketTimeoutException, OpenShiftException, UnsupportedEncodingException {
		String save = client.getAcceptedMediaType();
		client.setAcceptedMediaType(acceptedMediaType);
		
		HttpMethod httpMethod = link.getHttpMethod();
		URL url = getUrl(link.getHref());
		Map<String, Object> parameters = toMap(serviceParameters);
		
		LOGGER.trace("Requesting {} on {}", httpMethod.name(), url);
		LOGGER.info("Requesting {} on {}", httpMethod.name(), url);
		
		String response = client.get(url);

		client.setAcceptedMediaType(save);
		
		return response;
	}

	private URL getUrl(String href) throws MalformedURLException, OpenShiftException {
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

	public void setProxySet(boolean proxySet) {
		System.setProperty(SYSPROPERTY_PROXY_SET, String.valueOf(proxySet));
	}

	public void setProxyHost(String proxyHost) {
		System.setProperty(SYSPROPERTY_PROXY_HOST, proxyHost);
	}

	public void setProxyPort(String proxyPort) {
		System.setProperty(SYSPROPERTY_PROXY_PORT, proxyPort);
	}

	public String getServiceUrl() {
		return baseUrl + SERVICE_PATH;
	}

	public String getPlatformUrl() {
		return baseUrl;
	}
}
