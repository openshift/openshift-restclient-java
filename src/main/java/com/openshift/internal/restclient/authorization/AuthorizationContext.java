/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.authorization;

import org.apache.commons.lang.StringUtils;

import com.openshift.restclient.authorization.IAuthorizationContext;
import com.openshift.restclient.model.user.IUser;

/**
 * @author Jeff Cantrill
 */
public class AuthorizationContext implements IAuthorizationContext {
	
	private String token;
	private String expires;
	private String scheme;
	private IUser user;

	public AuthorizationContext(String scope){
		this.scheme = scope;
	}
	

	public AuthorizationContext(String token, String expires, IUser user, String scheme){
		this.token = token;
		this.expires = expires;
		this.user = user;
		this.scheme = scheme;
	}
	
	@Override
	public boolean isAuthorized() {
		return StringUtils.isNotEmpty(token);
	}

	@Override
	public String getToken(){
		return this.token;
	}
	
	@Override
	public String getExpiresIn(){
		return expires;
	}

	@Override
	public String getAuthScheme() {
		return scheme;
	}

	@Override
	public IUser getUser() {
		return user;
	}
	
}
