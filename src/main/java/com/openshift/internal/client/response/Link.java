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

import java.util.List;

import com.openshift.client.HttpMethod;

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

	public Link(final String rel, final String href, final HttpMethod httpMethod) {
		this(rel, href, httpMethod, null, null);
	}

	public Link(final String rel, final String href, final String httpMethod,
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
	public Link(final String rel, final String href, final HttpMethod httpMethod,
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

	public String toString() {
		return "Link [" +
				"rel=" + rel + ", "
				+ "httpMethod=" + httpMethod + ", "
				+ "href=" + href
				+ "]";
	}

}
