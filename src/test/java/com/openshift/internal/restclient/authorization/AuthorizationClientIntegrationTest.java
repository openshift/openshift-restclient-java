/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.authorization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.DefaultClientIntegrationTest;
import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.restclient.IClient;
import com.openshift.restclient.authorization.BasicAuthorizationStrategy;
import com.openshift.restclient.authorization.IAuthorizationContext;
import com.openshift.restclient.authorization.UnauthorizedException;

/**
 * @author Jeff Cantrill
 */
public class AuthorizationClientIntegrationTest {


	private static final Logger LOG = LoggerFactory.getLogger(DefaultClientIntegrationTest.class);

	private IntegrationTestHelper helper = new IntegrationTestHelper();
	private IClient client;

	@Before
	public void setup () {
		client = helper.createClient();
	}

	/*---------- These are tests that should pass when server is configured for oauth auth. No expectations regarding others */

	/*
	 * Assume Basic Auth, invalid token
	 */
	@Test
	//@Environment(auth=oauth) //lets build this
	public void getAuthorizationContextWhenOauthConfigurationAndInvalidToken() {
		try {
			client = helper.createClientForBasicAuth();
			client.getContext(client.getBaseURL().toString());
			fail("Expected to fail with authorization details");
		}catch(UnauthorizedException e) {
			assertNotNull(e.getAuthorizationDetails());
			LOG.info(e.toString());
		}
	}

	/*
	 * Assume Basic Auth, valid token
	 */
	@Test
	//@Environment(auth=oauth) //lets build this
	public void getAuthorizationContextWhenOauthConfigurationAndValidToken() {
		final String token = "Mzk2MDliYWYtOTA4OC00NzJlLTk2YmQtOGM3ZTAwYTM3ZDU4";
		client = helper.createClient();
		client.setAuthorizationStrategy(new BasicAuthorizationStrategy(helper.getDefaultClusterAdminUser(), helper.getDefaultClusterAdminPassword(), token));
		IAuthorizationContext context = client.getContext(client.getBaseURL().toString());
		assertEquals(token, context.getToken());
	}

	/*---------- These are tests that should pass when server is configured for basic auth. No expectations regarding others */

	/*
	 * Assume Basic Auth, valid token
	 */
	@Test
	//@Environment(auth=basic) //lets build this
	public void getAuthorizationContextWhenBasicAuthConfiguredAndValidToken() {
		final String token = "Mzk2MDliYWYtOTA4OC00NzJlLTk2YmQtOGM3ZTAwYTM3ZDU4";
		client = helper.createClient();
		client.setAuthorizationStrategy(new BasicAuthorizationStrategy(helper.getDefaultClusterAdminUser(), helper.getDefaultClusterAdminPassword(), token));
		IAuthorizationContext context = client.getContext(client.getBaseURL().toString());
		assertEquals(token, context.getToken());
	}

	/*
	 * Assume Basic Auth, invalid token
	 */
	@Test
	//@Environment(auth=basic) //lets build this
	public void getAuthorizationContextWhenBasicAuthConfiguredAndInValidToken() {
		final String token = "asdfasd";
		client = helper.createClient();
		client.setAuthorizationStrategy(new BasicAuthorizationStrategy(helper.getDefaultClusterAdminUser(), helper.getDefaultClusterAdminPassword(), token));
		IAuthorizationContext context = client.getContext(client.getBaseURL().toString());
		assertNotSame("Exp. to get a new token using the username and password", token, context.getToken());
	}

}
