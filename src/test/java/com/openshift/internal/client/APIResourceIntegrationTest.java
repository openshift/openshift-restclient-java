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

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.utils.TestConnectionFactory;

/**
 * @author André Dietisheim
 */
public class APIResourceIntegrationTest {

	private IOpenShiftConnection connection;

	@Before
	public void setUp() throws OpenShiftException, IOException {
		this.connection = new TestConnectionFactory().getConnection();
	}

	@Test
	public void shouldListEmbeddableCartridges() throws OpenShiftException {
		// pre-condition
		
		// operation
		final List<IEmbeddableCartridge> cartridges = connection.getEmbeddableCartridges();

		// verification
		assertThat(cartridges).isNotEmpty();
	}

	@Test
	public void shouldListStandaloneCartridges() throws OpenShiftException {
		// pre-condition
		
		// operation
		final List<IStandaloneCartridge> cartridges = connection.getStandaloneCartridges();

		// verification
		assertThat(cartridges).isNotEmpty();
	}

	@Test
	public void shouldHaveDescriptionInAvailableEmbeddableCartridges() throws OpenShiftException {
		// pre-condition

		// operation
		final List<IEmbeddableCartridge> cartridges = connection.getEmbeddableCartridges();
		
		// verification
		assertThat(cartridges).onProperty("description").isNotEmpty();
	}

	@Test
	public void shouldHaveDescriptionInAvailableStandaloneCartridges() throws OpenShiftException {
		// pre-condition

		// operation
		final List<IStandaloneCartridge> cartridges = connection.getStandaloneCartridges();
		
		// verification
		assertThat(cartridges).onProperty("description").isNotEmpty();
	}

	@Test
	public void shouldHaveDisplayNameInAvailableEmbeddableCartridges() throws OpenShiftException {
		// pre-condition

		// operation
		final List<IEmbeddableCartridge> cartridges = connection.getEmbeddableCartridges();
		
		// verification
		assertThat(cartridges).onProperty("displayName").isNotEmpty();
	}

	@Test
	public void shouldHaveDisplayNameInAvailableStandaloneCartridges() throws OpenShiftException {
		// pre-condition

		// operation
		final List<IStandaloneCartridge> cartridges = connection.getStandaloneCartridges();
		
		// verification
		assertThat(cartridges).onProperty("displayName").isNotEmpty();
	}

}
