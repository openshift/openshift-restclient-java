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
package org.jboss.tools.openshift.express.internal.client.test.utils;

import static org.junit.Assert.fail;

import java.text.MessageFormat;
import java.util.Collection;

import org.jboss.tools.openshift.express.client.ICartridge;

/**
 * @author André Dietisheim
 */
public class CartridgeAsserts {

	public static void assertThatContainsCartridge(String cartridgeName, Collection<ICartridge> cartridges) {
		boolean found = false;
		for (ICartridge cartridge : cartridges) {
			if (cartridgeName.equals(cartridge.getName())) {
				found = true;
				break;
			}
		}
		if (!found) {
			fail(MessageFormat.format("Could not find cartridge with name \"{0}\"", cartridgeName));
		}
	}
}
