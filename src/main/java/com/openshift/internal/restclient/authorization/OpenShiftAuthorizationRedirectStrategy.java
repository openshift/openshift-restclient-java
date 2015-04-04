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

/**
 * OpenShift authorization redirect strategy to disable
 * redirects once an access token is granted
 */
public class OpenShiftAuthorizationRedirectStrategy extends DefaultRedirectStrategy {
	
	private boolean authorized = false;
	
	public boolean isAuthorized(){
		return authorized;
	}
	
	@Override
	public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED){
			authorized  = false; 
			return false;
		}
		Map<String, String> pairs = splitFragment(getLocationURI(request, response, context));
		if(pairs.containsKey(AuthorizationClient.ACCESS_TOKEN)) {
			authorized = true;
			return false;
		}
			return super.isRedirected(request, response, context);
	}

}