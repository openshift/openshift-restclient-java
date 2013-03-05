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

import org.junit.Before;
import org.junit.Test;

import com.openshift.client.ApplicationScale;
import com.openshift.client.IApplication;
import com.openshift.client.ICartridge;
import com.openshift.client.IDomain;
import com.openshift.client.IGearProfile;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftEndpointException;
import com.openshift.client.OpenShiftException;
import com.openshift.client.utils.ApplicationAssert;
import com.openshift.client.utils.ApplicationTestUtils;
import com.openshift.client.utils.DomainTestUtils;
import com.openshift.client.utils.StringUtils;
import com.openshift.client.utils.TestConnectionFactory;

/**
 * @author André Dietisheim
 */
public class ApplicationResourceIntegrationTest {

	private static final long WAIT_TIMEOUT = 3 * 60 * 1000;

	private static IDomain domain;

	@Before
	public void setUp() throws Exception {
		try {
			IOpenShiftConnection connection = new TestConnectionFactory().getConnection();
			IUser user = connection.getUser();
			domain = DomainTestUtils.getFirstDomainOrCreate(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void shouldCreateNonScalableApplication() throws Exception {
		// pre-conditions
		ApplicationTestUtils.silentlyEnsureHasMaxApplication(2, domain);

		// operation
		String applicationName =
				ApplicationTestUtils.createRandomApplicationName();
		IApplication application = 
				domain.createApplication(applicationName, ICartridge.JBOSSAS_7);

		// verification
		assertThat(new ApplicationAssert(application))
				.hasName(applicationName)
				.hasUUID()
				.hasCreationTime()
				.hasCartridge(ICartridge.JBOSSAS_7)
				.hasValidApplicationUrl()
				.hasValidGitUrl()
				.hasEmbeddableCartridges()
				.hasAlias();
	}

	@Test
	public void shouldCreateNonScalableApplicationWithSmallGear() throws Exception {
		// pre-conditions
		ApplicationTestUtils.silentlyEnsureHasMaxApplication(2, domain);

		// operation
		String applicationName =
				ApplicationTestUtils.createRandomApplicationName();
		IApplication application = domain.createApplication(
				applicationName, ICartridge.JBOSSAS_7, IGearProfile.SMALL);

		// verification
		assertThat(new ApplicationAssert(application))
				.hasName(applicationName)
				.hasUUID()
				.hasCreationTime()
				.hasCartridge(ICartridge.JBOSSAS_7)
				.hasValidApplicationUrl()
				.hasValidGitUrl()
				.hasEmbeddableCartridges()
				.hasAlias();
	}

	@Test
	public void shouldCreateScalableApplication() throws Exception {
		// pre-conditions
		ApplicationTestUtils.silentlyEnsureHasMaxApplication(2, domain);

		// operation
		String applicationName =
				ApplicationTestUtils.createRandomApplicationName();
		IApplication application = domain.createApplication(
				applicationName, ICartridge.JBOSSAS_7, ApplicationScale.SCALE, GearProfile.SMALL);

		// verification
		assertThat(new ApplicationAssert(application))
				.hasName(applicationName)
				.hasUUID()
				.hasCreationTime()
				.hasCartridge(ICartridge.JBOSSAS_7)
				.hasValidApplicationUrl()
				.hasValidGitUrl()
				.hasEmbeddableCartridges()
				.hasAlias();
	}

	@Test
	public void shouldCreateJenkinsApplication() throws Exception {
		// pre-conditions
		ApplicationTestUtils.silentlyDestroyAllApplicationsByCartridge(ICartridge.JENKINS_14, domain);
		ApplicationTestUtils.silentlyEnsureHasMaxApplication(2, domain);

		// operation
		String applicationName =
				ApplicationTestUtils.createRandomApplicationName();
		IApplication application = domain.createApplication(
				applicationName, ICartridge.JENKINS_14);

		// verification
		assertThat(new ApplicationAssert(application))
				.hasName(applicationName)
				.hasUUID()
				.hasCreationTime()
				.hasCartridge(ICartridge.JENKINS_14)
				.hasValidApplicationUrl()
				.hasValidGitUrl()
				.hasEmbeddableCartridges()
				.hasAlias();
	}

	@Test
	public void shouldDestroyApplication() throws Exception {
		// pre-condition
		ApplicationTestUtils.silentlyEnsureHasMaxApplication(2, domain);
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		assertThat(application.getName()).isNotEmpty();

		// operation
		application.destroy();

		// verification
		assertThat(domain.hasApplicationByName(application.getName())).isFalse();

	}

	@Test(expected = OpenShiftException.class)
	public void createDuplicateApplicationThrowsException() throws Exception {
		// pre-condition
		ApplicationTestUtils.silentlyEnsureHasMaxApplication(2, domain);
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);

		// operation
		domain.createApplication(application.getName(), ICartridge.JBOSSAS_7);
	}

	@Test
	public void shouldStopApplication() throws Exception {
		// pre-condition
		ApplicationTestUtils.silentlyEnsureHasMaxApplication(2, domain);
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);

		// operation
		application.stop();
	}

	@Test
	public void shouldStartStoppedApplication() throws Exception {
		// pre-condition
		ApplicationTestUtils.silentlyEnsureHasMaxApplication(2, domain);
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		application.stop();

		// operation
		application.start();
	}

	@Test
	public void shouldStartStartedApplication() throws Exception {
		// pre-condition
		ApplicationTestUtils.silentlyEnsureHasMaxApplication(2, domain);
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
		ApplicationTestUtils.silentlyEnsureHasMaxApplication(2, domain);
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
		ApplicationTestUtils.silentlyEnsureHasMaxApplication(2, domain);
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
		ApplicationTestUtils.silentlyEnsureHasMaxApplication(2, domain);
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
		ApplicationTestUtils.silentlyEnsureHasMaxApplication(2, domain);
		IApplication application = domain.createApplication(
				DomainTestUtils.createRandomName(), ICartridge.JBOSSAS_7, ApplicationScale.NO_SCALE);

		// operation
		application.scaleDown();

		// verification
		// there's currently no API to verify the application state
	}

	@Test(expected = OpenShiftEndpointException.class)
	public void shouldNotScaleUpApplication() throws Throwable {
		// pre-condition
		ApplicationTestUtils.silentlyEnsureHasMaxApplication(2, domain);
		IApplication application = domain.createApplication(
				DomainTestUtils.createRandomName(), ICartridge.JBOSSAS_7, ApplicationScale.NO_SCALE);

		// operation
		application.scaleUp();

		// verification
		// there's currently no API to verify the application state
	}

	@Test(expected = OpenShiftEndpointException.class)
	public void shouldScaleUpApplication() throws Throwable {
		// pre-condition
		ApplicationTestUtils.silentlyEnsureHasMaxApplication(2, domain);
		IApplication application = domain.createApplication(
				DomainTestUtils.createRandomName(), ICartridge.JBOSSAS_7, ApplicationScale.SCALE);

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
				DomainTestUtils.createRandomName(), ICartridge.JBOSSAS_7, ApplicationScale.SCALE);
		application.scaleUp();
		
		// operation
		application.scaleDown();

		// verification
		// there's currently no API to verify the application state
	}
	
