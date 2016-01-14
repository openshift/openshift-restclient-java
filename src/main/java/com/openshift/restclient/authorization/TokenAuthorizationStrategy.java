/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.authorization;

import com.openshift.restclient.http.IHttpConstants;

/**
 * Authorization stategy to add a Bearer Token to a request
 * 
 * @author Jeff Cantrill
 * @author Andre Dietisheim
 */
public class TokenAuthorizationStrategy extends AbstractAuthorizationStrategy {

	private final String token;
	
	public TokenAuthorizationStrategy(String token) {
		this(token, null);
	}

	public TokenAuthorizationStrategy(String token, String username) {
		super(username);
		this.token = token;
	}

	@Override
	public void authorize(IRequest request) {
		request.setProperty(IHttpConstants.PROPERTY_AUTHORIZATION, String.format("%s %s", IHttpConstants.AUTHORIZATION_BEARER, token));
	}
	
	@Override
	public String getToken(){
		return this.token;
	}

	@Override
	public void accept(IAuthorizationStrategyVisitor visitor) {
		visitor.visit(this);
	}
}
