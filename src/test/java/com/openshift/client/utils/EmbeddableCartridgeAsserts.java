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
package com.openshift.client.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

import com.openshift.client.IEmbeddableCartridge;
import com.openshift.client.IEmbeddedCartridge;
import com.openshift.client.OpenShiftException;

/**
 * @author André Dietisheim
 */
public class EmbeddableCartridgeAsserts {

	public static void assertThatContainsCartridge(String embeddableCartridgeName, String url, List<IEmbeddedCartridge> embeddableCartridges) throws OpenShiftException {
		IEmbeddedCartridge cartridge = getEmbeddableCartridge(embeddableCartridgeName, embeddableCartridges);
		if (cartridge == null) {
			fail(MessageFormat.format("Could not find embeddable cartridge with name \"{0}\"", embeddableCartridgeName));
		}
		assertEmbeddableCartridge(embeddableCartridgeName, url, cartridge);
	}

	public static void assertEmbeddableCartridge(String name, String creationLog, IEmbeddedCartridge cartridge) {
		assertNotNull(cartridge);
		assertEquals(name, cartridge.getName());
		assertEquals(creationLog, cartridge.getCreationLog());
	}

	public static void assertThatContainsCartridge(IEmbeddableCartridge cartridge, List<IEmbeddedCartridge> cartridges) {
		assertThatContainsCartridge(cartridge.getName(), cartridges);
	}

	public static void assertThatContainsCartridge(String name, List<IEmbeddedCartridge> cartridges) {
		assertNotNull(getEmbeddableCartridge(name, cartridges));
	}

	public static void assertThatDoesntContainCartridge(IEmbeddableCartridge cartridge, List<IEmbeddedCartridge> cartridges) {
		assertThatDoesntContainCartridge(cartridge.getName(), cartridges);
	}

	public static void assertThatDoesntContainCartridge(String name, List<IEmbeddedCartridge> cartridges) {
		assertNull(getEmbeddableCartridge(name, cartridges));
	}

	public static void assertThatDoesntContainCartridges(Collection<IEmbeddableCartridge> shouldNotBeContained, List<IEmbeddedCartridge> cartridges) {
		for(IEmbeddableCartridge shouldNot : shouldNotBeContained) {
			assertFalse(cartridges.contains(shouldNot));
		}
	}

	private static IEmbeddedCartridge getEmbeddableCartridge(String name, List<IEmbeddedCartridge> cartridges) {
		IEmbeddedCartridge matchingCartridge = null;
		for (IEmbeddedCartridge cartridge : cartridges) {
			if (name.equals(cartridge.getName())) {
				matchingCartridge = cartridge;
				break;
			}
		}
		return matchingCartridge;
	}

	public static void assertThatContainsCartridges(Collection<IEmbeddableCartridge> shouldBeContained, List<IEmbeddedCartridge> cartridgesToCheck) {
		for (IEmbeddableCartridge cartridge : shouldBeContained) {
			assertTrue(cartridgesToCheck.contains(cartridge));
		}
	}

}
