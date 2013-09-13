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
package com.openshift.internal.client;

import java.util.List;

import com.openshift.client.IHttpClient;
import com.openshift.client.OpenShiftException;
import com.openshift.internal.client.httpclient.request.IMediaType;
import com.openshift.internal.client.httpclient.request.Parameter;
import com.openshift.internal.client.response.Link;
import com.openshift.internal.client.response.RestResponse;

/**
 * A rest service that executes request against the OpenShift server
 * 
 * @author Andre Dietisheim
 */
public interface IRestService {

	public static final String SERVICE_VERSION = "1.2";

	public static final String SERVICE_PATH = "/broker/rest/";

    public RestResponse request(Link link, Parameter... parameters) throws OpenShiftException;

    public RestResponse request(Link link, int timeout, Parameter... parameters) throws OpenShiftException;

	/**
	 * Requests the given link sending the given parameters while encoding the
	 * parameters with the given media type and respecting the given timeout.
	 * 
	 * @param link
	 *            the link to use
	 * @param parameters
	 *            the parameters to send
	 * @return the rest response
	 * @throws OpenShiftException
	 * 
	 * @see Link
	 * @see Parameter
	 * @see IMediaType
	 * @see IHttpClient#NO_TIMEOUT
	 * @see IHttpClient#SYSPROP_DEFAULT_CONNECT_TIMEOUT
	 * @see IHttpClient#SYSPROP_DEFAULT_READ_TIMEOUT
	 * @see IHttpClient#SYSPROP_OPENSHIFT_CONNECT_TIMEOUT
	 * @see IHttpClient#DEFAULT_CONNECT_TIMEOUT
	 * @see IHttpClient#DEFAULT_READ_TIMEOUT
	 */
	public RestResponse request(Link link, int timeout, IMediaType mediaType, Parameter... parameters)
			throws OpenShiftException;

	public RestResponse request(Link link, List<Parameter> urlParameters, Parameter... parameters)
			throws OpenShiftException;

	public RestResponse request(Link link, int timeout, List<Parameter> urlParameters, Parameter... parameters)
			throws OpenShiftException;

	public RestResponse request(Link link, int timeout, List<Parameter> urlParameters, IMediaType mediaType, Parameter... parameters)
			throws OpenShiftException;

	/**
	 * Returns the url for the OpenShift service, the endpoint which this rest
	 * service class is talking to.
	 * 
	 * @return the url of the OpenShift service
	 */
	public String getServiceUrl();

	/**
	 * Returns the OpenShift server.
	 * 
	 * @return
	 */
	public String getPlatformUrl();

}