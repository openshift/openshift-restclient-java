/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.openshift.client.HttpMethod;
import com.openshift.client.IHttpClient;
import com.openshift.client.OpenShiftException;
import com.openshift.client.OpenShiftRequestException;
import com.openshift.internal.client.httpclient.EncodingException;
import com.openshift.internal.client.httpclient.request.FormUrlEncodedMediaType;
import com.openshift.internal.client.httpclient.request.Parameter;
import com.openshift.internal.client.httpclient.request.ParameterValueMap;
import com.openshift.internal.client.utils.StringUtils;
import com.openshift.internal.client.utils.UrlUtils;

/**
 * The Class Link.
 * 
 * @author Xavier Coulon
 */
public class Link {

	/** The related resource (destination) this link points to */
	private final String rel;

	/** The url/href this link point to. */
	private final String href;

	/** The http method that this link operates on. */
	private final HttpMethod httpMethod;

	/** The required params of this link. */
	private final List<LinkParameter> requiredParams;

	/** The optional params of this link. */
	private final List<LinkParameter> optionalParams;

	public Link(final String href, final HttpMethod httpMethod) {
		this(null, href, httpMethod);
	}

	public Link(final String rel, final String href, final HttpMethod httpMethod) {
		this(rel, href, httpMethod, null, null);
	}

	protected Link(final String rel, final String href, final String httpMethod,
			final List<LinkParameter> requiredParams, final List<LinkParameter> optionalParams) {
		this(rel, href, HttpMethod.valueOf(httpMethod), requiredParams, optionalParams);
	}

	/**
	 * Instantiates a new Link object.
	 * 
	 * @param rel
	 *            the related resource this link points to
	 * @param href
	 *            the href the url/href this link points to
	 * @param httpMethod
	 *            the http method that this link operates on
	 * @param requiredParams
	 *            the required params
	 * @param optionalParams
	 *            the optional params
	 */
	protected Link(final String rel, final String href, final HttpMethod httpMethod,
			final List<LinkParameter> requiredParams, final List<LinkParameter> optionalParams) {
		this.rel = rel;
		this.href = href;
		this.httpMethod = httpMethod;
		this.requiredParams = requiredParams;
		this.optionalParams = optionalParams;
	}

	/**
	 * Gets the related resource (destination) this link points to.
	 * 
	 * @return the rel
	 */
	public final String getRel() {
		return rel;
	}

	/**
	 * Gets the url/href this link points to.
	 * 
	 * @return the href
	 */
	public final String getHref() {
		return href;
	}

	public final String getHref(String server, String servicePath, Parameter... urlParameters) {
		return addParameters(ensureAbsoluteUrl(href, server, servicePath), urlParameters);
	}

	public final String getHref(String server, String servicePath, List<Parameter> urlParameters) {
		return addParameters(ensureAbsoluteUrl(href, server, servicePath), urlParameters);
	}

	/**
	 * Gets the http method this link operates on.
	 * 
	 * @return the httpMethod
	 */
	public final HttpMethod getHttpMethod() {
		return httpMethod;
	}

	/**
	 * Gets the required params.
	 * 
	 * @return the requiredParams
	 */
	public final List<LinkParameter> getRequiredParams() {
		return requiredParams;
	}

	/**
	 * Gets the optional params.
	 * 
	 * @return the optionalParams
	 */
	public final List<LinkParameter> getOptionalParams() {
		return optionalParams;
	}

	public boolean hasParameter(String name) {
		if (getParameter(name, requiredParams) != null) {
			return true;
		} else if (getParameter(name, optionalParams) != null) {
			return true;
		}
		return false;
	}
		
	public void validateRequestParameters(Parameter[] parameters)
			throws OpenShiftRequestException {
		if (getRequiredParams() != null) {
			for (LinkParameter requiredParameter : getRequiredParams()) {
				validateRequiredParameter(requiredParameter, parameters);
			}
		}
		if (getOptionalParams() != null) {
			for (LinkParameter optionalParameter : getOptionalParams()) {
				validateOptionalParameters(optionalParameter);
			}
		}
	}

	private void validateRequiredParameter(LinkParameter linkParameter, Parameter[] parameters)
			throws OpenShiftRequestException {
		Parameter parameter = getParameter(linkParameter.getName(), parameters);
		if (parameter == null) {
			throw new OpenShiftRequestException(
					"Requesting {0}: required request parameter \"{1}\" is missing", getHref(),
					linkParameter.getName());
		}

		if (isEmptyString(linkParameter, parameter.getValue())) {
			throw new OpenShiftRequestException("Requesting {0}: required request parameter \"{1}\" is empty",
					getHref(), linkParameter.getName());
		}
		// TODO: check valid options (still reported in a very incosistent way)
	}

	private Parameter getParameter(String name, Parameter[] parameters) {
		if (StringUtils.isEmpty(name)
				|| parameters == null) {
			return null;
		}
		for (Parameter parameter : parameters) {
			if (name.equals(parameter.getName())) {
				return parameter;
			}
		}
		return null;
	}
	
	private LinkParameter getParameter(String name, List<LinkParameter> parameters) {
		if (StringUtils.isEmpty(name)
				|| parameters == null) {
			return null;
		}
		for (LinkParameter parameter : parameters) {
			if (name.equals(parameter.getName())) {
				return parameter;
			}
		}
		return null;
	}

	private void validateOptionalParameters(LinkParameter optionalParameter) {
		// TODO: implement
	}

	private boolean isEmptyString(LinkParameter parameter, Object parameterValue) {
		return parameter.getType() == LinkParameterType.STRING
				&& parameterValue instanceof String
				&& StringUtils.isEmpty((String) parameterValue);
	}

	/**
	 * 
	 * @param href
	 * @param server
	 * @param servicePath
	 * @return
	 */
	private String ensureAbsoluteUrl(String href, String server, String servicePath) {
		if (StringUtils.isEmpty(href)
				|| href.startsWith(IHttpClient.HTTP)) {
			return href;
		}

		if (StringUtils.isEmpty(servicePath)
				|| href.startsWith(servicePath)) {
			return StringUtils.prependIfNonEmpty(server, href);
		}
		
		if(!href.startsWith(servicePath)) {
			href = UrlUtils.appendPath(servicePath, href);
		}
		return StringUtils.prependIfNonEmpty(server, href);
	}
	
	private String addParameters(String url, Parameter... urlParameters) {
		if (urlParameters == null
				|| urlParameters.length == 0) {
			return url;
		}
		return addParameters(url, Arrays.asList(urlParameters));
	}

	private String addParameters(String url, List<Parameter> urlParameters) {
		if (urlParameters == null
				|| urlParameters.size() == 0) {
			return url;
		}
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			out.write(url.getBytes());
			out.write(IHttpClient.QUESTION_MARK);
			new FormUrlEncodedMediaType().writeTo(new ParameterValueMap(urlParameters), out);
			return out.toString();
		} catch (IOException e) {
			throw new OpenShiftException(e, "Could not add paramters {0} to url {1}", urlParameters, url);
		} catch (EncodingException e) {
			throw new OpenShiftException(e, "Could not add paramters {0} to url {1}", urlParameters, url);
		}
	}

	public String toString() {
		return "Link [" +
				"rel=" + rel + ", "
				+ "httpMethod=" + httpMethod + ", "
				+ "href=" + href
				+ "]";
	}

}
