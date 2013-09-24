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
package com.openshift.client;

import com.openshift.internal.client.httpclient.HttpClientException;
import com.openshift.internal.client.response.RestResponse;


/**
 * @author André Dietisheim
 */
public class InvalidCredentialsOpenShiftException extends OpenShiftEndpointException {

	private static final long serialVersionUID = 1L;

	public  InvalidCredentialsOpenShiftException(String url, HttpClientException cause, RestResponse restResponse) throws OpenShiftException {
		super(url, cause, restResponse, "Your credentials are not authorized to access \"{0}\"", (Object) url);
	}
}
