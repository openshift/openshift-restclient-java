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
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_1EMBEDDED;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_2EMBEDDED;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_3EMBEDDED;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_2EMBEDDED;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_3EMBEDDED;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.SocketTimeoutException;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IHttpClient;
import com.openshift.client.IUser;
import com.openshift.client.cartridge.EmbeddableCartridge;
import com.openshift.client.cartridge.IEmbeddedCartridge;
import com.openshift.client.utils.ResourcePropertyAssert;
import com.openshift.client.utils.Samples;
import com.openshift.client.utils.TestConnectionFactory;
import com.openshift.internal.client.httpclient.HttpClientException;
import com.openshift.internal.client.response.CartridgeResourceDTO;
import com.openshift.internal.client.response.ResourceProperties;

/**
 * @author Andre Dietisheim
 */
public class EmbeddedCartridgeTest {

	private IApplication application;
	private HttpClientMockDirector mockDirector;

	@Before
	public void setUp() throws SocketTimeoutException, HttpClientException, Throwable {
		// pre-conditions
		this.mockDirector = new HttpClientMockDirector();
		IHttpClient client =
				mockDirector
						.mockGetDomains(GET_DOMAINS)
						.mockGetApplications(
								"foobarz", GET_DOMAINS_FOOBARZ_APPLICATIONS_2EMBEDDED)
						.mockGetApplicationCartridges(
								"foobarz", "springeap6",
								GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_2EMBEDDED)
						.client();
		IUser user = new TestConnectionFactory().getConnection(client).getUser();
		IDomain domain = user.getDomain("foobarz");
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
		assertEquals(new EmbeddableCartridge("redhat"), embeddedCartridgeMock);
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
		assertThat(mysql.getUrl()).isEqualTo("mysql://$OPENSHIFT_MYSQL_DB_HOST:$OPENSHIFT_MYSQL_DB_PORT/");
	}

	@Test
	public void shouldHaveDisplayName() throws Throwable {
		// pre-conditions
		// operation
		IEmbeddedCartridge mysql = application.getEmbeddedCartridge("mysql-5.1");

		// verifications
		assertThat(mysql.getDisplayName()).isEqualTo("MySQL Database 5.1");
	}

	@Test
	public void shouldHaveDescription() throws Throwable {
		// pre-conditions
		// operation
		IEmbeddedCartridge mysql = application.getEmbeddedCartridge("mysql-5.1");

		// verifications
		assertThat(mysql.getDescription())
				.isEqualTo(
						"MySQL is a multi-user, multi-threaded SQL database server.");
	}

	@Test
	public void shouldBeLoadedAfterRefresh() throws Throwable {
		// pre-conditions
		EmbeddedCartridgeResource mysql =
				(EmbeddedCartridgeResource) application.getEmbeddedCartridge("mysql-5.1");
		assertThat(mysql.isResourceLoaded()).isFalse();
		// operation
		mysql.refresh();

		// verifications
		assertThat(mysql.isResourceLoaded()).isTrue();
	}

	@Test
	public void shouldLoadCartridgeOnDescription() throws Throwable {
		// pre-conditions
		EmbeddedCartridgeResource mysql =
				(EmbeddedCartridgeResource) application.getEmbeddedCartridge("mysql-5.1");
		assertThat(mysql.isResourceLoaded()).isFalse();

		// operation
		mysql.getDescription();

		// verifications
		assertThat(mysql.isResourceLoaded()).isTrue();
	}

	@Test
	public void shouldNotLoadCartridgeTwice() throws Throwable {
		// pre-conditions
		IEmbeddedCartridge mysql = application.getEmbeddedCartridge("mysql-5.1");

		// operation
		mysql.getDescription(); // triggers application to load all cartridge
								// resource(s)
		mysql.getDisplayName(); // should not trigger a 2nd time

		// verifications
		mockDirector.verifyGetApplicationCartridges(1, application.getDomain().getId(), application.getName());
	}

	@Test
	public void shouldUpdatePropertiesWhenRefreshed() throws Throwable {
		// pre-conditions
		HttpClientMockDirector mockDirector = new HttpClientMockDirector();
		IHttpClient client = mockDirector
				.mockGetDomains(GET_DOMAINS)
				.mockGetApplications(
						"foobarz", GET_DOMAINS_FOOBARZ_APPLICATIONS_3EMBEDDED)
				.mockGetApplicationCartridges(
						"foobarz", "springeap6",
						GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_3EMBEDDED)
				.client();
		IUser user = new TestConnectionFactory().getConnection(client).getUser();
		IDomain domain = user.getDomain("foobarz");
		IApplication application = domain.getApplicationByName("springeap6");
		assertThat(application.getEmbeddedCartridges()).onProperty("name").contains("switchyard-0");

		// operation
		IEmbeddedCartridge switchyard = application.getEmbeddedCartridge("switchyard-0");
		// no properties in embedded block (within application)
		assertThat(switchyard.getProperties().size()).isEqualTo(0); 
		switchyard.refresh();

		// verification
		mockDirector.verifyGetApplicationCartridges(1, "foobarz", "springeap6");
		ResourceProperties properties = switchyard.getProperties();
		// 1 property in embedded block in cartridges
		assertThat(properties.size()).isEqualTo(1); 
		new ResourcePropertyAssert(properties.getAll().iterator().next())
				.hasName("module_path")
				.hasDescription("Module Path")
				.hasType("cart_data");
	}

	@Test
	public void shouldRemoveRemovedCartridge() throws Throwable {
		// pre-conditions
		// contains mongo and mysql
		assertThat(application.getEmbeddedCartridges().size()).isEqualTo(2);
		assertThat(application.getEmbeddedCartridge("mysql-5.1")).isNotNull();
		mockDirector.mockGetCartridges(Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_1EMBEDDED);
		
		// operation
		// triggers app to load updated list without mysql, only mongo
		application.refresh();

		// verifications
		mockDirector.verifyGetApplicationCartridges(1, application.getDomain().getId(), application.getName());
		// mysql missing now
		assertThat(application.getEmbeddedCartridges().size()).isEqualTo(1);
		assertThat(application.getEmbeddedCartridge("mysql-5.1")).isNull();
	}

	
	private IEmbeddedCartridge createEmbeddedCartridgeMock(String name) {
		ApplicationResource applicationResourceMock = Mockito.mock(ApplicationResource.class);
		CartridgeResourceDTO cartridgeDTO = new CartridgeResourceDTO(name, null, null) {
		};
		return new EmbeddedCartridgeResource(cartridgeDTO, applicationResourceMock);
	}
}
