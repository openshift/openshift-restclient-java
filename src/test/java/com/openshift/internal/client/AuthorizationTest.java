/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 *
 * Contributors: 
 * Sean Kavanagh - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.openshift.client.IAuthorization;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftException;
import com.openshift.client.utils.TestConnectionFactory;
import com.openshift.internal.client.httpclient.HttpClientException;

/**
 * @author Sean Kavanagh
 */
public class AuthorizationTest extends TestTimer {

	private IUser user;
	private HttpClientMockDirector mockDirector;

	@Before
	public void setUp() throws HttpClientException, OpenShiftException, IOException {
		this.mockDirector = new HttpClientMockDirector();
		final IOpenShiftConnection connection =
				new TestConnectionFactory().getConnection();
		this.user = connection.getUser();
	}

	@Test
	public void shouldCreateGenericAuthorization() throws Exception {
		// pre-conditions
		IAuthorization authorization = user.getAuthorization();
		assertNotNull(authorization.getToken());
		String token = authorization.getToken();
		assertEquals(authorization.getScopes(), IAuthorization.SCOPE_SESSION);

		// operations
		IOpenShiftConnection connection =
				new TestConnectionFactory().getAuthTokenConnection(authorization.getToken());
		authorization = connection.getUser().getAuthorization();

		// verifications
		assertEquals(authorization.getScopes(), IAuthorization.SCOPE_SESSION);
		assertEquals(token, authorization.getToken());

		authorization.destroy();
	}

	@Test
	public void shouldCreateAuthorization() throws Exception {
		// pre-conditions
		IAuthorization authorization = user.createAuthorization("my note", IAuthorization.SCOPE_SESSION);
		assertNotNull(authorization.getToken());

		// operations
		IOpenShiftConnection connection =
				new TestConnectionFactory().getAuthTokenConnection(authorization.getToken());
		authorization = connection.getUser().getAuthorization();

		// verifications
		assertEquals(authorization.getScopes(), IAuthorization.SCOPE_SESSION);
		assertEquals(authorization.getNote(), "my note");

		authorization.destroy();
	}

	@Test
	public void shouldCreateAuthorizationWithExpiration() throws Exception {
		// pre-conditions
		IAuthorization authorization = user.createAuthorization("my note", IAuthorization.SCOPE_SESSION, 600);
		assertNotNull(authorization.getToken());

		// operations
		IOpenShiftConnection connection =
				new TestConnectionFactory().getAuthTokenConnection(authorization.getToken());
		authorization = connection.getUser().getAuthorization();

		// verifications
		assertEquals(authorization.getScopes(), IAuthorization.SCOPE_SESSION);
		assertEquals(authorization.getNote(), "my note");

		authorization.destroy();
	}

	@Test
	public void shouldDestroyAuthorization() throws Exception {
		// pre-conditions
		IAuthorization authorization = user.createAuthorization("my note", IAuthorization.SCOPE_READ, 600);
		assertNotNull(authorization.getToken());

		// operations
		authorization.destroy();

		// verification
		assertNull(authorization.getId());
		assertNull(authorization.getScopes());
		assertNull(authorization.getToken());
		assertNull(authorization.getNote());
		assertEquals(IAuthorization.NO_EXPIRES_IN, authorization.getExpiresIn());
	}

	@Test
	public void shouldCreateNewAuthorization() throws Exception {
		// pre-conditions
		IAuthorization authorization = user.createAuthorization("my note", IAuthorization.SCOPE_READ, 600);
		assertNotNull(authorization.getToken());

		// operations
		authorization.destroy();

		// verification
		// new authorization created upon #getAuthorization() since old one is destroyed
		IAuthorization newAuthorization = user.getAuthorization();
		assertFalse(authorization.equals(newAuthorization));
	}
}
