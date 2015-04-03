/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.authorization;

import static org.junit.Assert.*;

import org.junit.Test;

import com.openshift.internal.restclient.authorization.AuthorizationClient;
import com.openshift.restclient.authorization.IAuthorizationContext;

public class AuthorizationClientIntegrationTest {

	private static final String BASE_URL = "https://localhost:8443";
	private AuthorizationClient client = new AuthorizationClient();
	
	@Test
	public void testAuthorize() {
		IAuthorizationContext context = client.getContext(BASE_URL, "foo", "bar");
		assertNotNull("Exp. to get a token", context.getToken());
	}

	@Test
	public void testBadAuthorization() {
		IAuthorizationContext context =  client.getContext(BASE_URL, "foo", "");
		assertFalse(context.isAuthorized());
		assertNull("Exp. to not get a token", context.getToken());
	}

}
