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
package com.openshift.internal.client;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.utils.ApplicationTestUtils;
import com.openshift.client.utils.DomainTestUtils;
import com.openshift.client.utils.StandaloneCartridgeAssert;
import com.openshift.client.utils.TestConnectionBuilder;

/**
 * @author André Dietisheim
 */
public class StandaloneCartridgesIntegrationTest extends TestTimer {

	private IUser user;
	private IDomain domain;
	private IApplication application;

	@Before
	public void setUp() throws OpenShiftException, IOException {
		this.user = new TestConnectionBuilder().defaultCredentials().disableSSLCertificateChecks().create().getUser();
		this.domain = DomainTestUtils.ensureHasDomain(user);
		this.application = ApplicationTestUtils.getOrCreateApplication(domain);
	}

	@Test
	public void shouldReportStandaloneCartridge() throws OpenShiftException, URISyntaxException {
		// precondition

		// operation
		IStandaloneCartridge cartridge = application.getCartridge();
		
		// verification
		assertThat(cartridge).isNotNull();
		assertThat(cartridge.getName()).isNotEmpty();
		IStandaloneCartridge availableCartridge = getAvailableCartridge(cartridge.getName());
		new StandaloneCartridgeAssert(cartridge).equals(availableCartridge);
	}

	private IStandaloneCartridge getAvailableCartridge(String name) {
		assertThat(name).isNotNull();
		for(IStandaloneCartridge cartridge : user.getConnection().getStandaloneCartridges()) {
			if (name.equals(cartridge.getName())) {
				return cartridge;
			}
		}
		return null;
	}
}
