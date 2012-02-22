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
package com.openshift.express.internal.client.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.text.MessageFormat;
import java.util.List;

import com.openshift.express.client.IEmbeddableCartridge;
import com.openshift.express.client.OpenShiftException;

/**
 * @author André Dietisheim
 */
public class EmbeddableCartridgeAsserts {

	public static void assertThatContainsCartridge(String embeddableCartridgeName, String url, List<IEmbeddableCartridge> embeddableCartridges) throws OpenShiftException {
		IEmbeddableCartridge cartridge = getEmbeddableCartridge(embeddableCartridgeName, embeddableCartridges);
		if (cartridge == null) {
			fail(MessageFormat.format("Could not find embeddable cartridge with name \"{0}\"", embeddableCartridgeName));
		}
		assertEmbeddableCartridge(embeddableCartridgeName, url, cartridge);
	}

	public static void assertEmbeddableCartridge(String name, String creationLog, IEmbeddableCartridge cartridge) {
		assertNotNull(cartridge);
		assertEquals(name, cartridge.getName());
		assertEquals(creationLog, cartridge.getCreationLog());
	}

	public static void assertThatContainsCartridge(String applicationName, List<IEmbeddableCartridge> cartridges) {
		assertNotNull(getEmbeddableCartridge(applicationName, cartridges));
	}

	private static IEmbeddableCartridge getEmbeddableCartridge(String name, List<IEmbeddableCartridge> cartridges) {
		IEmbeddableCartridge matchingCartridge = null;
		for (IEmbeddableCartridge cartridge : cartridges) {
			if (name.equals(cartridge.getName())) {
				matchingCartridge = cartridge;
				break;
			}
		}
		return matchingCartridge;
	}

}
