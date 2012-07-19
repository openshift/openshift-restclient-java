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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IEmbeddableCartridge;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftConnectionFactory;
import com.openshift.client.OpenShiftException;
import com.openshift.client.utils.ApplicationTestUtils;
import com.openshift.client.utils.DomainTestUtils;
import com.openshift.client.utils.EmbeddableCartridgeAsserts;
import com.openshift.client.utils.EmbeddedCartridgeTestUtils;
import com.openshift.client.utils.OpenShiftTestConfiguration;

/**
 * @author André Dietisheim
 */
public class EmbeddedCartridgeResourceIntegrationTest {

	// private static final int WAIT_FOR_APPLICATION = 10 * 1000;

	private IUser user;
	private IApplication application;
	private static IDomain domain;

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
		domain = DomainTestUtils.getFirstDomainOrCreate(user);
		this.application = ApplicationTestUtils.getOrCreateApplication(domain);
	}

	@AfterClass
	public static void cleanup() {
		ApplicationTestUtils.silentlyDestroyAllApplications(domain);
	}

	@Test
	public void shouldReturnEmbeddedCartridges() throws SocketTimeoutException, OpenShiftException {
		assertNotNull(application.getEmbeddedCartridges());
	}

	@Test
	public void shouldAddMySQLEmbeddedCartridge() throws SocketTimeoutException, OpenShiftException {
		// pre-conditions
		EmbeddedCartridgeTestUtils.silentlyDestroy(IEmbeddableCartridge.MYSQL_51, application);
		int numOfEmbeddedCartridges = application.getEmbeddedCartridges().size();

		// operation
		application.addEmbeddableCartridge(IEmbeddableCartridge.MYSQL_51);

		// verification
		assertNotNull(application.getEmbeddedCartridges());
		assertTrue(application.getEmbeddedCartridges().size() == numOfEmbeddedCartridges + 1);
		EmbeddableCartridgeAsserts.assertThatContainsCartridge(
				IEmbeddableCartridge.MYSQL_51.getName(), application.getEmbeddedCartridges());
	}

	@Test
	public void shouldReturnThatContainsEmbeddedCartridge() throws SocketTimeoutException, OpenShiftException {
		// pre-conditions
		EmbeddedCartridgeTestUtils.silentlyDestroy(IEmbeddableCartridge.MYSQL_51, application);

		// operation
		application.addEmbeddableCartridge(IEmbeddableCartridge.MYSQL_51);

		// verification
		assertNotNull(application.getEmbeddedCartridges());
		assertTrue(application.hasEmbeddedCartridge(IEmbeddableCartridge.MYSQL_51));
	}

	@Ignore
	@Test
	public void canEmbedMongo() throws Exception {
		// MongoEmbeddableCartridge mongo = new
		// MongoEmbeddableCartridge(service, user);
		// IEmbeddableCartridge cartridge =
		// service.addEmbeddedCartridge(application.getName(), mongo, user);
		// assertEquals(mongo.getName(), cartridge.getName());
		// assertThatContainsCartridge(mongo.getName(),
		// application.getEmbeddedCartridges());
	}

	@Ignore
	@Test
	public void canEmbedRockMongo() throws Exception {
		// MongoEmbeddableCartridge mongo = new
		// MongoEmbeddableCartridge(service, user);
		// IEmbeddableCartridge cartridge =
		// service.addEmbeddedCartridge(application.getName(), mongo, user);
		// assertEquals(mongo.getName(), cartridge.getName());
		// assertThatContainsCartridge(mongo.getName(),
		// application.getEmbeddedCartridges());
		//
		// RockMongoEmbeddableCartridge rockmongo = new
		// RockMongoEmbeddableCartridge(service, user);
		// cartridge = service.addEmbeddedCartridge(application.getName(),
		// rockmongo, user);
		// assertEquals(rockmongo.getName(), cartridge.getName());
		// assertThatContainsCartridge(rockmongo.getName(),
		// application.getEmbeddedCartridges());
	}

	@Ignore
	@Test
	public void canEmbedCron() throws Exception {
		// CronEmbeddableCartridge cron = new CronEmbeddableCartridge(service,
		// user);
		// IEmbeddableCartridge cartridge =
		// service.addEmbeddedCartridge(application.getName(), cron, user);
		// assertEquals(cron.getName(), cartridge.getName());
		// assertThatContainsCartridge(cron.getName(),
		// application.getEmbeddedCartridges());
	}

	@Ignore
	@Test
	public void canEmbedGenMMSAgent() throws Exception {
		// MongoEmbeddableCartridge mongo = new
		// MongoEmbeddableCartridge(service, user);
		// IEmbeddableCartridge cartridge =
		// service.addEmbeddedCartridge(application.getName(), mongo, user);
		// assertEquals(mongo.getName(), cartridge.getName());
		// assertThatContainsCartridge(mongo.getName(),
		// application.getEmbeddedCartridges());
		//
		// GenMMSAgentEmbeddableCartridge mms = new
		// GenMMSAgentEmbeddableCartridge(service, user);
		// cartridge = service.addEmbeddedCartridge(application.getName(), mms,
		// user);
		// assertEquals(mms.getName(), cartridge.getName());
		// assertThatContainsCartridge(mms.getName(),
		// application.getEmbeddedCartridges());
	}

	@Ignore
	@Test(expected = OpenShiftException.class)
	public void canEmbedBogusGeneric() throws Exception {
		// List<IEmbeddableCartridge> cartridges =
		// service.getEmbeddableCartridges(user);
		//
		// Iterator<IEmbeddableCartridge> i = cartridges.iterator();
		// while (i.hasNext()){
		// IEmbeddableCartridge cartridge = i.next();
		// System.out.println("embeddable " + cartridge.getName());
		// }
		// EmbeddableCartridge bogus = new EmbeddableCartridge("bogus-1.0");
		// IEmbeddableCartridge cartridge =
		// service.addEmbeddedCartridge(application.getName(), bogus, user);
	}

	@Ignore
	@Test
	public void canEmbedGeneric() throws Exception {
		// EmbeddableCartridge mysql = new EmbeddableCartridge("mysql-5.1");
		// IEmbeddableCartridge cartridge =
		// service.addEmbeddedCartridge(application.getName(), mysql, user);
		// assertEquals(mysql.getName(), cartridge.getName());
		// assertThatContainsCartridge(mysql.getName(),
		// application.getEmbeddedCartridges());
	}

	@Test
	public void canEmbedMetrics() throws Exception {
		// MetricsEmbeddableCartridge metrics = new
		// MetricsEmbeddableCartridge(service, user);
		// IEmbeddableCartridge cartridge =
		// service.addEmbeddedCartridge(application.getName(), metrics, user);
		// assertEquals(metrics.getName(), cartridge.getName());
		// assertThatContainsCartridge(metrics.getName(),
		// application.getEmbeddedCartridges());
	}

	@Ignore
	@Test
	public void canEmbedPhpMyAdmin() throws Exception {
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
		// assertThatContainsCartridge(myadmin.getName(),
		// application.getEmbeddedCartridges());
	}

	@Ignore
	@Test
	public void canEmbedJenkins() throws Exception {
		// ApplicationTestUtils.silentlyDestroyAnyJenkinsApplication(user);
		// String jenkinsAppName = "jenkins";
		// IJenkinsApplication jenkins = null;
		// try {
		// jenkins = service.createJenkinsApplication(jenkinsAppName, user);
		// assertTrue(jenkins.waitForAccessible(WAIT_FOR_APPLICATION * 10));
		// service.addEmbeddedCartridge(application.getName(), new
		// JenkinsClientEmbeddableCartridge(service, user), user);
		// } finally {
		// ApplicationTestUtils.silentlyDestroyApplication(jenkinsAppName,
		// jenkins.getCartridge(), user, service);
		// }
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

	@Test
	public void shouldRemoveEmbeddedCartridge() throws Exception {
		// pre-conditions
		EmbeddedCartridgeTestUtils.silentlyDestroy(IEmbeddableCartridge.MYSQL_51, application);
		int numOfEmbeddedCartridges = application.getEmbeddedCartridges().size();

		// operation
		application.addEmbeddableCartridge(IEmbeddableCartridge.MYSQL_51);

		// verification
		assertNotNull(application.getEmbeddedCartridges());
		assertTrue(application.getEmbeddedCartridges().size() == numOfEmbeddedCartridges + 1);
		EmbeddableCartridgeAsserts.assertThatContainsCartridge(
				IEmbeddableCartridge.MYSQL_51.getName(), application.getEmbeddedCartridges());
	}
	
	 @Test
	 public void cannotRemoveEmbeddedCartridgeThatWasNotAdded() throws SocketTimeoutException, OpenShiftException {
		// pre-conditions
		EmbeddedCartridgeTestUtils.silentlyDestroy(IEmbeddableCartridge.MYSQL_51, application);
		int numOfEmbeddedCartridges = application.getEmbeddedCartridges().size();
		
		// operation
		application.removeEmbeddedCartridge(IEmbeddableCartridge.MYSQL_51);

		// verification
		assertNotNull(application.getEmbeddedCartridges());
		assertTrue(application.getEmbeddedCartridges().size() == numOfEmbeddedCartridges);
		EmbeddableCartridgeAsserts.assertThatDoesntContainsCartridge(
				IEmbeddableCartridge.MYSQL_51.getName(), application.getEmbeddedCartridges());
	 }

}
