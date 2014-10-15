/******************************************************************************* 
 * Copyright (c) 2013-2014 Red Hat, Inc. 
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
import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import com.openshift.client.IHttpClient;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.cartridge.EmbeddableCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.cartridge.StandaloneCartridge;
import com.openshift.client.utils.CartridgeAssert;
import com.openshift.client.utils.CartridgeTestUtils;
import com.openshift.client.utils.Samples;
import com.openshift.client.utils.TestConnectionBuilder;

/**
 * @author Andre Dietisheim
 * @author Jeff Cantrill
 */
public class StandaloneCartridgeTest extends TestTimer {

	private IOpenShiftConnection connection;

	@Before
	public void setup() throws Throwable {
		IHttpClient client = new HttpClientMockDirector()
				.mockGetCartridges(Samples.GET_CARTRIDGES)
				.client();
		this.connection = new TestConnectionBuilder().defaultCredentials().create(client);
	}

	@Test
	public void shouldDownloadableWithDifferentNameEqualsDownloadable() throws MalformedURLException {
		// pre-coniditions
		// operation
		// verification
		assertThat(new StandaloneCartridge("redhat", new URL(CartridgeTestUtils.GO_URL)))
				.isEqualTo(new StandaloneCartridge("jboss", new URL(CartridgeTestUtils.GO_URL)));
		// should equal if url is equal, name doesnt matter 
		// (name is updated as soon as cartridge is deployed)
		assertThat(new StandaloneCartridge("jboss", new URL(CartridgeTestUtils.GO_URL)))
				.isEqualTo(new StandaloneCartridge("redhat", new URL(CartridgeTestUtils.GO_URL)));
	}

	@Test
	public void shouldDownloadableStandaloneNotEqualsDownloadableEmbeddable() throws MalformedURLException {
		// pre-coniditions
		// operation
		// verification
		assertThat(new StandaloneCartridge(new URL(CartridgeTestUtils.GO_URL)))
				.isNotEqualTo(new EmbeddableCartridge(new URL(CartridgeTestUtils.FOREMAN_URL)));
	}

	@Test
	public void shouldHaveNameDisplaynameDescription() throws Throwable {
		// pre-condition
		IStandaloneCartridge nodeJs = connection.getStandaloneCartridges().get(0);
		CartridgeAssert<IStandaloneCartridge> cartridgeAssert = new CartridgeAssert<IStandaloneCartridge>(nodeJs);

		// operation
		// verifcation
		cartridgeAssert
				.hasName("nodejs-0.6")
				.hasDisplayName("Node.js 0.6")
				.hasDescription(
						"Node.js is a platform built on Chrome's JavaScript runtime for easily building fast, "
						+ "scalable network applications. Node.js is perfect for data-intensive real-time "
						+ "applications that run across distributed devices.");
	}
	
	@Test
	public void standaloneCartridgeResourceShouldEqualStandAloneCartridgeWithoutName() throws MalformedURLException {
		// pre-coniditions
		// operation
		// verification
		assertEquals(new StandaloneCartridge(new URL(CartridgeTestUtils.FOREMAN_URL)),
				new StandaloneCartridge("redhat", new URL(CartridgeTestUtils.FOREMAN_URL)));
	}
}
