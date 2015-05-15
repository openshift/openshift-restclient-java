/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.authorization;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.impl.client.SystemDefaultCredentialsProvider;

import com.openshift.restclient.authorization.BasicAuthorizationStrategy;
import com.openshift.restclient.authorization.IAuthorizationContext;
import com.openshift.restclient.authorization.IAuthorizationStrategyVisitor;
import com.openshift.restclient.authorization.KerbrosBrokerAuthorizationStrategy;
import com.openshift.restclient.authorization.TokenAuthorizationStrategy;

public class OpenShiftCredentialsProvider implements CredentialsProvider, IAuthorizationStrategyVisitor{
	
	private CredentialsProvider provider = new SystemDefaultCredentialsProvider();
	private Map<String, Credentials> creds = new HashMap<String, Credentials>(2);
	private String token;
	private String scheme;
	
	/**
	 * Get the token if known;
	 * @return
	 */
	public String getToken() {
		return token;
	}
	
	public String getScheme() {
		return scheme;
	}
	
	@Override
	public void visit(BasicAuthorizationStrategy strategy) {
		creds.put(IAuthorizationContext.AUTHSCHEME_BASIC.toLowerCase(), new UsernamePasswordCredentials(strategy.getUsername(), strategy.getPassword()));
		scheme = IAuthorizationContext.AUTHSCHEME_BASIC;
		token = strategy.getToken();
	}
	
	
	@Override
	public void visit(TokenAuthorizationStrategy strategy) {
		this.scheme = IAuthorizationContext.AUTHSCHEME_OAUTH;
		this.token = strategy.getToken();
	}

	@Override
	public void visit(KerbrosBrokerAuthorizationStrategy strategy) {
	}

	@Override
	public void setCredentials(AuthScope authscope, Credentials credentials) {
		provider.setCredentials(authscope, credentials);
	}

	@Override
	public Credentials getCredentials(AuthScope authscope) {
		final String scheme = authscope.getScheme().toLowerCase();
		if(creds.containsKey(scheme)){
			return creds.get(scheme);
		}
		return provider.getCredentials(authscope);
	}

	@Override
	public void clear() {
		provider.clear();
	}
	
}