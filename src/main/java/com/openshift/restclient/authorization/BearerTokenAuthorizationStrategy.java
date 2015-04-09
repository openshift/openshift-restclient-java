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
 * Authorization stategy to add a Bearer Token to a request
 * 
 * @author Jeff Cantrill
 */
public class BearerTokenAuthorizationStrategy implements IAuthorizationStrategy {

	private final String token;
	
	public BearerTokenAuthorizationStrategy(String token) {
		this.token = token;
	}

	@Override
	public void authorize(IRequest request) {
		request.setProperty(IHttpClient.PROPERTY_AUTHORIZATION, String.format("%s %s", IHttpClient.AUTHORIZATION_BEARER, token));
	}
	
	public String getToken(){
		return this.token;
	}
}
