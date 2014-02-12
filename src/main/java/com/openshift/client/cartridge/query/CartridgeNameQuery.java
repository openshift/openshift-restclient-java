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

import java.util.regex.Pattern;

import com.openshift.client.cartridge.ICartridge;

/**
 * A query that returns the cartridges whose name match the given pattern.
 * 
 * @author André Dietisheim
 */
public class CartridgeNameQuery extends AbstractCartridgeQuery {

	private Pattern namePattern;

	public CartridgeNameQuery(String namePattern) {
		this(Pattern.compile(namePattern));
	}

	public CartridgeNameQuery(Pattern namePattern) {
		this.namePattern = namePattern;
	}

	@Override
	public <C extends ICartridge> boolean matches(C cartridge) {
		if (cartridge == null) {
			return false;
		}
		return namePattern.matcher(cartridge.getName()).matches();
	}

	@Override
	public String toString() {
		return "CartridgeNameQuery ["
				+ "namePattern=" + namePattern 
				+ "]";
	}

}