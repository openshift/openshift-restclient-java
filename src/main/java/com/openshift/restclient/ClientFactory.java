/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient;

import java.net.MalformedURLException;
import java.net.URL;

import com.openshift.client.IHttpClient.ISSLCertificateCallback;
import com.openshift.internal.restclient.DefaultClient;

/**
 * Factory class for creating clients to an OpenShift server 
 */
public final class ClientFactory {
	
	/**
	 * 
	 * @param baseUrl                  The OpenShift server URL
	 * @param sslCertCallback     The callback handler for SSL Cert challanges
	 * @return an implementation of IClient
	 * @throws OpenShiftException if the baseURL is malformed
	 */
	public final IClient create(String baseUrl, ISSLCertificateCallback sslCertCallback){
		URL url;
		try {
			url = new URL(baseUrl);
		} catch (MalformedURLException e) {
			throw new OpenShiftException(String.format("Malformed URL '%s'", baseUrl), e, null);
		}
		return new DefaultClient(url, sslCertCallback);
	}
}
