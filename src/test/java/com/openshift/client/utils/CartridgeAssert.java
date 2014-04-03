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

import java.net.URL;

import org.fest.assertions.AssertExtension;

import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.ICartridge;
import com.openshift.internal.client.cartridge.BaseCartridge;

/**
 * @author André Dietisheim
 */
public class CartridgeAssert<C extends ICartridge> implements AssertExtension {

	private C cartridge;

	public CartridgeAssert(C cartridge) {
		assertThat(cartridge).isNotNull();
		this.cartridge = cartridge;
	}

	public CartridgeAssert<C> hasName(String name) throws OpenShiftException {
		assertThat(cartridge.getName()).isEqualTo(name);
		return this;
	}

	public CartridgeAssert<C> hasName() throws OpenShiftException {
		assertThat(cartridge.getName()).isNotEmpty();
		return this;
	}
	
	public CartridgeAssert<C> nameStartWith(String namePrefix) throws OpenShiftException {
		assertThat(cartridge.getName()).startsWith(namePrefix);
		return this;
	}

	public CartridgeAssert<C> hasDisplayName(String displayName) throws OpenShiftException {
		assertThat(cartridge.getDisplayName()).isEqualTo(displayName);
		return this;
	}

	public CartridgeAssert<C> hasDisplayName() throws OpenShiftException {
		assertThat(cartridge.getDisplayName()).isNotEmpty();
		return this;
	}

	public CartridgeAssert<C> hasDescription(String description) throws OpenShiftException {
		assertThat(cartridge.getDescription()).isEqualTo(description);
		return this;
	}

	public CartridgeAssert<C> hasDescription() throws OpenShiftException {
		assertThat(cartridge.getDescription()).isNotNull();
		return this;
	}

	public CartridgeAssert<C> hasNoUrl() throws OpenShiftException {
		assertThat(cartridge.getUrl()).isNull();
		return this;
	}

	public CartridgeAssert<C> hasUrl() throws OpenShiftException {
		assertThat(cartridge.getUrl()).isNotNull();
		return this;
	}
	
	public CartridgeAssert<C> isDownloadable() throws OpenShiftException {
		assertThat(cartridge.isDownloadable()).isTrue();
		return this;
	}

	public CartridgeAssert<C> hasUrl(String url) throws OpenShiftException {
		URL cartridgeUrl = cartridge.getUrl();
		if (cartridgeUrl == null) {
			assertThat(cartridgeUrl).isEqualTo(url);
		} else {
			assertThat(cartridgeUrl.toString()).isEqualTo(url);
		}
		return this;
	}

	public CartridgeAssert<C> isEqualsTo(BaseCartridge otherCartridge) throws OpenShiftException {
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
