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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.openshift.client.IEmbeddableCartridge;
import com.openshift.client.IEmbeddableCartridgeConstraint;
import com.openshift.client.IEmbeddedCartridge;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.OpenShiftException;

/**
 * @author Andre Dietisheim
 */
public class EmbeddableCartridgeNameConstraintTest {

	@Test
	public void shouldEqualsOtherCartridgeConstraint() {
		// pre-coniditions
		// operation
		// verification
		assertEquals(new EmbeddableCartridgeNameConstraint("redhat"),
				new EmbeddableCartridgeNameConstraint("redhat"));
		assertFalse(new EmbeddableCartridgeNameConstraint("redhat").equals(
				new EmbeddableCartridgeNameConstraint("jboss")));
	}

	@Test(expected=OpenShiftException.class)
	public void shouldThrowExceptionWhenNoMatch() {
		// pre-coniditions
		IOpenShiftConnection connection = createConnectionMock(new IEmbeddableCartridge[] { 
				createEmbeddedCartridgeMock("mongo"),
				createEmbeddedCartridgeMock("mysql")
		});
		IEmbeddableCartridgeConstraint cartridgeConstraint = new EmbeddableCartridgeNameConstraint("nonexistant");

		// operation
		cartridgeConstraint.getEmbeddableCartridges(connection);

		// verification
	}

	@Test
	public void shouldMatchMysql() {
		// pre-coniditions
		String mysqlCartridgeName = "mysql-5.1";
		IOpenShiftConnection connection = createConnectionMock(new IEmbeddableCartridge[] { 
				createEmbeddedCartridgeMock(mysqlCartridgeName)
		});
		IEmbeddableCartridgeConstraint cartridgeConstraint = new EmbeddableCartridgeNameConstraint("mysql");

		// operation
		List<IEmbeddableCartridge> embeddableCartridges = cartridgeConstraint.getEmbeddableCartridges(connection);

		// verification
		assertThat(embeddableCartridges.size()).isEqualTo(1);
		assertThat(embeddableCartridges.get(0).getName()).isEqualTo(mysqlCartridgeName);
	}

	@Test
	public void shouldMatchLatestMysql() {
		// pre-coniditions
		String mysql51Name = "mysql-5.1";
		IOpenShiftConnection connection = createConnectionMock(new IEmbeddableCartridge[] { 
				createEmbeddedCartridgeMock(mysql51Name),
				createEmbeddedCartridgeMock("mysql-5.0")
		});
		IEmbeddableCartridgeConstraint cartridgeConstraint = new EmbeddableCartridgeNameConstraint("mysql");

		// operation
		List<IEmbeddableCartridge> embeddableCartridges = cartridgeConstraint.getEmbeddableCartridges(connection);

		// verification
		assertThat(embeddableCartridges.size()).isEqualTo(1);
		assertThat(embeddableCartridges.get(0).getName()).isEqualTo(mysql51Name);
	}

	@Test
	public void shouldMatchMajorVersionedCartridge() {
		// pre-coniditions
		String cartridgeName = "jbossas-7";
		IOpenShiftConnection connection = createConnectionMock(new IEmbeddableCartridge[] { 
				createEmbeddedCartridgeMock(cartridgeName),
				createEmbeddedCartridgeMock("mysql")
		});
		IEmbeddableCartridgeConstraint cartridgeConstraint = new EmbeddableCartridgeNameConstraint("jboss");

		// operation
		List<IEmbeddableCartridge> embeddableCartridges = cartridgeConstraint.getEmbeddableCartridges(connection);

		// verification
		assertThat(embeddableCartridges.size()).isEqualTo(1);
		assertThat(embeddableCartridges.get(0).getName()).isEqualTo(cartridgeName);
	}

	@Test
	public void shouldMatchAlphanumericVersionedCartridge() {
		// pre-coniditions
		String cartridgeName = "somecartridge-7b";
		IOpenShiftConnection connection = createConnectionMock(new IEmbeddableCartridge[] { 
				createEmbeddedCartridgeMock(cartridgeName),
				createEmbeddedCartridgeMock("mysql-5.0")
		});
		IEmbeddableCartridgeConstraint cartridgeConstraint = new EmbeddableCartridgeNameConstraint("some");

		// operation
		List<IEmbeddableCartridge> embeddableCartridges = cartridgeConstraint.getEmbeddableCartridges(connection);

		// verification
		assertThat(embeddableCartridges.size()).isEqualTo(1);
		assertThat(embeddableCartridges.get(0).getName()).isEqualTo(cartridgeName);
	}

	private IOpenShiftConnection createConnectionMock(IEmbeddableCartridge[] embeddableCartridges) {
		IOpenShiftConnection connectionMock = mock(IOpenShiftConnection.class);
		when(connectionMock.getEmbeddableCartridges()).thenReturn(Arrays.asList(embeddableCartridges));
		return connectionMock;
	}

	private IEmbeddedCartridge createEmbeddedCartridgeMock(String name) {
		IEmbeddedCartridge mock = mock(IEmbeddedCartridge.class);
		when(mock.getName()).thenReturn(name);
		return mock;
	}
}
