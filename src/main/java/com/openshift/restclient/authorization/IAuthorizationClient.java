/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.authorization;

import com.openshift.client.IHttpClient.ISSLCertificateCallback;

public interface IAuthorizationClient {

	/**
	 * Retrieve a token for OpenShift using the given baseURL, username, and password
	 * 
	 * @param baseURL
	 * @param username
	 * @param password
	 * @return
	 */
	IAuthorizationContext getContext(final String baseURL, final String username, final String password);
	
	/**
	 * Set the callback handler to use for certificate trust issues.
	 * @param callback
	 */
	void setSSLCertificateCallback(ISSLCertificateCallback callback);
}
