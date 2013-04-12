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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.openshift.client.cartridge.EmbeddableCartridge;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.IEmbeddedCartridge;
import com.openshift.client.cartridge.selector.LatestEmbeddableCartridge;
import com.openshift.client.cartridge.selector.LatestVersionSelector;

/**
 * @author Andre Dietisheim
 */
public class LatestVersionSelectorTest {

	@Test
	public void shouldEqualsOtherCartridgeConstraint() {
		// pre-coniditions
		// operation
		// verification
		assertEquals(
				new LatestVersionSelector("redhat"),
				new LatestVersionSelector("redhat"));
		assertFalse(
				new LatestVersionSelector("redhat").equals(
				new LatestVersionSelector("jboss")));
	}

	@Test
	public void shouldMatchEmbeddableCartridge() {
		// pre-coniditions
		LatestVersionSelector redhatSelector = new LatestVersionSelector("redhat");
		LatestVersionSelector jbossSelector = new LatestVersionSelector("jboss");
		IEmbeddableCartridge redhat10 = new EmbeddableCartridge("redhat", "1.0");
		IEmbeddableCartridge redhat30 = new EmbeddableCartridge("redhat", "3.0");
		IEmbeddableCartridge jboss10 = new EmbeddableCartridge("jboss", "1.0");
		LatestVersionSelector closedSourceSelector = new LatestVersionSelector("closedsource");
		// operation
		// verification
		assertTrue(redhatSelector.matches(redhat10));
		assertTrue(redhatSelector.matches(redhat30));
		assertFalse(redhatSelector.matches(jboss10));
		assertTrue(jbossSelector.matches(jboss10));
		assertFalse(closedSourceSelector.matches(jboss10));
	}

	@Test
	public void shouldReturnEmptyListOnNoCartridges() {
		// pre-coniditions
		List<IEmbeddedCartridge> embeddedCartridges = Collections.emptyList();
		
		LatestVersionSelector selector = new LatestVersionSelector("mysql");

		// operation
		IEmbeddedCartridge matchingCartridge = selector.get(embeddedCartridges);

		// verification
		assertThat(matchingCartridge).isNull();
	}

	@Test
	public void shouldReturnEmptyListOnNoMatchingCartridge() {
		// pre-coniditions
		List<IEmbeddedCartridge> embeddedCartridges = Arrays.asList(
				createEmbeddedCartridgeMock("eclipsecon-2013"),
				createEmbeddedCartridgeMock("community")
		);
		
		LatestVersionSelector selector = new LatestVersionSelector("fun");

		// operation
		IEmbeddedCartridge matchingCartridge = selector.get(embeddedCartridges);

		// verification
		assertThat(matchingCartridge).isNull();
	}

	@Test
	public void shouldMatchMysql() {
		// pre-coniditions
		String mysqlCartridgeName = "mysql-5.1";
		List<IEmbeddedCartridge> embeddedCartridges = Arrays.asList(createEmbeddedCartridgeMock(mysqlCartridgeName));
		LatestVersionSelector cartridgeConstraint = new LatestVersionSelector("mysql");

		// operation
		IEmbeddedCartridge matchingCartridge = cartridgeConstraint.get(embeddedCartridges);

		// verification
		assertThat(matchingCartridge).isNotNull();
		assertThat(matchingCartridge.getName()).isEqualTo(mysqlCartridgeName);
	}

	@Test
	public void shouldMatchLatestMysql() {
		// pre-coniditions
		String mysql51Name = "mysql-5.1";
		List<IEmbeddedCartridge> embeddedCartridges = Arrays.asList(
				createEmbeddedCartridgeMock(mysql51Name),
				createEmbeddedCartridgeMock("mysql-5.0")
		);

		LatestVersionSelector cartridgeSelector = new LatestVersionSelector("mysql");

		// operation
		IEmbeddedCartridge latestMysql = cartridgeSelector.get(embeddedCartridges);

		// verification
		assertThat(latestMysql).isNotNull();
		assertThat(latestMysql.getName()).isEqualTo(mysql51Name);
	}

	@Test
	public void shouldMatchMajorVersionedCartridge() {
		// pre-coniditions
		String cartridgeName = "vertx-2";
		List<IEmbeddedCartridge> embeddedCartridges = Arrays.asList(
				createEmbeddedCartridgeMock(cartridgeName),
				createEmbeddedCartridgeMock("mysql")
		);
		LatestEmbeddableCartridge constraint = new LatestEmbeddableCartridge("vertx");

		// operation
		IEmbeddedCartridge cartridge = constraint.get(embeddedCartridges);

		// verification
		assertThat(cartridge).isNotNull();
		assertThat(cartridge.getName()).isEqualTo(cartridgeName);
	}

	@Test
	public void shouldMatchLaterMajorVersion() {
		// pre-coniditions
		String mysql6Name = "mysql-6";
		List<IEmbeddedCartridge> embeddedCartridges = Arrays.asList(
				createEmbeddedCartridgeMock(mysql6Name),
				createEmbeddedCartridgeMock("mysql-5.0")
		);

		LatestVersionSelector cartridgeSelector = new LatestVersionSelector("mysql");

		// operation
		IEmbeddedCartridge latestMysql = cartridgeSelector.get(embeddedCartridges);

		// verification
		assertThat(latestMysql).isNotNull();
		assertThat(latestMysql.getName()).isEqualTo(mysql6Name);
	}

	@Test
	public void shouldMatchAlphanumericVersionedCartridge() {
		// pre-coniditions
		String cartridgeName = "somecartridge-7b";
		List<IEmbeddedCartridge> embeddedCartridges = Arrays.asList(
				createEmbeddedCartridgeMock(cartridgeName),
				createEmbeddedCartridgeMock("mysql-5.0")
		);
		LatestVersionSelector constraint = new LatestVersionSelector("somecartridge");

		// operation
		IEmbeddedCartridge cartridge = constraint.get(embeddedCartridges);

		// verification
		assertThat(cartridge).isNotNull();
		assertThat(cartridge.getName()).isEqualTo(cartridgeName);
	}

	@Test
	public void shouldMatchCartridgeNameWithDash() {
		// pre-coniditions
		String jenkins2 = "jenkins-client-2.0";
		List<IEmbeddedCartridge> embeddedCartridges = Arrays.asList(
				createEmbeddedCartridgeMock(jenkins2),
				createEmbeddedCartridgeMock("jenkins-client-1.4")
		);
		LatestVersionSelector constraint = new LatestVersionSelector("jenkins-client");

		// operation
		IEmbeddedCartridge cartridge = constraint.get(embeddedCartridges);

		// verification
		assertThat(cartridge).isNotNull();
		assertThat(cartridge.getName()).isEqualTo(jenkins2);
	}

	private IEmbeddedCartridge createEmbeddedCartridgeMock(String name) {
		IEmbeddedCartridge mock = mock(IEmbeddedCartridge.class);
		when(mock.getName()).thenReturn(name);
		return mock;
	}
}
