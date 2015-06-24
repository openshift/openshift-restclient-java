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

import com.openshift.internal.restclient.DefaultClient;

/**
 * Factory class for creating clients to an OpenShift server
 * 
 * @author Jeff Cantrill
 */
public final class ClientFactory {
	
	/**
	 * Creates a client for the given base url and ssl certificate callback.
	 * 
	 * @param baseUrl
	 *            The OpenShift server URL
	 * @param sslCertCallback
	 *            The callback handler for SSL Cert challanges
	 * @return an implementation of IClient
	 * @throws OpenShiftException
	 *             if the baseURL is malformed
	 */
	public final IClient create(String baseUrl, ISSLCertificateCallback sslCertCallback){
		try {
			return new DefaultClient(new URL(baseUrl), sslCertCallback);
		} catch (MalformedURLException e) {
			throw new OpenShiftException(e, "Malformed URL '%s'", baseUrl);
		}
	}
}
