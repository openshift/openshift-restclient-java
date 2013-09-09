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

import static org.fest.assertions.Assertions.assertThat;

import java.net.URISyntaxException;

import org.fest.assertions.AssertExtension;

import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.ICartridge;

/**
 * @author André Dietisheim
 */
public abstract class AbstractCartridgeAssert<C extends ICartridge> implements AssertExtension {

	private C cartridge;

	protected AbstractCartridgeAssert(C cartridge) {
		assertThat(cartridge).isNotNull();
		this.cartridge = cartridge;
	}

	public AbstractCartridgeAssert<C> hasName(String name) throws OpenShiftException, URISyntaxException {
		assertThat(cartridge.getName()).isEqualTo(name);
		return this;
	}

	public AbstractCartridgeAssert<C> hasName() throws OpenShiftException, URISyntaxException {
		assertThat(cartridge.getName()).isNotEmpty();
		return this;
	}

	public AbstractCartridgeAssert<C> hasDisplayName(String displayName) throws OpenShiftException, URISyntaxException {
		assertThat(cartridge.getDisplayName()).isEqualTo(displayName);
		return this;
	}

	public AbstractCartridgeAssert<C> hasDisplayName() throws OpenShiftException, URISyntaxException {
		assertThat(cartridge.getDisplayName()).isNotEmpty();
		return this;
	}

	public AbstractCartridgeAssert<C> hasDescription(String description) throws OpenShiftException, URISyntaxException {
		assertThat(cartridge.getDescription()).isEqualTo(description);
		return this;
	}

	public AbstractCartridgeAssert<C> hasDescription() throws OpenShiftException, URISyntaxException {
		assertThat(cartridge.getDescription()).isNotNull();
		return this;
	}

	public AbstractCartridgeAssert<C> isEqualsTo(C otherCartridge) throws OpenShiftException, URISyntaxException {
		assertThat(otherCartridge).isNotNull();
		hasName(otherCartridge.getName());
		hasDescription(otherCartridge.getDescription());
		hasDisplayName(otherCartridge.getDisplayName());
		assertThat(cartridge.equals(otherCartridge));
		return this;
	}
	
	protected C getCartridge() {
		return cartridge;
	}
}
