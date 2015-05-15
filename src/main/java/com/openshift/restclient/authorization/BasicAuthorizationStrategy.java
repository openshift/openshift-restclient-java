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
import com.openshift.restclient.utils.Base64Coder;

/**
 * Authorization strategy for basic authorization
 * 
 * @author Jeff Cantrill
 */
public class BasicAuthorizationStrategy implements IAuthorizationStrategy{

	private final String password;
	private final String username;
	private final String token;
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @param token     optionally a token to use before trying to auth with username/password
	 */
	public BasicAuthorizationStrategy(String username, String password, String token) {
		this.username = username;
		this.password = password;
		this.token = token;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}
	
	public String getToken() {
		return token;
	}

	@Override
	public void authorize(IRequest request) {
		String value = IHttpClient.AUTHORIZATION_BASIC + " " + Base64Coder.encode(String.format("%s:%s", username, password));
		request.setProperty(IHttpClient.PROPERTY_AUTHORIZATION, value);
	}

	@Override
	public void accept(IAuthorizationStrategyVisitor visitor) {
		visitor.visit(this);
	}

}
