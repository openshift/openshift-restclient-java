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

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.cartridge.ICartridge;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.utils.CartridgeTestUtils;
import com.openshift.client.utils.TestConnectionFactory;

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
		connection = new TestConnectionFactory().getConnection(mockDirector.client());
	}

	@Test
	public void shouldListStandaloneCartridges() throws Throwable {
		// pre-conditions
		// operation
		final List<IStandaloneCartridge> cartridges = connection.getStandaloneCartridges();
		// verifications
		assertThat(cartridges)
				.hasSize(15)
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

	@Test
	public void shouldListEmbeddableCartridges() throws Throwable {
		// pre-conditions
		// operation
		final List<IEmbeddableCartridge> cartridges = connection.getEmbeddableCartridges();
		// verifications
		assertThat(cartridges)
				.hasSize(11)
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

	@Test
	public void shouldListCartridges() throws Throwable {
		// pre-conditions
		// operation
		final List<ICartridge> cartridges = connection.getCartridges();
		// verifications
		assertThat(cartridges)
				.hasSize(26)
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
}
