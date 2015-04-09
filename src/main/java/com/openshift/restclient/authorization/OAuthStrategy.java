/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.authorization;

/**
 * @author Jeff Cantrill
 */
public class OAuthStrategy implements IAuthorizationStrategy {

	private IAuthorizationClient authclient;
	private IAuthorizationContext context;
	private String baseURL;
	private String username;
	private String password;
	private IAuthorizationStrategy strategy;
	
	public OAuthStrategy(String baseURL, IAuthorizationClient authclient, String username, String password) {
		this.baseURL = baseURL;
		this.authclient = authclient;
		this.username = username;
		this.password = password;
	}

	@Override
	public void authorize(IRequest request) {
		if(context == null){
			context =  authclient.getContext(baseURL, username, password);
			strategy = new BearerTokenAuthorizationStrategy(context.getToken());
		}
		strategy.authorize(request);
	}

}
