/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.openshift.client.IApplication;
import com.openshift.client.ICartridge;
import com.openshift.client.IDomain;
import com.openshift.client.IEmbeddableCartridge;
import com.openshift.client.IEmbeddableCartridgeConstraint;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.LatestVersionOf;
import com.openshift.client.OpenShiftConnectionFactory;
import com.openshift.client.OpenShiftEndpointException;
import com.openshift.client.OpenShiftException;
import com.openshift.client.utils.ApplicationAssert;
import com.openshift.client.utils.ApplicationTestUtils;
import com.openshift.client.utils.DomainTestUtils;
import com.openshift.client.utils.EmbeddableCartridgeAsserts;
import com.openshift.client.utils.EmbeddedCartridgeTestUtils;
import com.openshift.client.utils.OpenShiftTestConfiguration;

/**
 * @author André Dietisheim
 */
public class EmbeddedCartridgeResourceIntegrationTest {

	private static final int WAIT_FOR_APPLICATION = 180 * 1000;

	private IApplication application;
	private IDomain domain;

	@Before
	public void setUp() throws OpenShiftException, IOException {
		final OpenShiftTestConfiguration configuration = new OpenShiftTestConfiguration();
		final IOpenShiftConnection connection =
				new OpenShiftConnectionFactory().getConnection(
						configuration.getClientId(),
						configuration.getRhlogin(),
						configuration.getPassword(),
						configuration.getLibraServer());
		this.user = connection.getUser();
		this.domain = DomainTestUtils.getFirstDomainOrCreate(user);
		this.application = ApplicationTestUtils.getOrCreateApplication(domain);
	}

	@Test
	public void shouldReturnEmbeddedCartridges() throws SocketTimeoutException, OpenShiftException {
		assertNotNull(application.getEmbeddedCartridges());
	}

	@Ignore("temporary")
	@Test
	public void shouldAddMySQLEmbeddedCartridge() throws SocketTimeoutException, OpenShiftException {
		// pre-conditions
		EmbeddedCartridgeTestUtils.silentlyDestroy(LatestVersionOf.mySQL(), application);
		int numOfEmbeddedCartridges = application.getEmbeddedCartridges().size();

		// operation
		application.addEmbeddableCartridge(LatestVersionOf.mySQL());

		// verification
		assertNotNull(application.getEmbeddedCartridges());
		assertTrue(application.getEmbeddedCartridges().size() == numOfEmbeddedCartridges + 1);

		EmbeddableCartridgeAsserts.assertThatContainsCartridge(
				EmbeddedCartridgeTestUtils.getLatestMySqlCartridge(user.getConnection()),
				application.getEmbeddedCartridges());
	}

	@Ignore("temporary")
	@Test
	public void shouldReturnThatHasEmbeddedCartridge() throws SocketTimeoutException, OpenShiftException {
		// pre-conditions
		IEmbeddableCartridge mySQL = 
				EmbeddedCartridgeTestUtils.getLatestMySqlCartridge(user.getConnection());
		EmbeddedCartridgeTestUtils.silentlyDestroy(LatestVersionOf.mySQL(), application);

		// operation
		application.addEmbeddableCartridge(LatestVersionOf.mySQL());

		// verification
		assertNotNull(application.getEmbeddedCartridges());
		assertTrue(application.hasEmbeddedCartridge(mySQL));
	}

//	@Ignore("temporary")
	@Test
	public void canEmbedMongo() throws Exception {
		// pre-conditions
		EmbeddedCartridgeTestUtils.silentlyDestroy(IEmbeddableCartridge.MONGODB_22, application);
		assertThat(new ApplicationAssert(application)
				.hasNotEmbeddableCartridges(IEmbeddableCartridge.MONGODB_22.toString()));

		// operation
		application.addEmbeddableCartridge(IEmbeddableCartridgeConstraint.MONGODB);

		// verification
		assertThat(new ApplicationAssert(application)
				.hasEmbeddableCartridges(IEmbeddableCartridge.MONGODB_22.getName()));
	}

	@Ignore("temporary")
	@Test
	public void canEmbedRockMongo() throws Exception {
		// pre-conditions
		EmbeddedCartridgeTestUtils.silentlyDestroy(IEmbeddableCartridge.MONGODB_22, application);
		EmbeddedCartridgeTestUtils.silentlyDestroy(IEmbeddableCartridge.ROCKMONGO_11, application);
		assertThat(new ApplicationAssert(application)
			.hasNotEmbeddableCartridges(IEmbeddableCartridge.MONGODB_22.toString())
			.hasNotEmbeddableCartridges(IEmbeddableCartridge.ROCKMONGO_11.toString()));

		// operation
		application.addEmbeddableCartridge(IEmbeddableCartridgeConstraint.MONGODB);
		application.addEmbeddableCartridge(IEmbeddableCartridgeConstraint.ROCKMONGO);

		// verification
		assertThat(new ApplicationAssert(application)
				.hasEmbeddableCartridges(IEmbeddableCartridge.MONGODB_22.getName())
				.hasEmbeddableCartridges(IEmbeddableCartridge.ROCKMONGO_11.getName()));
	}

	@Ignore("temporary")
	@Test
	public void canEmbedPhpMyAdmin() throws Exception {
		// pre-conditions
		EmbeddedCartridgeTestUtils.silentlyDestroy(IEmbeddableCartridge.MYSQL_51, application);
		EmbeddedCartridgeTestUtils.silentlyDestroy(IEmbeddableCartridge.PHPMYADMIN_34, application);
		assertThat(new ApplicationAssert(application)
				.hasNotEmbeddableCartridges(IEmbeddableCartridge.MYSQL_51.toString()))
				.hasNotEmbeddableCartridges(IEmbeddableCartridge.PHPMYADMIN_34.toString());

		// operation
		application.addEmbeddableCartridge(IEmbeddableCartridgeConstraint.MYSQL);
		application.addEmbeddableCartridge(IEmbeddableCartridgeConstraint.PHPMYADMIN);

		// verification
		assertThat(new ApplicationAssert(application)
				.hasEmbeddableCartridges(IEmbeddableCartridge.MYSQL_51.getName()))
				.hasEmbeddableCartridges(IEmbeddableCartridge.PHPMYADMIN_34.getName());
	}

