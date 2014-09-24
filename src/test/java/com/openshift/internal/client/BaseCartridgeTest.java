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
package com.openshift.internal.client;

import static org.fest.assertions.Assertions.assertThat;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import com.openshift.client.cartridge.ICartridge;
import com.openshift.client.utils.CartridgeAssert;
import com.openshift.client.utils.CartridgeTestUtils;
import com.openshift.internal.client.cartridge.BaseCartridge;

/**
 * @author Andre Dietisheim
 */
public class BaseCartridgeTest extends TestTimer {

	@Test
	public void shouldNonDownloadableEqualsNonDownloadable() {
		// pre-coniditions
		// operation
		// verification
		assertThat(new BaseCartridge("redhat"))
				.isEqualTo(new BaseCartridge("redhat"));
		assertThat(new BaseCartridge("redhat"))
				.isNotEqualTo(new BaseCartridge("jboss"));
	}

	@Test
	public void shouldDownloadableEqualsDownloadable() throws MalformedURLException {
		// pre-coniditions
		// operation
		// verification
		assertThat(new BaseCartridge(new URL(CartridgeTestUtils.GO_URL)))
				.isEqualTo(new BaseCartridge(new URL(CartridgeTestUtils.GO_URL)));
	}
	
	@Test
	public void shouldDownloadableNotEqualsDownloadableWithDifferentUrl() throws MalformedURLException {
		// pre-coniditions
		// operation
		// verification
		assertThat(new BaseCartridge(new URL(CartridgeTestUtils.GO_URL)))
				.isNotEqualTo(new BaseCartridge(new URL(CartridgeTestUtils.FOREMAN_URL)));
	}

	@Test
	public void shouldNotHaveUrlInNonDownloadableCartridge() throws Throwable {
		// pre-conditions

		// operation
		ICartridge mysql = new BaseCartridge(CartridgeTestUtils.MYSQL_51_NAME);

		// verifications
		new CartridgeAssert<ICartridge>(mysql)
				.hasNoUrl();
	}

	@Test
	public void shouldHaveDisplayNameInDownloadableCartridgeWithAnchor() throws Throwable {
		// pre-conditions
		ICartridge aerogear = CartridgeTestUtils.aerogear();
		// operation

		
		// verifications
		assertThat(aerogear.getDisplayName()).isEqualTo("AeroGear");
	}

	@Test
	public void shouldNotOverrideDisplayNameWithAnchor() throws Throwable {
		// pre-conditions
		String name = "redhat";
		String displayName = "Red Hat OpenShift Cartridge";
		// operation
		ICartridge redhat = new CartridgeFake(name, new URL(CartridgeTestUtils.WILDFLY_URL), displayName);
		
		// verifications
		assertThat(redhat.getDisplayName()).isEqualTo(displayName);
		assertThat(redhat.getName()).isEqualTo(name);
	}

	@Test
	public void shouldNotBeDownloadableCartridge() throws Throwable {
		// pre-conditions
		ICartridge jbossAsCartridge = new BaseCartridge("jboss-7");
		ICartridge jbossEapCartridge = new BaseCartridge("jbosseap-6");

		// operation
		// verifications
		assertThat(jbossAsCartridge.isDownloadable()).isFalse();
		assertThat(jbossAsCartridge.getUrl()).isNull();
		assertThat(jbossEapCartridge.isDownloadable()).isFalse();
		assertThat(jbossEapCartridge.getUrl()).isNull();
	}

	@Test
	public void shouldBeDownloadableCartridges() throws Throwable {
		// pre-conditions
		// operation
		// verifications
		assertThat(CartridgeTestUtils.go11().isDownloadable()).isTrue();
		assertThat(CartridgeTestUtils.foreman063().isDownloadable()).isTrue();
	}
	
	@Test
	public void shouldBeObsoleteCartridges() throws Throwable {
		// pre-conditions
		// operation
		// verifications
		assertThat(CartridgeTestUtils.createObsoleteStandaloneCartridge(false).isObsolete()).isFalse();
		assertThat(CartridgeTestUtils.createObsoleteStandaloneCartridge(true).isObsolete()).isTrue();
		assertThat(CartridgeTestUtils.createObsoleteEmbeddableCartridge(false).isObsolete()).isFalse();
		assertThat(CartridgeTestUtils.createObsoleteEmbeddableCartridge(true).isObsolete()).isTrue();
	}
	
	private class CartridgeFake extends BaseCartridge {

		CartridgeFake(String name, URL url, String displayName) {
			super(name, url, displayName, null);
		}
		
	}
	
}
