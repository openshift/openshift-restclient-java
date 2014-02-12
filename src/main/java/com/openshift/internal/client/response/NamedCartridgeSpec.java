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
package com.openshift.internal.client.response;

import java.util.Collections;
import java.util.List;

import com.openshift.client.cartridge.ICartridge;
import com.openshift.client.cartridge.query.ICartridgeQuery;
import com.openshift.internal.client.cartridge.BaseCartridge;

/**
 * @author Andre Dietisheim
 */
public class NamedCartridgeSpec implements ICartridgeQuery {

	private final ICartridge cartridge;

	protected NamedCartridgeSpec(String name) {
		this.cartridge = new BaseCartridge(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <C extends ICartridge> List<C> getAll(List<C> cartridges) {
		return (List<C>) Collections.singletonList(cartridge);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <C extends ICartridge> C get(List<C> cartridges) {
		return (C) cartridge;
	}

	@Override
	public <C extends ICartridge> boolean matches(C cartridge) {
		return this.cartridge.equals(cartridge);
	}

	@Override
	public String toString() {
		return "NamedCartridgeSpec [cartridge=" + cartridge + "]";
	}
}