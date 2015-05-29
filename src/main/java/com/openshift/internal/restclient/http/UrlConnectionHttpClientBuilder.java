/******************************************************************************* 
 * Copyright (c) 2012-2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.restclient.http;

import com.openshift.restclient.ISSLCertificateCallback;
import com.openshift.restclient.authorization.BasicAuthorizationStrategy;
import com.openshift.restclient.authorization.IAuthorizationStrategy;
import com.openshift.restclient.http.IHttpClient;

/**
 * @author André Dietisheim
 * @author Corey Daley
 * @author Sean Kavanagh
 */
public class UrlConnectionHttpClientBuilder {

	private String userAgent;
	private String acceptedMediaType;
	private String version;
	private Integer configTimeout;
	private ISSLCertificateCallback callback;
	private String excludeSSLCipherRegex;
	private IAuthorizationStrategy authStrategy;

	public UrlConnectionHttpClientBuilder setUserAgent(String userAgent) {
		this.userAgent = userAgent;
		return this;
	}
	
	public UrlConnectionHttpClientBuilder setAuthorizationStrategy(IAuthorizationStrategy strategy){
		this.authStrategy = strategy;
		return this;
	}
	
	public UrlConnectionHttpClientBuilder setCredentials(String username, String password, String token) {
		return setAuthorizationStrategy(new BasicAuthorizationStrategy(username, password, token));
	}
	
	public UrlConnectionHttpClientBuilder setConfigTimeout (Integer configTimeout) {
		this.configTimeout = configTimeout;
		return this;
	}

	public UrlConnectionHttpClientBuilder setAcceptMediaType(String mediaType) {
		this.acceptedMediaType = mediaType;
		return this;
	}

	public UrlConnectionHttpClientBuilder setSSLCertificateCallback(ISSLCertificateCallback callback) {
		this.callback = callback;
		return this;
	}
	
	public UrlConnectionHttpClientBuilder setVersion(String version) {
		this.version = version;
		return this;
	}

	public UrlConnectionHttpClientBuilder excludeSSLCipher(String excludeSSLCipherRegex) {
		this.excludeSSLCipherRegex = excludeSSLCipherRegex;
		return this;
	}
	
	public IHttpClient client() {
		UrlConnectionHttpClient urlClient = new UrlConnectionHttpClient(
				userAgent, acceptedMediaType, version, callback, configTimeout, excludeSSLCipherRegex);
		urlClient.setAuthorizationStrategy(authStrategy);
		return urlClient;
	}
}
