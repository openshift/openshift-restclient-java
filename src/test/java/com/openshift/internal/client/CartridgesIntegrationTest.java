/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
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
import com.openshift.client.utils.TestConnectionFactory;

/**
 * @author André Dietisheim
 */
public class CartridgesIntegrationTest {

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
}
