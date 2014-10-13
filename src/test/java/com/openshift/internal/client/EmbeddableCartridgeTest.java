/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
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

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import com.openshift.client.IHttpClient;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.cartridge.EmbeddableCartridge;
import com.openshift.client.cartridge.ICartridge;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.StandaloneCartridge;
import com.openshift.client.cartridge.query.CartridgeNameQuery;
import com.openshift.client.cartridge.query.LatestVersionQuery;
import com.openshift.client.utils.CartridgeAssert;
import com.openshift.client.utils.CartridgeTestUtils;
import com.openshift.client.utils.Samples;
import com.openshift.client.utils.TestConnectionBuilder;

/**
 * @author Andre Dietisheim
 */
public class EmbeddableCartridgeTest extends TestTimer {

	private IOpenShiftConnection connection;

	@Before
	public void setup() throws Throwable {
		IHttpClient client = new HttpClientMockDirector()
				.mockGetCartridges(Samples.GET_CARTRIDGES)
				.client();
		this.connection = new TestConnectionBuilder().defaultCredentials().create(client);
	}

	@Test
	public void shouldNonDownloadableEqualsNonDownloadable() {
		// pre-coniditions
		// operation
		// verification
		assertThat(new EmbeddableCartridge("redhat"))
				.isEqualTo(new EmbeddableCartridge("redhat"));
		assertThat(new EmbeddableCartridge("redhat"))
				.isNotEqualTo(new EmbeddableCartridge("jboss"));
	}

	@Test
	public void shouldDownloadableEqualsDownloadable() throws MalformedURLException {
		// pre-coniditions
		// operation
		// verification
		assertThat(new EmbeddableCartridge(new URL(CartridgeTestUtils.FOREMAN_URL)))
				.isEqualTo(new EmbeddableCartridge(new URL(CartridgeTestUtils.FOREMAN_URL)));
	}

	@Test
	public void shouldDownloadableWithNonEqualNameEqualsDownloadable() throws MalformedURLException {
		// pre-coniditions
		// operation
		// verification
		assertThat(new EmbeddableCartridge("redhat", new URL(CartridgeTestUtils.FOREMAN_URL)))
				.isEqualTo(new EmbeddableCartridge(null, new URL(CartridgeTestUtils.FOREMAN_URL)));
		// should equal if url is equal, name doesnt matter
		// (name is updated as soon as cartridge is deployed)
		assertThat(new EmbeddableCartridge("jboss", new URL(CartridgeTestUtils.FOREMAN_URL)))
				.isEqualTo(new EmbeddableCartridge("redhat", new URL(CartridgeTestUtils.FOREMAN_URL)));
	}

	@Test
	public void shouldDownloadableStandaloneNotEqualsDownloadableEmbeddable() throws MalformedURLException {
		// pre-coniditions
		// operation
		// verification
		assertThat(new EmbeddableCartridge(null, new URL(CartridgeTestUtils.FOREMAN_URL)))
				.isNotEqualTo(new StandaloneCartridge(null, new URL(CartridgeTestUtils.GO_URL)));
	}

	@Test
	public void shouldHaveNameDisplaynameDescription() throws Throwable {
		// pre-condition
		IEmbeddableCartridge mongoDb = connection.getEmbeddableCartridges().get(0);
		CartridgeAssert<IEmbeddableCartridge> cartridgeAssert = new CartridgeAssert<IEmbeddableCartridge>(mongoDb);

		// operation
		// verifcation
		cartridgeAssert
				.hasName("mongodb-2.2")
				.hasDisplayName("MongoDB NoSQL Database 2.2")
				.hasDescription("MongoDB is a scalable, high-performance, open source NoSQL database.");
	}

	@Test
	public void shouldHaveObsoleteCartridges() throws MalformedURLException {
		// pre-coniditions
		// operation
		ICartridge metrics = new CartridgeNameQuery("metrics").get(connection.getCartridges(true));
		assertThat(metrics).isNotNull();
		ICartridge zend = new CartridgeNameQuery("zend-5.6").get(connection.getCartridges(true));
		assertThat(zend).isNotNull();
		ICartridge php = new LatestVersionQuery("php").get(connection.getCartridges(true));
		assertThat(php).isNotNull();
		
		// verification
		assertThat(metrics.isObsolete()).isTrue();
		assertThat(zend.isObsolete()).isTrue();
		assertThat(php.isObsolete()).isFalse();
	}
}