	@Test
	public void shouldEmbedJenkinsClient() throws Exception {
		// pre-conditions
		// need 2 free gears; jenkins + builder
		ApplicationTestUtils.silentlyDestroyAllApplicationsByCartridge(ICartridge.JENKINS_14, domain);
		ApplicationTestUtils.silentlyEnsureHasMaxApplication(1, domain);
		IApplication application = domain.getApplications().get(0);
		IApplication jenkins = domain.createApplication("j", ICartridge.JENKINS_14);
		assertTrue(jenkins.waitForAccessible(WAIT_FOR_APPLICATION * 10));

		// operation
		application.addEmbeddableCartridge(IEmbeddableCartridgeConstraint.JENKINS_CLIENT);

		// verification
		assertThat(new ApplicationAssert(application)
				.hasEmbeddableCartridges(IEmbeddableCartridge.JENKINS_14.toString()));
	}

	@Ignore
	@Test
	public void embeddedCartridgeHasUrl() throws OpenShiftException {
		// String applicationName =
		// ApplicationTestUtils.createRandomApplicationName();
		// IApplication application = null;
		// try {
		// application = service.createJBossASApplication(applicationName,
		// user);
		// MySqlEmbeddableCartridge mysql = new
		// MySqlEmbeddableCartridge(service, user);
		// application.addEmbbedCartridge(mysql);
		// IEmbeddableCartridge embeddedCartridge =
		// application.getEmbeddedCartridge(mysql.getName());
		// assertNotNull(embeddedCartridge);
		// assertNotNull(embeddedCartridge.getUrl());
		// } finally {
		// ApplicationTestUtils.silentlyDestroyApplication(applicationName,
		// application.getCartridge(), user, service);
		// }
	}

	@Ignore
	@Test
	public void loadWithOtherUserReportsIdenticalResults() throws Exception {
		// MySqlEmbeddableCartridge mysql = new
		// MySqlEmbeddableCartridge(service, user);
		// IEmbeddableCartridge cartridge =
		// service.addEmbeddedCartridge(application.getName(), mysql, user);
		// assertEquals(mysql.getName(), cartridge.getName());
		//
		// PHPMyAdminEmbeddableCartridge myadmin = new
		// PHPMyAdminEmbeddableCartridge(service, user);
		// cartridge = service.addEmbeddedCartridge(application.getName(),
		// myadmin, user);
		// assertEquals(myadmin.getName(), cartridge.getName());
		//
		// User newUser = new TestUser(service);
		// IApplication reloadedApplication =
		// newUser.getApplicationByName(application.getName());
		// assertNotNull(reloadedApplication);
		// List<IEmbeddableCartridge> embeddedCartridges =
		// reloadedApplication.getEmbeddedCartridges();
		// assertNotNull(embeddedCartridges);
		// assertEquals(2, embeddedCartridges.size());
		// assertThatContainsCartridge(mysql.getName(), embeddedCartridges);
		// assertThatContainsCartridge(myadmin.getName(), embeddedCartridges);
	}

	@Test(expected=OpenShiftEndpointException.class)
	public void shouldNotAddEmbeddedCartridgeTwice() throws Exception {
		// pre-conditions
		EmbeddedCartridgeTestUtils.ensureHasEmbeddedCartridges(LatestVersionOf.mySQL(), application);

		// operation
		application.addEmbeddableCartridge(LatestVersionOf.mySQL());
	}

	@Test
	public void shouldRemoveEmbeddedCartridge() throws Exception {
		// pre-conditions
		EmbeddedCartridgeTestUtils.ensureHasEmbeddedCartridges(LatestVersionOf.mySQL(), application);
		int numOfEmbeddedCartridges = application.getEmbeddedCartridges().size();

		// operation
		application.removeEmbeddedCartridges(LatestVersionOf.mySQL());

		// verification
		assertNotNull(application.getEmbeddedCartridges());
		assertTrue(application.getEmbeddedCartridges().size() == numOfEmbeddedCartridges - 1);
		EmbeddableCartridgeAsserts.assertThatDoesntContainCartridge(
				EmbeddedCartridgeTestUtils.getLatestMySqlCartridge(user.getConnection()),
				application.getEmbeddedCartridges());
	}

	@Ignore("temporary")
	@Test
	public void cannotRemoveEmbeddedCartridgeThatWasNotAdded() throws SocketTimeoutException, OpenShiftException {
		// pre-conditions
		EmbeddedCartridgeTestUtils.silentlyDestroy(LatestVersionOf.mySQL(), application);
		int numOfEmbeddedCartridges = application.getEmbeddedCartridges().size();

		// operation
		application.removeEmbeddedCartridges(LatestVersionOf.mySQL());

		// verification
		assertNotNull(application.getEmbeddedCartridges());
		assertTrue(application.getEmbeddedCartridges().size() == numOfEmbeddedCartridges);
		IEmbeddableCartridge mySql = EmbeddedCartridgeTestUtils.getLatestMySqlCartridge(user.getConnection());
		EmbeddableCartridgeAsserts.assertThatDoesntContainCartridge(
				mySql.getName(), application.getEmbeddedCartridges());
	}

}
