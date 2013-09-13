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

import static com.openshift.client.utils.Cartridges.FOREMAN_DOWNLOAD_URL;
import static com.openshift.client.utils.Samples.GET_DOMAINS;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_2EMBEDDED;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_3EMBEDDED;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_DOWNLOADABLECART;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_2EMBEDDED;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_3EMBEDDED;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IHttpClient;
import com.openshift.client.IUser;
import com.openshift.client.cartridge.EmbeddableCartridge;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.IEmbeddedCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.cartridge.StandaloneCartridge;
import com.openshift.client.cartridge.selector.UrlPropertyQuery;
import com.openshift.client.utils.CartridgeAssert;
import com.openshift.client.utils.Cartridges;
import com.openshift.client.utils.EmbeddedCartridgeAssert;
import com.openshift.client.utils.ResourcePropertyAssert;
import com.openshift.client.utils.Samples;
import com.openshift.client.utils.TestConnectionFactory;
import com.openshift.internal.client.httpclient.HttpClientException;
import com.openshift.internal.client.response.CartridgeResourceDTO;
import com.openshift.internal.client.response.CartridgeResourceProperties;
import com.openshift.internal.client.response.CartridgeResourceProperty;

/**
 * @author Andre Dietisheim
 */
public class EmbeddedCartridgeResourceTest {

	private IApplication application;
	private HttpClientMockDirector mockDirector;

	@Before
	public void setUp() throws SocketTimeoutException, HttpClientException, Throwable {
		// pre-conditions
		this.mockDirector = new HttpClientMockDirector()
						.mockGetDomains(GET_DOMAINS)
						.mockGetApplications(
								"foobarz", GET_DOMAINS_FOOBARZ_APPLICATIONS_2EMBEDDED)
						.mockGetApplicationCartridges(
								"foobarz", "springeap6",
								GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_2EMBEDDED);
		IDomain domain = mockDirector.getDomain("foobarz");
		this.application = domain.getApplicationByName("springeap6");
	}

	@Test
	public void shouldEmbeddedCartridgeEqualsEmbeddableCartridge() {
		// pre-coniditions
		IEmbeddedCartridge embeddedCartridgeFake = createEmbeddedCartridgeFake("redhat");

		// operation
		// verification
		assertThat(new EmbeddableCartridge("redhat")).isEqualTo(embeddedCartridgeFake);
		assertThat(embeddedCartridgeFake).isEqualTo(new EmbeddableCartridge("redhat"));
		assertThat(new EmbeddableCartridge("redhat")).isNotEqualTo(new EmbeddableCartridge("jboss"));
	}

	@Test
	public void shouldHaveSameHashCode() {
		// pre-coniditions
		IEmbeddedCartridge embeddedCartridgeFake = createEmbeddedCartridgeFake("redhat");
		// operation
		// verification
		assertThat(embeddedCartridgeFake.hashCode()).isEqualTo(new EmbeddableCartridge("redhat").hashCode());
	}
	
	@Test
	public void shouldEmbeddableCartridgeWithNameEqualsEmbeddedCartridgeWithoutName() throws MalformedURLException {
		// pre-coniditions
		// operation
		// verification
		assertEquals(new EmbeddableCartridge(null, new URL(Cartridges.FOREMAN_DOWNLOAD_URL)),
				new EmbeddableCartridge("redhat", new URL(Cartridges.FOREMAN_DOWNLOAD_URL)));
	}

