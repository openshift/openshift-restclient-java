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
import java.net.URL;

import org.fest.assertions.AssertExtension;

import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.IEmbeddedCartridge;
import com.openshift.client.cartridge.query.UrlPropertyQuery;
import com.openshift.internal.client.response.CartridgeResourceProperty;

/**
 * @author André Dietisheim
 */
public class EmbeddedCartridgeAssert implements AssertExtension {

	private IEmbeddedCartridge cartridge;

	public EmbeddedCartridgeAssert(IEmbeddedCartridge embeddedCartridge) {
		assertThat(embeddedCartridge).isNotNull();

		this.cartridge = embeddedCartridge;
	}

	public EmbeddedCartridgeAssert hasName(String name) throws OpenShiftException, URISyntaxException {
		assertThat(cartridge.getName()).isEqualTo(name);
		return this;
	}

	public EmbeddedCartridgeAssert hasName() throws OpenShiftException, URISyntaxException {
		assertThat(cartridge.getName()).isNotEmpty();
		return this;
	}

	public EmbeddedCartridgeAssert hasDisplayName(String displayName) throws OpenShiftException, URISyntaxException {
		assertThat(cartridge.getDisplayName()).isEqualTo(displayName);
		return this;
	}

	public EmbeddedCartridgeAssert hasDisplayName() throws OpenShiftException, URISyntaxException {
		assertThat(cartridge.getDisplayName()).isNotEmpty();
		return this;
	}

	public EmbeddedCartridgeAssert hasDescription(String description) throws OpenShiftException, URISyntaxException {
		assertThat(cartridge.getDescription()).isEqualTo(description);
		return this;
	}

	public EmbeddedCartridgeAssert hasDescription() throws OpenShiftException, URISyntaxException {
		assertThat(cartridge.getDescription()).isNotNull();
		return this;
	}

	public EmbeddedCartridgeAssert hasNoUrl() throws OpenShiftException {
		assertThat(cartridge.getUrl()).isNull();
		return this;
	}

	public EmbeddedCartridgeAssert hasUrl() throws OpenShiftException, URISyntaxException {
		assertThat(cartridge.getUrl()).isNotNull();
		return this;
	}

	public EmbeddedCartridgeAssert hasUrl(String url) throws OpenShiftException {
		URL cartridgeUrl = cartridge.getUrl();
		if (cartridgeUrl == null) {
			assertThat(cartridgeUrl).isEqualTo(url);
		} else {
			assertThat(cartridgeUrl.toString()).isEqualTo(url);
		}

		return this;
	}

	public EmbeddedCartridgeAssert hasMessages() {
		assertThat(cartridge.getMessages()).isNotNull();
		assertThat(cartridge.getMessages().getAll()).isNotEmpty();
		return this;
	}

	public EmbeddedCartridgeAssert hasUrlProperty() {
		CartridgeResourceProperty property = new UrlPropertyQuery().getMatchingProperty(cartridge);
		assertThat(property).isNotNull();
		assertThat(property.getValue()).isNotEmpty();
		return this;
	}

	public EmbeddedCartridgeAssert hasUrlProperty(String url) {
		CartridgeResourceProperty property = new UrlPropertyQuery().getMatchingProperty(cartridge);
		assertThat(property).isNotNull();
		assertThat(property.getValue()).isEqualTo(url);
		return this;
	}

}
