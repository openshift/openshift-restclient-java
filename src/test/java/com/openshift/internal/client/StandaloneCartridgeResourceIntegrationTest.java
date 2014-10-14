/******************************************************************************* 
 * Copyright (c) 2011-2014 Red Hat, Inc. 
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
import org.junit.Ignore;
import org.junit.Test;

import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IGearGroup;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.IDeployedStandaloneCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.cartridge.query.CartridgeNameQuery;
import com.openshift.client.utils.ApplicationTestUtils;
import com.openshift.client.utils.DomainTestUtils;
import com.openshift.client.utils.StandaloneCartridgeAssert;
import com.openshift.client.utils.TestConnectionBuilder;

/**
 * @author André Dietisheim
 * @author Jeff Cantrill
 */
public class StandaloneCartridgeResourceIntegrationTest extends TestTimer {

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
		IStandaloneCartridge availableCartridge = new CartridgeNameQuery(cartridge.getName()).get(user.getConnection()
				.getStandaloneCartridges());
		new StandaloneCartridgeAssert(cartridge).equals(availableCartridge);
	}

	@Test
	public void shouldReportGearGroup() throws OpenShiftException, URISyntaxException {
		// precondition
		IDeployedStandaloneCartridge cartridge = application.getCartridge();
		assertThat(cartridge).isNotNull();

		// operation
		IGearGroup gearGroup = cartridge.getGearGroup();

		// verification
		assertThat(gearGroup).isNotNull();
		assertThat(gearGroup.getCartridges()).contains(cartridge);
	}

	@Test
	public void shouldGetGearStorage() throws OpenShiftException, URISyntaxException, IOException {
		// precondition
		IDeployedStandaloneCartridge cartridge = application.getCartridge();
		assertThat(cartridge).isNotNull();

		// operation
		int additionalGearStorage = cartridge.getAdditionalGearStorage();

		// verification
		// reload user info to ensure the storage info isnt cached
		assertThat(additionalGearStorage).isNotEqualTo(IGearGroup.NO_ADDITIONAL_GEAR_STORAGE);
	}

	@Ignore("This application is not allowed to have additional gear storage")
	@Test
	public void shouldSetGearStorage() throws OpenShiftException, URISyntaxException, IOException {
		// precondition
		IDeployedStandaloneCartridge cartridge = application.getCartridge();
		assertThat(cartridge).isNotNull();
		int newAdditionalGearStorage = 3;

		// operation
		cartridge.setAdditionalGearStorage(newAdditionalGearStorage);

		// verification
		// reload user info to ensure the storage info isnt cached
		assertThat(cartridge.getAdditionalGearStorage()).isEqualTo(newAdditionalGearStorage);
	}

	@Ignore("This application is not allowed to have additional gear storage")
	@Test
	public void shouldSeeNewAdditionalGearStorageInNewConnection() throws OpenShiftException, URISyntaxException,
			IOException {
		// precondition
		IDeployedStandaloneCartridge cartridge = application.getCartridge();
		assertThat(cartridge).isNotNull();
		int additionalGearStorage = 4;

		// operation
		cartridge.setAdditionalGearStorage(additionalGearStorage);

		// verification
		// reload user info to ensure the storage info isnt cached
		IUser newUser = new TestConnectionBuilder()
				.defaultCredentials()
				.disableSSLCertificateChecks()
				.create()
				.getUser();
		IApplication newApplication = newUser.getDefaultDomain().getApplicationByName(application.getName());
		IDeployedStandaloneCartridge newCartridge = newApplication.getCartridge();
		new StandaloneCartridgeAssert(newCartridge).isEqualTo(cartridge);
		assertThat(newCartridge.getAdditionalGearStorage()).isEqualTo(additionalGearStorage);
	}
}