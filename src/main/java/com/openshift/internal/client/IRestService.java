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

import java.net.SocketTimeoutException;
import java.util.Map;

import com.openshift.client.HttpMethod;
import com.openshift.client.OpenShiftException;
import com.openshift.internal.client.response.Link;
import com.openshift.internal.client.response.RestResponse;

/**
 * @author Andre Dietisheim
 */
public interface IRestService {

	public abstract RestResponse request(Link link)
			throws OpenShiftException, SocketTimeoutException;

	public RestResponse request(Link link, ServiceParameter... serviceParameters)
			throws OpenShiftException;

	public abstract RestResponse request(Link link, Map<String, Object> parameters)
			throws OpenShiftException;

	public abstract String request(String url, HttpMethod httpMethod, Map<String, Object> parameters)
			throws OpenShiftException;

	public abstract void setProxySet(boolean proxySet);

	public abstract void setProxyHost(String proxyHost);

	public abstract void setProxyPort(String proxyPort);

	public abstract String getServiceUrl();

	public abstract String getPlatformUrl();

}