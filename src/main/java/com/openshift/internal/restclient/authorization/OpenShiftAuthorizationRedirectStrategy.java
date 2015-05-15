/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.authorization;

import static com.openshift.internal.util.URIUtils.splitFragment;

import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;

import com.openshift.internal.util.Assert;
import com.openshift.restclient.authorization.IAuthorizationContext;
import com.openshift.restclient.authorization.IAuthorizationDetails;
import com.openshift.restclient.authorization.IAuthorizationStrategy;
import com.openshift.restclient.authorization.TokenAuthorizationStrategy;
import com.openshift.restclient.authorization.UnauthorizedException;
import com.openshift.restclient.IClient;

/**
 * OpenShift authorization redirect strategy to disable
 * redirects once an access token is granted
 * 
 * @author Jeff Cantrill
 */
public class OpenShiftAuthorizationRedirectStrategy extends DefaultRedirectStrategy{
	
	private static final String ACCESS_TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	
	private IAuthorizationContext authcontext;
	private IClient client;
	
	public OpenShiftAuthorizationRedirectStrategy(IClient client) {
		Assert.notNull(this.client = client);
	}
	
	public IAuthorizationContext getAuthorizationContext() {
		return authcontext;
	}
	
	@Override
	public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED){
			IAuthorizationDetails details = new AuthorizationDetails(response.getAllHeaders());
			throw new UnauthorizedException(details);
		}
		Map<String, String> pairs = splitFragment(getLocationURI(request, response, context));
		if(pairs.containsKey(ACCESS_TOKEN)) {
			final String token = pairs.get(ACCESS_TOKEN);
			IAuthorizationStrategy strategy = client.getAuthorizationStrategy();
			client.setAuthorizationStrategy(new TokenAuthorizationStrategy(token));
			this.authcontext = new AuthorizationContext(token, pairs.get(EXPIRES), client.getCurrentUser(), IAuthorizationContext.AUTHSCHEME_BASIC);
			client.setAuthorizationStrategy(strategy);
			return false;
		}
		return super.isRedirected(request, response, context);
	}
	

}