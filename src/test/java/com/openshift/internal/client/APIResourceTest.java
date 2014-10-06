/******************************************************************************* 
 * Copyright (c) 2012-2014 Red Hat, Inc. 
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

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.cartridge.ICartridge;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.utils.CartridgeTestUtils;
import com.openshift.client.utils.TestConnectionBuilder;

/**
 * @author Xavier Coulon
 * @author Andre Dietisheim
 */
public class APIResourceTest extends TestTimer {

	private IOpenShiftConnection connection;
	private HttpClientMockDirector mockDirector;

	@Before
	public void setup() throws Throwable {
		this.mockDirector = new HttpClientMockDirector();
		this.connection = new TestConnectionBuilder().defaultCredentials().create(mockDirector.client());
	}

	@Test
	public void shouldListStandaloneCartridges() throws Throwable {
		// pre-conditions
		// operation
		final List<IStandaloneCartridge> cartridges = connection.getStandaloneCartridges();
		// verifications
		assertThat(cartridges)
				.hasSize(15) // 16 - 1 obsolete (zend-5.6)
				.onProperty("name")
				.contains(CartridgeTestUtils.NODEJS_06_NAME
						, CartridgeTestUtils.JBOSSAS_7_NAME
						, CartridgeTestUtils.JBOSSEAP_6_NAME
						, CartridgeTestUtils.JBOSSEWS_1_NAME
						, CartridgeTestUtils.JBOSSEWS_2_NAME)
				.excludes(CartridgeTestUtils.MONGODB_22_NAME
						, CartridgeTestUtils.MYSQL_51_NAME
						, CartridgeTestUtils.SWITCHYARD_06_NAME);
	}

	public void shouldNotListObsoleteStandaloneCartridges() throws Throwable {
		// pre-conditions
		// operation
		final List<IStandaloneCartridge> cartridges = connection.getStandaloneCartridges();
		// verifications
		assertThat(cartridges)
				.hasSize(14) // 15 - 1 obsolete (zend-5.6)
				.onProperty("name")
				.contains(CartridgeTestUtils.JBOSSAS_7_NAME)
				.excludes(CartridgeTestUtils.ZEND_56_NAME);
	}
	
	public void shouldListObsoleteStandaloneCartridges() throws Throwable {
		// pre-conditions
		// operation
		final List<IStandaloneCartridge> cartridges = connection.getStandaloneCartridges(true);
		// verifications
		assertThat(cartridges)
				.hasSize(15)
				.onProperty("name")
				.contains(CartridgeTestUtils.ZEND_56_NAME);
	}
	
	@Test
	public void shouldListEmbeddableCartridges() throws Throwable {
		// pre-conditions
		// operation
		final List<IEmbeddableCartridge> cartridges = connection.getEmbeddableCartridges();
		// verifications
		assertThat(cartridges)
				.hasSize(10) // // 11 - 1 obsolete (metrics-0.1)
				.onProperty("name")
				.excludes(CartridgeTestUtils.NODEJS_06_NAME
						, CartridgeTestUtils.JBOSSAS_7_NAME
						, CartridgeTestUtils.JBOSSEAP_6_NAME
						, CartridgeTestUtils.JBOSSEWS_1_NAME
						, CartridgeTestUtils.JBOSSEWS_2_NAME)
				.contains(CartridgeTestUtils.MONGODB_22_NAME
						, CartridgeTestUtils.MYSQL_51_NAME
						, CartridgeTestUtils.SWITCHYARD_06_NAME);
	}

	public void shouldNotListObsoleteEmbeddableCartridges() throws Throwable {
		// pre-conditions
		// operation
		final List<IStandaloneCartridge> cartridges = connection.getStandaloneCartridges();
		// verifications
		assertThat(cartridges)
				.hasSize(15)
				.onProperty("name")
				.contains(CartridgeTestUtils.MYSQL_55_NAME)
				.excludes(CartridgeTestUtils.METRICS_01_NAME);
	}
	
	public void shouldListObsoleteEmbeddableCartridges() throws Throwable {
		// pre-conditions
		// operation
		final List<IStandaloneCartridge> cartridges = connection.getStandaloneCartridges(true);
		// verifications
		assertThat(cartridges)
				.hasSize(15)
				.onProperty("name")
				.contains(CartridgeTestUtils.METRICS_01_NAME);
	}
	
	@Test
	public void shouldListCartridges() throws Throwable {
		// pre-conditions
		// operation
		final List<ICartridge> cartridges = connection.getCartridges();
		// verifications
		assertThat(cartridges)
				.hasSize(25) // 26 - 1 obsolete
				.onProperty("name")
				.contains(CartridgeTestUtils.NODEJS_06_NAME
						, CartridgeTestUtils.JBOSSAS_7_NAME
						, CartridgeTestUtils.JBOSSEAP_6_NAME
						, CartridgeTestUtils.JBOSSEWS_1_NAME
						, CartridgeTestUtils.JBOSSEWS_2_NAME
						, CartridgeTestUtils.MONGODB_22_NAME
						, CartridgeTestUtils.MYSQL_51_NAME
						, CartridgeTestUtils.SWITCHYARD_06_NAME);
	}

	@Test
	public void shouldNotListObsoleteCartridges() throws Throwable {
		// pre-conditions
		// operation
		final List<ICartridge> cartridges = connection.getCartridges();
		// verifications
		assertThat(cartridges)
				.hasSize(25) // 26 - 1 obsolete (zend-5.6)
				.onProperty("name")
				.excludes(CartridgeTestUtils.METRICS_01_NAME
						, CartridgeTestUtils.ZEND_56_NAME);
	}

	@Test
	public void shouldListObsoleteCartridges() throws Throwable {
		// pre-conditions
		// operation
		final List<ICartridge> cartridges = connection.getCartridges(true);
		// verifications
		assertThat(cartridges)
				.hasSize(27) // includes obsolete zend-5.6
				.onProperty("name")
				.contains(CartridgeTestUtils.METRICS_01_NAME
						, CartridgeTestUtils.ZEND_56_NAME);
	}
}
