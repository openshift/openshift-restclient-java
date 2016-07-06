/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.authorization;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import okhttp3.Headers;

/**
 * @author Jeff Cantrill
 */
public class AuthorizationDetailsTest {
	/*
	 * WWW-Authenticate: OAuth realm=<your_realm>
	 * WWW-Authenticate: Basic
	 * Link: <https://127.0.0.1:8443/oauth/token/request>; rel="related"
	 * Warning: 199 OpenShift "You must obtain an API token by visiting https://127.0.0.1:8443/oauth/token/request"
	 * code 401
	 */
	private Headers.Builder builder = new Headers.Builder();

	@Before
	public void setUp() {
		
	}
	
	@Test
	public void testMessageDetailsWithoutAuthorizationHeader() {
		givenHeader("Link", "<https://127.0.0.1:8443/oauth/token/request>; rel=\"related\"");
		givenHeader("Warning", "199 OpenShift \"You must obtain an API token by visiting https://127.0.0.1:8443/oauth/token/request\"");
		AuthorizationDetails details = whenCreatingAnAuthorizationScheme();
		assertEquals("https://127.0.0.1:8443/oauth/token/request", details.getRequestTokenLink());
		assertEquals("\"You must obtain an API token by visiting https://127.0.0.1:8443/oauth/token/request\"", details.getMessage());
	}

	@Test
	public void testMessageDetailsWithAuthorizationHeader() {
		givenHeader("WWW-Authenticate", "Basic realm=\"openshift\"");
		givenHeader("Warning", "199 OpenShift \"You must obtain an API token by visiting https://127.0.0.1:8443/oauth/token/request\"");
		AuthorizationDetails details = whenCreatingAnAuthorizationScheme();
		assertEquals("Basic", details.getScheme());
	}

	private AuthorizationDetails whenCreatingAnAuthorizationScheme() {
		return new AuthorizationDetails(builder.build());
	}

	private void givenHeader(String name, String value) {
		builder.add(name, value);
	}

}
