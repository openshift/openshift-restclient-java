/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client;

import static org.fest.assertions.Assertions.assertThat;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import com.openshift.client.IHttpClient;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.utils.TestConnectionFactory;

/**
 * @author Xavier Coulon
 * @author Andre Dietisheim
 */
public class UserTest extends TestTimer {

	private IUser user;
	private static final String PASSWORD = "123490";
	private static final String SERVER = "openshift-origin-server.local";
	private IHttpClient clientMock;
	private HttpClientMockDirector mockDirector;

	@Before
	public void setup() throws Throwable {
		this.mockDirector = new HttpClientMockDirector();
		this.clientMock = mockDirector.client();

		final IOpenShiftConnection connection = 
				new TestConnectionFactory().getConnection(PASSWORD, SERVER, clientMock);
		this.user = connection.getUser();
	}

	@Test
	public void shouldReturnServer() throws Throwable {
		// pre-conditions
		// verifications
		assertThat(new URL(user.getServer()).getHost()).isEqualTo(SERVER);
	}

	@Test
	public void shouldHaveCredentials() throws Throwable {
		// pre-conditions
		// verifications
		assertThat(user.getPassword()).isEqualTo(PASSWORD);
	}

	@Test
	public void shouldHaveConsumedGears() throws Throwable {
		// pre-conditions
		// verifications
		assertThat(user.getConsumedGears()).isEqualTo(3);
	}

	@Test
	public void shouldHaveMaxGears() throws Throwable {
		// pre-conditions
		// verifications
		assertThat(user.getMaxGears()).isEqualTo(10);
	}

	@Test
	public void shouldHaveId() throws Throwable {
		// pre-conditions
		// verifications
		assertThat(user.getId()).isEqualTo("511a780cf2cb83f4d0001b23");
	}
}
