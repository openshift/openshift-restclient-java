/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client.cartridge.query;

import com.openshift.client.cartridge.ICartridge;
import com.openshift.internal.client.utils.Assert;

/**
 * A query that returns the cartridges whose name match the given pattern.
 * 
 * @author André Dietisheim
 */
public class CartridgeNameQuery extends AbstractCartridgeQuery {

	private String nameSubstring;

	public CartridgeNameQuery(String nameSubstring) {
		Assert.notEmpty(nameSubstring);
		this.nameSubstring = nameSubstring;
	}

	@Override
	public <C extends ICartridge> boolean matches(C cartridge) {
		if (cartridge == null
				|| cartridge.getName() == null) {
			return false;
		}
		return cartridge.getName().indexOf(nameSubstring) >= 0;
	}

	@Override
	public String toString() {
		return "CartridgeNameQuery ["
				+ "nameSubstring=" + nameSubstring
				+ "]";
	}
}