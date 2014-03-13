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
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.utils.Cartridges;
import com.openshift.client.utils.TestConnectionFactory;

/**
 * @author Xavier Coulon
 * @author Andre Dietisheim
 */
public class APIResourceTest extends TestTimer {

	private IOpenShiftConnection connection;

	@Before
	public void setup() throws Throwable {
		connection = new TestConnectionFactory().getConnection(new HttpClientMockDirector().client());
	}

	@Test
	public void shouldLoadListOfStandaloneCartridges() throws Throwable {
		// pre-conditions
		// operation
		final List<IStandaloneCartridge> cartridges = connection.getStandaloneCartridges();
		// verifications
		assertThat(cartridges)
				.hasSize(15)
				.onProperty("name")
				.contains(Cartridges.NODEJS_06_NAME
						, Cartridges.JBOSSAS_7_NAME
						, Cartridges.JBOSSEAP_6_NAME
						, Cartridges.JBOSSEWS_1_NAME
						, Cartridges.JBOSSEWS_2_NAME)
				.excludes(Cartridges.MONGODB_22_NAME
						, Cartridges.MYSQL_51_NAME
						, Cartridges.SWITCHYARD_06_NAME);
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
				.excludes(Cartridges.NODEJS_06_NAME
						, Cartridges.JBOSSAS_7_NAME
						, Cartridges.JBOSSEAP_6_NAME
						, Cartridges.JBOSSEWS_1_NAME
						, Cartridges.JBOSSEWS_2_NAME)
				.contains(Cartridges.MONGODB_22_NAME
						, Cartridges.MYSQL_51_NAME
						, Cartridges.SWITCHYARD_06_NAME);
	}
}
