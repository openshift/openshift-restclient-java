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

import static com.openshift.client.utils.Samples.GET_DOMAINS;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_2EMBEDDED;
import static com.openshift.client.utils.UrlEndsWithMatcher.urlEndsWith;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IField;
import com.openshift.client.IHttpClient;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.Message;
import com.openshift.client.Messages;
import com.openshift.client.OpenShiftConnectionFactory;
import com.openshift.client.cartridge.EmbeddableCartridge;
import com.openshift.client.cartridge.IEmbeddedCartridge;
import com.openshift.client.utils.Samples;
import com.openshift.internal.client.httpclient.HttpClientException;
import com.openshift.internal.client.response.Link;

/**
 * @author Andre Dietisheim
 */
public class EmbeddedCartridgeTest {

	private IUser user;
	private IDomain domain;
	private IApplication application;

	@Before
	public void setUp() throws SocketTimeoutException, HttpClientException, Throwable {
		// pre-conditions
		IHttpClient mockClient = mock(IHttpClient.class);
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		when(mockClient.get(urlEndsWith("/broker/rest/api")))
				.thenReturn(Samples.GET_API.getContentAsString());
		when(mockClient.get(urlEndsWith("/user")))
				.thenReturn(Samples.GET_USER_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains")))
				.thenReturn(GET_DOMAINS.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications/springeap6")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications/springeap6/cartridges")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_2EMBEDDED.getContentAsString());
		final IOpenShiftConnection connection =
				new OpenShiftConnectionFactory().getConnection(
						new RestService("http://mock", "clientId", mockClient), "foo@redhat.com", "bar");
		this.user = connection.getUser();
		this.domain = user.getDomain("foobarz");
		this.application = domain.getApplicationByName("springeap6");
	}
	
	@Test
	public void shouldEqualsOtherCartridge() {
		// pre-coniditions
		// operation
		// verification
		assertEquals(new EmbeddableCartridge("redhat"), new EmbeddableCartridge("redhat"));
		assertFalse(new EmbeddableCartridge("redhat").equals(new EmbeddableCartridge("jboss")));
	}

	@Test
	public void shouldEqualsEmbeddableCartridge() {
		// pre-coniditions
		IEmbeddedCartridge embeddedCartridgeMock = createEmbeddedCartridgeMock("redhat");

		// operation
		// verification
		assertEquals(new EmbeddableCartridge("redhat"),	embeddedCartridgeMock);
		assertEquals(embeddedCartridgeMock, new EmbeddableCartridge("redhat"));
		assertFalse(new EmbeddableCartridge("redhat").equals(new EmbeddableCartridge("jboss")));
	}

	@Test
	public void shouldHaveSameHashCode() {
		// pre-coniditions
		IEmbeddedCartridge embeddedCartridgeMock = createEmbeddedCartridgeMock("redhat");
		// operation
		// verification
		assertEquals(embeddedCartridgeMock.hashCode(), new EmbeddableCartridge("redhat").hashCode());
	}

	@Test
	public void shouldRemoveEmbeddedCartridgeInASetByEmbeddableCartridge() {
		// pre-coniditions
		IEmbeddedCartridge embeddedCartridgeMock = createEmbeddedCartridgeMock("redhat");
		HashSet<IEmbeddedCartridge> cartridges = new HashSet<IEmbeddedCartridge>();
		cartridges.add(embeddedCartridgeMock);
		assertEquals(cartridges.size(), 1);
		// operation
		boolean removed = cartridges.remove(new EmbeddableCartridge("redhat"));

		// verification
		assertTrue(removed);
		assertEquals(0, cartridges.size());
	}

	@Test
	public void shouldHaveUrl() throws Throwable {
		// pre-conditions
		// operation
		IEmbeddedCartridge mongo = application.getEmbeddedCartridge("mongodb-2.2");
		IEmbeddedCartridge mysql = application.getEmbeddedCartridge("mysql-5.1");

		// verifications
		assertThat(mongo.getUrl()).isEqualTo("mongodb://$OPENSHIFT_MONGODB_DB_HOST:$OPENSHIFT_MONGODB_DB_PORT/");
		assertThat(mysql.getUrl()).isNull();
	}

	@Test
	public void shouldHaveDisplayName() throws Throwable {
		// pre-conditions
		// operation
		IEmbeddedCartridge mysql = application.getEmbeddedCartridge("mysql-5.1");

		// verifications
		assertThat(mysql.getDisplayName()).isEqualTo("MySQL Database 5.1");
	}
	
	private IEmbeddedCartridge createEmbeddedCartridgeMock(String name) {
		ApplicationResource applicationResourceMock = Mockito.mock(ApplicationResource.class);
		return new EmbeddedCartridgeResource(
						name,
						"displayName",
						"description",
						CartridgeType.EMBEDDED,
						"embedded-info",
						Collections.<String, Link> emptyMap(),
						new Messages(Collections.<IField, List<Message>> emptyMap()),
						applicationResourceMock);
	}
}
