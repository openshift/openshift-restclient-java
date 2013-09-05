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
package com.openshift.client.utils;

import java.util.ArrayList;
import java.util.List;

import com.openshift.client.cartridge.ICartridge;

/**
 * @author Andre Dietisheim
 */
public class OpenShiftResourceUtils {

	public static List<String> toNames(ICartridge... cartridges) {
		List<String> cartridgeNames = new ArrayList<String>();
		if (cartridges == null) {
			return cartridgeNames;
		}
		for (ICartridge cartridge : cartridges) {
			if (cartridge == null) {
				continue;
			}
			cartridgeNames.add(cartridge.getName());
		}
		return cartridgeNames;
	}
}
