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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.text.MessageFormat;
import java.util.List;

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

	public static void assertThatContainsCartridge(String applicationName, List<IEmbeddedCartridge> cartridges) {
		assertNotNull(getEmbeddableCartridge(applicationName, cartridges));
	}

	public static void assertThatDoesntContainsCartridge(String applicationName, List<IEmbeddedCartridge> cartridges) {
		assertNull(getEmbeddableCartridge(applicationName, cartridges));
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

}
