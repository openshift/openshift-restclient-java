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
package com.openshift.internal.client;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.openshift.client.ApplicationScale;
import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IGearGroup;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftEndpointException;
import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.cartridge.selector.LatestVersionOf;
import com.openshift.client.utils.ApplicationTestUtils;
import com.openshift.client.utils.DomainTestUtils;
import com.openshift.client.utils.GearGroupsAssert;
import com.openshift.client.utils.StringUtils;
import com.openshift.client.utils.TestConnectionFactory;

/**
 * @author André Dietisheim
 */
public class ApplicationResourceIntegrationTest {

	private static final long WAIT_TIMEOUT = 3 * 60 * 1000;

	private IUser user;
	private IDomain domain;

	@Before
	public void setUp() throws Exception {
		IOpenShiftConnection connection = new TestConnectionFactory().getConnection();
		this.user = connection.getUser();
		this.domain = DomainTestUtils.ensureHasDomain(user);
	}

	@Test
	public void shouldReturnGears() throws Exception {
		// pre-conditions
		ApplicationTestUtils.destroyIfMoreThan(2, domain);

		// operation
		String applicationName =
				ApplicationTestUtils.createRandomApplicationName();
		IStandaloneCartridge jbossas = LatestVersionOf.jbossAs().get(user);		
		IApplication application =
				domain.createApplication(applicationName, jbossas);

		Collection<IGearGroup> gearGroups = application.getGearGroups();
		assertThat(new GearGroupsAssert(gearGroups)).hasSize(1);
		assertThat(new GearGroupsAssert(gearGroups))
				.assertGroup(0).hasUUID().hasGears()
				.assertGear(0).hasId().hasState();
	}

	@Test
	public void shouldDestroyApplication() throws Exception {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		assertThat(application.getName()).isNotEmpty();

		// operation
		application.destroy();

		// verification
		assertThat(domain.hasApplicationByName(application.getName())).isFalse();

	}

	@Test
	public void shouldStopApplication() throws Exception {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);

		// operation
		application.stop();
	}

	@Test
	public void shouldStartStoppedApplication() throws Exception {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		application.stop();

		// operation
		application.start();
	}

	@Test
	public void shouldStartStartedApplication() throws Exception {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		application.start();

		// operation
		application.start();

		// verification
		// there's currently no API to verify the application state
	}

	@Test
	public void shouldStopStoppedApplication() throws Exception {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		application.stop();

		// operation
		application.stop();

		// verification
		// there's currently no API to verify the application state
	}

	@Test
	public void shouldRestartStartedApplication() throws Exception {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		application.start();

		// operation
		application.restart();

		// verification
		// there's currently no API to verify the application state
	}

	@Test
	public void shouldRestartStoppedApplication() throws Exception {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		application.stop();

		// operation
		application.restart();

		// verification
		// there's currently no API to verify the application state
	}

	@Test(expected = OpenShiftEndpointException.class)
	public void shouldNotScaleDownIfNotScaledUpApplication() throws Throwable {
		// pre-condition
		ApplicationTestUtils.silentlyDestroyAllApplications(domain);
		IApplication application = domain.createApplication(
				DomainTestUtils.createRandomName(), LatestVersionOf.jbossAs().get(user), ApplicationScale.NO_SCALE);

		// operation
		application.scaleDown();

		// verification
		// there's currently no API to verify the application state
	}

	@Test(expected = OpenShiftEndpointException.class)
	public void shouldNotScaleUpApplication() throws Throwable {
		// pre-condition
		ApplicationTestUtils.silentlyDestroyAllApplications(domain);
		IApplication application = domain.createApplication(
				DomainTestUtils.createRandomName(), LatestVersionOf.jbossAs().get(user), ApplicationScale.NO_SCALE);

		// operation
		application.scaleUp();

		// verification
		// there's currently no API to verify the application state
	}

	public void shouldScaleUpApplication() throws Throwable {
		// pre-condition
		ApplicationTestUtils.silentlyDestroyAllApplications(domain);
		IApplication application = domain.createApplication(
				DomainTestUtils.createRandomName(), LatestVersionOf.jbossAs().get(user), ApplicationScale.SCALE);

		// operation
		application.scaleUp();

		// verification
		// there's currently no API to verify the application state
	}

	@Test
	public void shouldScaleDownApplication() throws Throwable {
		// pre-condition
		ApplicationTestUtils.silentlyDestroyAllApplications(domain);
		IApplication application = domain.createApplication(
				DomainTestUtils.createRandomName(), LatestVersionOf.jbossAs().get(user), ApplicationScale.SCALE);
		application.scaleUp();

		// operation
		application.scaleDown();

		// verification
		// there's currently no API to verify the application state
	}

	@Test
	public void shouldAddAliasToApplication() throws Throwable {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		String alias = createAlias();
		// operation

		application.addAlias(alias);

		// verification
		assertThat(application.getAliases()).contains(alias);
	}

	@Test
	public void shouldRemoveAliasOfApplication() throws Throwable {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		String alias = createAlias();
		application.addAlias(alias);
		assertThat(application.getAliases()).contains(alias);

		// operation
		application.removeAlias(alias);

		// verification
		assertThat(alias).isNotIn(application.getAliases());
	}

	@Test(expected = OpenShiftEndpointException.class)
	public void shouldNotAddExistingAliasToApplication() throws Throwable {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		String alias = String.valueOf(System.currentTimeMillis());
		application.addAlias(alias);
		assertThat(application.getAliases()).contains(alias);

		// operation
		application.addAlias(alias);
	}

	/**
	 * https://issues.jboss.org/browse/JBIDE-13569
	 */
	@Test
	public void shouldWaitForApplication() throws OpenShiftException, MalformedURLException, IOException {
		// pre-condition
		ApplicationTestUtils.destroyIfMoreThan(2, domain);
		long startTime = System.currentTimeMillis();
		IApplication application = domain.createApplication(StringUtils.createRandomString(), LatestVersionOf.jbossAs().get(user));

		// operation
		boolean successfull = application.waitForAccessible(WAIT_TIMEOUT);

		if (successfull) {
			assertTrue(System.currentTimeMillis() <= startTime + WAIT_TIMEOUT);
		} else {
			assertTrue(System.currentTimeMillis() >= startTime + WAIT_TIMEOUT);
		}
	}

	/**
	 * https://issues.jboss.org/browse/JBIDE-14721
	 */
	private String createAlias() {
		return new StringBuilder(String.valueOf(System.currentTimeMillis()))
		// valid alias is "/\A[a-z0-9]+(-[a-z0-9]+)*(\.[a-z0-9]+(-[a-z0-9]+)*)+\z/"
		.append('.')
		.append('1')
		.toString();
	}
}
