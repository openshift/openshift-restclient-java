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
package com.openshift.express.client;

import com.openshift.express.internal.client.httpclient.HttpClientException;
import com.openshift.express.internal.client.response.OpenShiftResponse;

/**
 * @author André Dietisheim
 */
public class OpenShiftEndpointException extends OpenShiftException {

	private static final long serialVersionUID = 1L;

	private String url;

	private OpenShiftResponse<Object> errorResponse;

	public OpenShiftEndpointException(String url, Throwable cause, String message, Object... arguments) {
		super(cause, message, arguments);
		this.url = url;
	}

	public OpenShiftEndpointException(String url, HttpClientException e, OpenShiftResponse<Object> errorResponse,
			String errorMessage, Object... arguments) {
		this(url, e, errorMessage, arguments);
		this.errorResponse = errorResponse;
	}

	protected String getUrl() {
		return url;
	}

	public String getResponseMessage() {
		if (errorResponse == null) {
			return null;
		}
		return errorResponse.getMessages();
	}

	public String getResponseResult() {
		if (errorResponse == null) {
			return null;
		}
		return errorResponse.getResult();
	}

	public int getResponseExitCode() {
		if (errorResponse == null) {
			return -1;
		}
		return errorResponse.getExitCode();
	}

}