	@Test
	public void shouldAddAliasToApplication() throws Throwable {
		// pre-condition
		ApplicationTestUtils.silentlyEnsureHasMaxApplication(2, domain);
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		String alias = String.valueOf(System.currentTimeMillis());
		// operation

		application.addAlias(alias);

		// verification
		assertThat(application.getAliases()).contains(alias);
	}

	@Test
	public void shouldRemoveAliasOfApplication() throws Throwable {
		// pre-condition
		ApplicationTestUtils.silentlyEnsureHasMaxApplication(2, domain);
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		String alias = String.valueOf(System.currentTimeMillis());
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
		ApplicationTestUtils.silentlyEnsureHasMaxApplication(2, domain);
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
		ApplicationTestUtils.silentlyEnsureHasMaxApplication(2, domain);
		long startTime = System.currentTimeMillis();
		IApplication application = domain.createApplication(StringUtils.createRandomString(), ICartridge.JBOSSAS_7);

		// operation
		boolean successfull = application.waitForAccessible(WAIT_TIMEOUT);

		if (successfull) {
			assertTrue(System.currentTimeMillis() <= startTime + WAIT_TIMEOUT);
		} else {
			assertTrue(System.currentTimeMillis() >= startTime + WAIT_TIMEOUT);
		}
}
}
