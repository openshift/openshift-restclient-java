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
import java.security.cert.X509Certificate;

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
	private X509Certificate certificate;
	private String certificateAlias;
	private IResourceFactory resourceFactory;
	private IAuthorizationStrategy authStrategy;
	private String withUserName;
	private Object token;

	public ClientBuilder() {
		this(null);
	}

	public ClientBuilder(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public ClientBuilder sslCertificateCallback(ISSLCertificateCallback callback) {
		this.sslCertificateCallback = callback;
		return this;
	}
	
	public ClientBuilder sslCertificate(String alias, X509Certificate cert) {
		this.certificateAlias = alias;
		this.certificate = cert;
		return this;
	}
	
	public ClientBuilder resourceFactory(IResourceFactory factory) {
		this.resourceFactory = factory;
		return this;
	}

	@Deprecated
	public ClientBuilder resourceFactory(IAuthorizationStrategy authStrategy) {
		this.authStrategy = authStrategy;
		return this;
	}

	public ClientBuilder authorizationStrategy(IAuthorizationStrategy authStrategy) {
		this.authStrategy = authStrategy;
		return this;
	}
	
	public ClientBuilder toCluster(String baseUrl) {
		this.baseUrl = baseUrl;
		return this;
	}

	public ClientBuilder withUserName(String userName) {
		this.withUserName = userName;
		return this;
	}
	
	public ClientBuilder usingToken(String userName) {
		this.token = token;
		return this;
	}
	
	/**
	 * Build a client using the config loading rules defined http://janetkuo.github.io/kubernetes/v1.0/docs/user-guide/kubeconfig-file.html.  Brief summary
	 * of loading order:
	 * 
	 * 1. use explicit values set in builder
	 *   a. username/token
	 *   b. authStrategy
	 * 2. currentContext of config file located at $KUBECONFIG
	 * 3. currentContext of config file located at ~/.kube/config 
	 * 
	 * @return
	 */
	public IClient build() {
		try {
			ISSLCertificateCallback sslCallback = defaultIfNull(this.sslCertificateCallback, new NoopSSLCertificateCallback());
			IResourceFactory factory = defaultIfNull(resourceFactory, new ResourceFactory(null));
			DefaultClient client = new DefaultClient(new URL(this.baseUrl), null, sslCallback, factory, certificateAlias, certificate);
			
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
