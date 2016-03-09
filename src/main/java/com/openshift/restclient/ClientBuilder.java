/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.restclient;

import java.net.MalformedURLException;
import java.net.URL;

import com.openshift.internal.restclient.DefaultClient;
import com.openshift.internal.restclient.ResourceFactory;
import com.openshift.restclient.authorization.IAuthorizationStrategy;

/**
 * Builder to create IClient instances.
 * @author jeff.cantrill
 *
 */
public class ClientBuilder {
	
	private String baseUrl;
	private ISSLCertificateCallback sslCertificateCallback;
	private IResourceFactory resourceFactory;
	private IAuthorizationStrategy authStrategy;

	public ClientBuilder(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public ClientBuilder sslCertificateCallback(ISSLCertificateCallback callback) {
		this.sslCertificateCallback = callback;
		return this;
	}
	
	public ClientBuilder resourceFactory(IResourceFactory factory) {
		this.resourceFactory = factory;
		return this;
	}

	public ClientBuilder resourceFactory(IAuthorizationStrategy authStrategy) {
		this.authStrategy = authStrategy;
		return this;
	}
	
	public IClient build() {
		try {
			ISSLCertificateCallback sslCallback = defaultIfNull(this.sslCertificateCallback, new NoopSSLCertificateCallback());
			IResourceFactory factory = defaultIfNull(resourceFactory, new ResourceFactory(null));
			DefaultClient client = new DefaultClient(new URL(this.baseUrl), null, sslCallback, factory);
			
			client.setAuthorizationStrategy(authStrategy);
			
			return client;
		} catch (MalformedURLException e) {
			throw new OpenShiftException(e, "");
		}
	}
	
	private <T> T defaultIfNull(T value, T aDefault) {
		if(value != null)
			return value;
		return aDefault;
	}
}