	@Test
	public void shouldRemoveEmbeddedCartridgeInASetByEmbeddableCartridge() {
		// pre-coniditions
		IEmbeddedCartridge embeddedCartridgeMock = createEmbeddedCartridgeFake("redhat");
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
	public void shouldHaveUrlProperty() throws Throwable {
		// pre-conditions
		// operation
		IEmbeddedCartridge mongo = application.getEmbeddedCartridge(Cartridges.MONGODB_22_NAME);
		IEmbeddedCartridge mysql = application.getEmbeddedCartridge(Cartridges.MYSQL_51_NAME);

		// verifications
		UrlPropertyQuery selector = new UrlPropertyQuery();
		CartridgeResourceProperty property = selector.getMatchingProperty(mysql);
		assertThat(property).isNotNull();
		assertThat(property.getValue()).isEqualTo("mysql://$OPENSHIFT_MYSQL_DB_HOST:$OPENSHIFT_MYSQL_DB_PORT/");
		property = selector.getMatchingProperty(mongo);
		assertThat(property).isNotNull();
		assertThat(property.getValue()).isEqualTo("mongodb://$OPENSHIFT_MONGODB_DB_HOST:$OPENSHIFT_MONGODB_DB_PORT/");
	}

	@Test
	public void shouldNotHaveUrlInNonDownloadableCartridge() throws Throwable {
		// pre-conditions
		IEmbeddableCartridge mysql = new EmbeddableCartridge(Cartridges.MYSQL_51_NAME);
		assertThat(mysql.getUrl()).isNull();

		// operation
		IEmbeddedCartridge embeddedMysql = application.getEmbeddedCartridge(mysql);
		// verifications
		new EmbeddedCartridgeAssert(embeddedMysql)
				.hasNoUrl();
	}

	@Test
	public void shouldHaveNameDescriptionDisplayNameUrlInDownloadableCartridge() throws Throwable {
		// pre-conditions
		mockDirector
				.mockGetApplications("foobarz",
						Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP_SCALABLE_DOWNLOADABLECART)
				.mockGetApplication("foobarz", "downloadablecart",
						GET_DOMAINS_FOOBARZ_APPLICATIONS_DOWNLOADABLECART);
		
		IDomain domain = mockDirector.getDomain("foobarz");
		IApplication downloadablecartApp = domain.getApplicationByName("downloadablecart");
		assertThat(downloadablecartApp).isNotNull();

		IEmbeddableCartridge foreman = new EmbeddableCartridge(new URL(FOREMAN_DOWNLOAD_URL));
		new CartridgeAssert<IEmbeddableCartridge>(foreman)
				.hasUrl(Cartridges.FOREMAN_DOWNLOAD_URL)
				.hasName(null)
				.hasDescription(null)
				.hasDisplayName(null);

		// operation
		IEmbeddedCartridge embeddedForeman = downloadablecartApp.getEmbeddedCartridge(foreman);
		// verifications
		// embedded cartridge should get updated with name, description and display name
		new EmbeddedCartridgeAssert(embeddedForeman)
				.hasUrl(Cartridges.FOREMAN_DOWNLOAD_URL)
				.hasName("andygoldstein-foreman-0.63.0")
				.hasDescription("Foreman TODO")
				.hasDisplayName("Foreman");
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
		CartridgeResourceProperties properties = switchyard.getProperties();
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

	@Test
	public void shouldBeDownloadableCartridges() throws Throwable {
		// pre-conditions
		// operation
		// verifications
		assertThat(Cartridges.go11().isDownloadable()).isTrue();
		assertThat(Cartridges.foreman063().isDownloadable()).isTrue();
	}

	@Test
	public void shouldNotBeDownloadableCartridge() throws Throwable {
		// pre-conditions
		IStandaloneCartridge jbossAsCartridge = new StandaloneCartridge("jboss-7");
		IStandaloneCartridge jbossEapCartridge = new StandaloneCartridge("jbosseap-6");

		// operation
		// verifications
		assertThat(jbossAsCartridge.isDownloadable()).isFalse();
		assertThat(jbossAsCartridge.getUrl()).isNull();
		assertThat(jbossEapCartridge.isDownloadable()).isFalse();
		assertThat(jbossEapCartridge.getUrl()).isNull();
	}

	private IEmbeddedCartridge createEmbeddedCartridgeFake(String name) {
		ApplicationResource applicationResourceMock = Mockito.mock(ApplicationResource.class);
		CartridgeResourceDTO cartridgeDTO = new CartridgeResourceDTO(name, null, null) {
		};
		return new EmbeddedCartridgeResource(cartridgeDTO, applicationResourceMock);
	}
}
