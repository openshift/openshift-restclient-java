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
import static org.junit.Assert.assertTrue;

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
 * @author Andre Dietisheim
 */
public class AuthorizationIntegrationTest extends TestTimer {

	// TODO: add tests for expired tokens
	private IUser user;

	@Before
	public void setUp() throws HttpClientException, OpenShiftException, IOException {
		final IOpenShiftConnection connection =
				new TestConnectionFactory().getConnection();
		this.user = connection.getUser();
	}
	
	@Test
	public void shouldCreateGenericAuthorization() throws Exception {
		// pre-conditions
		IAuthorization authorization = user.getAuthorization();
		assertNotNull(authorization.getToken());
		assertEquals(authorization.getScopes(), IAuthorization.SCOPE_SESSION);

		// operations
		IOpenShiftConnection connection =
				new TestConnectionFactory().getAuthTokenConnection(authorization.getToken());
		authorization = connection.getUser().getAuthorization();
		
		// verifications
		assertEquals(authorization.getScopes(), IAuthorization.SCOPE_SESSION);

		authorization.destroy();
	}

	@Test
	public void shouldCreateAuthorization() throws Exception {
		// pre-conditions
		IAuthorization authorization = user.createAuthorization("my note", IAuthorization.SCOPE_SESSION_READ);
		assertNotNull(authorization.getToken());
		assertEquals(authorization.getScopes(), IAuthorization.SCOPE_SESSION_READ);

		// operations
		IOpenShiftConnection connection =
				new TestConnectionFactory().getAuthTokenConnection(authorization.getToken());
		authorization = connection.getUser().getAuthorization();

		// verifications
		assertEquals(authorization.getScopes(), IAuthorization.SCOPE_SESSION_READ);
		assertEquals(authorization.getNote(), "my note");

		authorization.destroy();
	}

	@Test
	public void shouldCreateAuthorizationWithExpiration() throws Exception {
		// pre-conditions
		IAuthorization authorization = user.createAuthorization("my note", IAuthorization.SCOPE_SESSION_READ, 600);
		assertNotNull(authorization.getToken());
		assertEquals(authorization.getScopes(), IAuthorization.SCOPE_SESSION_READ);

		// operations
		IOpenShiftConnection connection =
				new TestConnectionFactory().getAuthTokenConnection(authorization.getToken());

		authorization = connection.getUser().getAuthorization();

		// verifications
		assertEquals(authorization.getScopes(), IAuthorization.SCOPE_SESSION_READ);
		assertEquals(authorization.getNote(), "my note");
		assertEquals(authorization.getExpiresIn(), 600);
		
		authorization.destroy();
	}

	@Test
	public void shouldReplaceExistingAuthorization() throws Exception {
		// pre-conditions
		IAuthorization authorization = user.createAuthorization("my note", IAuthorization.SCOPE_SESSION_READ, 600);
		assertNotNull(authorization.getToken());
		assertEquals(authorization.getScopes(), IAuthorization.SCOPE_SESSION_READ);

		// operations
		user.createAuthorization("new note", IAuthorization.SCOPE_SESSION);
		IAuthorization newAuthorization = user.getAuthorization();
		
		// verifications
		assertFalse(authorization.equals(newAuthorization));
		assertEquals(newAuthorization.getScopes(), IAuthorization.SCOPE_SESSION);
		assertFalse(authorization.getToken().equals(newAuthorization.getToken()));
		assertEquals(newAuthorization.getNote(), "new note");
		assertTrue(newAuthorization.getExpiresIn() != 600);
		
		// cleanup
		authorization.destroy();
		newAuthorization.destroy();
	}
}
