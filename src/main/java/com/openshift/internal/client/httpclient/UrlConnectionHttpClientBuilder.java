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
package com.openshift.internal.client.httpclient;

import com.openshift.client.IHttpClient;

/**
 * @author André Dietisheim
 */
public class UrlConnectionHttpClientBuilder {

	private String userAgent;
	private boolean sslChecks = false;
	private String username;
	private String password;
	private String authKey;
	private String authIV;
	private String acceptedMediaType;
	private String version;

	public UrlConnectionHttpClientBuilder setUserAgent(String userAgent) {
		this.userAgent = userAgent;
		return this;
	}

	public UrlConnectionHttpClientBuilder setSSLChecks(boolean check) {
		this.sslChecks = check;
		return this;
	}

	public UrlConnectionHttpClientBuilder setCredentials(String username, String password) {
		return setCredentials(username, password, null, null);
	}
	
	public UrlConnectionHttpClientBuilder setCredentials(String username, String password, String authKey, String authIV) {
		this.username = username;
		this.password = password;
		this.authKey = authKey;
		this.authIV = authIV;
		return this;
	}

	public UrlConnectionHttpClientBuilder setAcceptMediaType(String mediaType) {
		this.acceptedMediaType = mediaType;
		return this;
	}

	public UrlConnectionHttpClientBuilder setVersion(String version) {
		this.version = version;
		return this;
	}

	public IHttpClient client() {
		return new UrlConnectionHttpClient(
				username, password, userAgent, sslChecks, acceptedMediaType, version, authKey, authIV);
	}
}
