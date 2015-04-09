/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.authorization;

import com.openshift.restclient.http.IHttpClient;

/**
 * Authorization strategy to support Kerberos authorization
 * to the OpenShift broker
 * 
 * @author Jeff Cantrill
 */
public class KerbrosBrokerAuthorizationStrategy implements	IAuthorizationStrategy {

	private final String authIV;
	private final String authKey;

	public KerbrosBrokerAuthorizationStrategy(String authKey, String authIV) {
		this.authKey = authKey;
		this.authIV = authIV;
	}

	@Override
	public void authorize(IRequest request) {
		request.setProperty(IHttpClient.PROPERTY_AUTHKEY, authKey);
		request.setProperty(IHttpClient.PROPERTY_AUTHIV, authIV);
	}

}
