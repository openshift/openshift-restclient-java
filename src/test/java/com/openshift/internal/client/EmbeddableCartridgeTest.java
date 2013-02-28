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

import static com.openshift.client.utils.Samples.GET_APPLICATIONS_WITH2APPS_JSON;
import static com.openshift.client.utils.Samples.GET_APPLICATION_CARTRIDGES_WITH2ELEMENTS_JSON;
import static com.openshift.client.utils.Samples.GET_APPLICATION_WITH2CARTRIDGES2ALIASES_JSON;
import static com.openshift.client.utils.Samples.GET_DOMAINS_1EXISTING;
import static com.openshift.client.utils.UrlEndsWithMatcher.urlEndsWith;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;

import org.junit.Test;
import org.mockito.Mockito;

import com.openshift.client.EmbeddableCartridge;
import com.openshift.client.IApplication;
import com.openshift.client.IEmbeddedCartridge;
import com.openshift.client.IHttpClient;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.OpenShiftConnectionFactory;
import com.openshift.client.utils.Samples;
import com.openshift.internal.client.response.Link;
import com.openshift.internal.client.response.Message;

/**
 * @author Andre Dietisheim
 */
public class EmbeddableCartridgeTest {

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
		ApplicationResource applicationResourceMock = Mockito.mock(ApplicationResource.class);

		// operation
		// verification
		assertEquals(
				new EmbeddableCartridge("redhat"),
				new EmbeddedCartridgeResource(
						"redhat",
						CartridgeType.EMBEDDED,
						"embedded-info",
						Collections.<String, Link> emptyMap(),
						Collections.<Message> emptyList(),
						applicationResourceMock));
		assertFalse(new EmbeddableCartridge("redhat").equals(new EmbeddableCartridge("jboss")));
	}

	@Test
	public void shouldHaveSameHashCode() {
		// pre-coniditions
		ApplicationResource applicationResourceMock = Mockito.mock(ApplicationResource.class);
		// operation
		// verification
		EmbeddedCartridgeResource embeddedCartridge =
				new EmbeddedCartridgeResource(
						"redhat",
						CartridgeType.EMBEDDED,
						"embedded-info",
						Collections.<String, Link> emptyMap(),
						Collections.<Message> emptyList(),
						applicationResourceMock);
		assertEquals(embeddedCartridge.hashCode(), new EmbeddableCartridge("redhat").hashCode());
	}

	@Test
	public void shouldRemoveEmbeddedCartridgeInASetByEmbeddableCartridge() {
		// pre-coniditions
		ApplicationResource applicationResourceMock = Mockito.mock(ApplicationResource.class);
		EmbeddedCartridgeResource embeddedCartridge =
				new EmbeddedCartridgeResource(
						"redhat",
						CartridgeType.EMBEDDED,
						"embedded-info",
						Collections.<String, Link> emptyMap(),
						Collections.<Message> emptyList(),
						applicationResourceMock);
		HashSet<IEmbeddedCartridge> cartridges = new HashSet<IEmbeddedCartridge>();
		cartridges.add(embeddedCartridge);
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
		IHttpClient mockClient = mock(IHttpClient.class);
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/broker/rest/api")))
				.thenReturn(Samples.GET_REST_API_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/user")))
				.thenReturn(Samples.GET_USER_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains")))
				.thenReturn(GET_DOMAINS_1EXISTING.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobar/applications/sample"))).thenReturn(
				GET_APPLICATION_WITH2CARTRIDGES2ALIASES_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobar/applications/sample/cartridges"))).thenReturn(
				GET_APPLICATION_CARTRIDGES_WITH2ELEMENTS_JSON.getContentAsString());

		final IOpenShiftConnection connection =
				new OpenShiftConnectionFactory().getConnection(
						new RestService("http://mock", "clientId", mockClient), "foo@redhat.com", "bar");
		final IApplication app = connection.getUser().getDomain("foobar").getApplicationByName("sample");
		// operation

		// verifications
		IEmbeddedCartridge embeddedCartridge = app.getEmbeddedCartridge("mongodb-2.0");
		assertThat(embeddedCartridge.getUrl()).isEqualTo("mongodb://127.13.83.1:27017/");

		embeddedCartridge = app.getEmbeddedCartridge("mysql-5.1");
		assertThat(embeddedCartridge.getUrl()).isNull();
	}

}
