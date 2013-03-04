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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import com.openshift.client.IApplication;
import com.openshift.client.ICartridge;
import com.openshift.client.IDomain;
import com.openshift.client.IEmbeddableCartridge;
import com.openshift.client.IUser;
import com.openshift.client.LatestVersionOf;
import com.openshift.client.OpenShiftEndpointException;
import com.openshift.client.OpenShiftException;
import com.openshift.client.utils.ApplicationAssert;
import com.openshift.client.utils.ApplicationTestUtils;
import com.openshift.client.utils.DomainTestUtils;
import com.openshift.client.utils.EmbeddedCartridgeAssert;
import com.openshift.client.utils.EmbeddedCartridgeTestUtils;
import com.openshift.client.utils.TestConnectionFactory;

/**
 * @author André Dietisheim
 */
public class EmbeddedCartridgeResourceIntegrationTest {

	private static final int WAIT_FOR_APPLICATION = 180 * 1000;

	private IApplication application;
	private IDomain domain;
	private IUser user;
	
	@Before
	public void setUp() throws OpenShiftException, IOException {
		this.user = new TestConnectionFactory().getConnection().getUser();
		this.domain = DomainTestUtils.getFirstDomainOrCreate(user);
		this.application = ApplicationTestUtils.getOrCreateApplication(domain);
		ApplicationTestUtils.silentlyEnsureHasMaxApplication(1, domain);
	}

	@Test
	public void shouldReturnEmbeddedCartridges() throws SocketTimeoutException, OpenShiftException {
		assertTrue(application.getEmbeddedCartridges().size() >= 0);
	}
	
	@Test
	public void shouldEmbedMySQL() throws SocketTimeoutException, OpenShiftException, URISyntaxException {
		// pre-conditions
		EmbeddedCartridgeTestUtils.silentlyDestroyAllEmbeddedCartridges(application);
		int numOfEmbeddedCartridges = application.getEmbeddedCartridges().size();
		assertThat(numOfEmbeddedCartridges).isEqualTo(0);
		
		// operation
		application.addEmbeddableCartridge(LatestVersionOf.mySQL());

		// verification
		assertThat(new ApplicationAssert(application))
				.hasEmbeddableCartridges(numOfEmbeddedCartridges + 1)
				.hasEmbeddedCartridges(LatestVersionOf.mySQL());

		IEmbeddableCartridge mysql = 
				EmbeddedCartridgeTestUtils.getFirstEmbeddableCartridge(LatestVersionOf.mySQL(), user.getConnection());
		new EmbeddedCartridgeAssert(application.getEmbeddedCartridge(mysql))
				.hasUrl();
	}

	@Test
	public void shouldHaveUrlInEmbeddedMySQL() throws OpenShiftException, URISyntaxException, FileNotFoundException, IOException {
		// pre-conditions
		EmbeddedCartridgeTestUtils.ensureHasEmbeddedCartridges(LatestVersionOf.mySQL(), application);
		IUser user2 = new TestConnectionFactory().getConnection().getUser();
		IApplication user2Application = user2.getDefaultDomain().getApplicationByName(application.getName());
		assertThat(new ApplicationAssert(user2Application)).hasEmbeddedCartridges(LatestVersionOf.mySQL());

		// operation

		// verification
		IEmbeddableCartridge mysql =
				EmbeddedCartridgeTestUtils.getFirstEmbeddableCartridge(LatestVersionOf.mySQL(), user2.getConnection());
		new EmbeddedCartridgeAssert(user2Application.getEmbeddedCartridge(mysql))
				.hasUrl();
	}

	@Test
	public void shouldReturnThatHasMySQL() throws OpenShiftException, FileNotFoundException, IOException {
		// pre-conditions
		EmbeddedCartridgeTestUtils.ensureHasEmbeddedCartridges(LatestVersionOf.mySQL(), application);
		IUser user2 = new TestConnectionFactory().getConnection().getUser();
		IApplication user2Application = user2.getDefaultDomain().getApplicationByName(application.getName());
		EmbeddedCartridgeTestUtils.ensureHasEmbeddedCartridges(LatestVersionOf.mySQL(), user2Application);

		// operation
		
		// verification
		assertThat(new ApplicationAssert(user2Application)).hasEmbeddedCartridges(LatestVersionOf.mySQL());
	}

	@Test
	public void shouldEmbedPostgreSQL() throws SocketTimeoutException, OpenShiftException, URISyntaxException {
		// pre-conditions
		EmbeddedCartridgeTestUtils.silentlyDestroyAllEmbeddedCartridges(application);
		int numOfEmbeddedCartridges = application.getEmbeddedCartridges().size();
		assertThat(numOfEmbeddedCartridges).isEqualTo(0);

		application.addEmbeddableCartridge(LatestVersionOf.postgreSQL());

		// verification
		assertThat(new ApplicationAssert(application))
				.hasEmbeddableCartridges(numOfEmbeddedCartridges + 1)
				.hasEmbeddedCartridges(LatestVersionOf.postgreSQL());

		IEmbeddableCartridge postgres = 
				EmbeddedCartridgeTestUtils.getFirstEmbeddableCartridge(LatestVersionOf.postgreSQL(), user.getConnection());
		new EmbeddedCartridgeAssert(application.getEmbeddedCartridge(postgres))
				.hasUrl();
	}

	@Test
	public void shouldHaveUrlInEmbeddedPostgres() throws OpenShiftException, URISyntaxException, FileNotFoundException, IOException {
		// pre-conditions
		EmbeddedCartridgeTestUtils.ensureHasEmbeddedCartridges(LatestVersionOf.postgreSQL(), application);
		IUser user2 = new TestConnectionFactory().getConnection().getUser();
		IApplication user2Application = user2.getDefaultDomain().getApplicationByName(application.getName());
		assertThat(new ApplicationAssert(user2Application)).hasEmbeddedCartridges(LatestVersionOf.postgreSQL());
		
		// operation

		// verification
		IEmbeddableCartridge postgres = 
				EmbeddedCartridgeTestUtils.getFirstEmbeddableCartridge(LatestVersionOf.postgreSQL(), user2.getConnection());
		new EmbeddedCartridgeAssert(user2Application.getEmbeddedCartridge(postgres))
				.hasUrl();
	}

	@Test
	public void shouldEmbedMongo() throws Exception {
		// pre-conditions
		EmbeddedCartridgeTestUtils.silentlyDestroyAllEmbeddedCartridges(application);
		int numOfEmbeddedCartridges = application.getEmbeddedCartridges().size();
		assertThat(numOfEmbeddedCartridges).isEqualTo(0);

		// operation
		application.addEmbeddableCartridge(LatestVersionOf.mongoDB());

		// verification
		assertThat(new ApplicationAssert(application)
				.hasEmbeddedCartridges(LatestVersionOf.mongoDB()));
		IEmbeddableCartridge mongo = 
				EmbeddedCartridgeTestUtils.getFirstEmbeddableCartridge(LatestVersionOf.mongoDB(), user.getConnection());
		new EmbeddedCartridgeAssert(application.getEmbeddedCartridge(mongo))
				.hasUrl();
	}

	@Test
	public void shouldHaveUrlInEmbeddedMongo() throws OpenShiftException, URISyntaxException, FileNotFoundException, IOException {
		// pre-conditions
		EmbeddedCartridgeTestUtils.ensureHasEmbeddedCartridges(LatestVersionOf.mongoDB(), application);
		IUser user2 = new TestConnectionFactory().getConnection().getUser();
		IApplication user2Application = user2.getDefaultDomain().getApplicationByName(application.getName());
		assertThat(new ApplicationAssert(user2Application)).hasEmbeddedCartridges(LatestVersionOf.mongoDB());
		
		// operation

		// verification
		IEmbeddableCartridge mongo = 
				EmbeddedCartridgeTestUtils.getFirstEmbeddableCartridge(LatestVersionOf.mongoDB(), user2.getConnection());
		new EmbeddedCartridgeAssert(user2Application.getEmbeddedCartridge(mongo))
				.hasUrl();
	}

	@Test
	public void shouldEmbedRockMongo() throws Exception {
		// pre-conditions
		// pre-conditions
		EmbeddedCartridgeTestUtils.silentlyDestroyAllEmbeddedCartridges(application);
		assertThat(new ApplicationAssert(application)
			.hasNotEmbeddableCartridges(LatestVersionOf.mongoDB())
			.hasNotEmbeddableCartridges(LatestVersionOf.rockMongo()));

		// operation
		application.addEmbeddableCartridge(LatestVersionOf.mongoDB());
		application.addEmbeddableCartridge(LatestVersionOf.rockMongo());

		// verification
		assertThat(new ApplicationAssert(application)
				.hasEmbeddedCartridges(LatestVersionOf.mongoDB())
				.hasEmbeddedCartridges(LatestVersionOf.rockMongo()));
	}

	@Test
	public void shouldHaveUrlInEmbeddedRockMongo() throws SocketTimeoutException, OpenShiftException, URISyntaxException {
		// pre-conditions
		assertThat(new ApplicationAssert(application)).hasEmbeddedCartridges(LatestVersionOf.rockMongo());
		
		// operation

		// verification
		IEmbeddableCartridge rockMongo = 
				EmbeddedCartridgeTestUtils.getFirstEmbeddableCartridge(LatestVersionOf.rockMongo(), user.getConnection());
		new EmbeddedCartridgeAssert(application.getEmbeddedCartridge(rockMongo))
				.hasUrl();
	}
	
	@Test
	public void shouldEmbedPhpMyAdmin() throws Exception {
		// pre-conditions
		EmbeddedCartridgeTestUtils.silentlyDestroyAllEmbeddedCartridges(application);
		assertThat(new ApplicationAssert(application)
				.hasNotEmbeddableCartridges(LatestVersionOf.mySQL())
				.hasNotEmbeddableCartridges(LatestVersionOf.phpMyAdmin()));

		// operation
		application.addEmbeddableCartridge(LatestVersionOf.mySQL());
		application.addEmbeddableCartridge(LatestVersionOf.phpMyAdmin());

		// verification
		assertThat(new ApplicationAssert(application)
				.hasEmbeddedCartridges(LatestVersionOf.mySQL()))
				.hasEmbeddedCartridges(LatestVersionOf.phpMyAdmin());
	}

	@Test
	public void shouldHaveUrlInEmbeddedPhpMyadmin() throws SocketTimeoutException, OpenShiftException, URISyntaxException {
		// pre-conditions
		assertThat(new ApplicationAssert(application)).hasEmbeddedCartridges(LatestVersionOf.phpMyAdmin());
		
		// operation

		// verification
		IEmbeddableCartridge phpMyAdmin = 
				EmbeddedCartridgeTestUtils.getFirstEmbeddableCartridge(LatestVersionOf.phpMyAdmin(), user.getConnection());
		new EmbeddedCartridgeAssert(application.getEmbeddedCartridge(phpMyAdmin))
				.hasUrl();
	}

	@Test
	public void shouldEmbedJenkinsClient() throws Exception {
		// pre-conditions
		// need 2 free gears; jenkins + builder
		ApplicationTestUtils.silentlyDestroyAllApplicationsByCartridge(ICartridge.JENKINS_14, domain);
		ApplicationTestUtils.silentlyEnsureHasMaxApplication(1, domain);
		IApplication application = domain.getApplications().get(0);
		IApplication jenkins = domain.createApplication("j", ICartridge.JENKINS_14);
		assertTrue(jenkins.waitForAccessible(WAIT_FOR_APPLICATION));

		// operation
		application.addEmbeddableCartridge(LatestVersionOf.jenkinsClient());

		// verification
		assertThat(new ApplicationAssert(application)
				.hasEmbeddedCartridges(LatestVersionOf.jenkinsClient()));
		IEmbeddableCartridge jenkinsClient = 
				EmbeddedCartridgeTestUtils.getFirstEmbeddableCartridge(LatestVersionOf.jenkinsClient(), user.getConnection());
		new EmbeddedCartridgeAssert(application.getEmbeddedCartridge(jenkinsClient))
				.hasUrl();
	}

	@Test
	public void shouldHaveUrlInEmbeddedJenkinsClient() throws OpenShiftException, URISyntaxException, FileNotFoundException, IOException {
		// pre-conditions
		IApplication jenkinsApplication = ApplicationTestUtils.getOrCreateApplication(domain, ICartridge.JENKINS_14);
		assertThat(jenkinsApplication).isNotNull();
		EmbeddedCartridgeTestUtils.ensureHasEmbeddedCartridges(LatestVersionOf.jenkinsClient(), jenkinsApplication);
		assertThat(new ApplicationAssert(jenkinsApplication)).hasEmbeddedCartridges(LatestVersionOf.jenkinsClient());
		
		// operation

		// verification
		IUser user2 = new TestConnectionFactory().getConnection().getUser();
		IApplication user2Application = user2.getDefaultDomain().getApplicationByName(jenkinsApplication.getName());
		IEmbeddableCartridge jenkinsClient = 
				EmbeddedCartridgeTestUtils.getFirstEmbeddableCartridge(LatestVersionOf.jenkinsClient(), user2.getConnection());
		new EmbeddedCartridgeAssert(user2Application.getEmbeddedCartridge(jenkinsClient))
				.hasUrl();
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
		assertTrue(application.getEmbeddedCartridges().size() == numOfEmbeddedCartridges - 1);
		assertThat(new ApplicationAssert(application))
				.hasNotEmbeddableCartridge(EmbeddedCartridgeTestUtils.getLatestMySqlCartridge(user.getConnection()));
	}

	@Test
	public void shouldNotRemoveEmbeddedCartridgeThatWasNotAdded() throws SocketTimeoutException, OpenShiftException {
		// pre-conditions
		EmbeddedCartridgeTestUtils.silentlyDestroy(LatestVersionOf.mySQL(), application);
		int numOfEmbeddedCartridges = application.getEmbeddedCartridges().size();

		// operation
		application.removeEmbeddedCartridges(LatestVersionOf.mySQL());

		// verification
		IEmbeddableCartridge mySql = EmbeddedCartridgeTestUtils.getLatestMySqlCartridge(user.getConnection());
		assertThat(new ApplicationAssert(application))
				.hasEmbeddableCartridges(numOfEmbeddedCartridges)
				.hasNotEmbeddableCartridge(mySql.getName());
	}

	@Test
	public void shouldSeeCartridgeRemovedWithOtherUser() throws Exception {
		// pre-condition
		IEmbeddableCartridge mySqlEmbeddableCartridge = 
				EmbeddedCartridgeTestUtils.getLatestMySqlCartridge(user.getConnection());
		EmbeddedCartridgeTestUtils.ensureHasEmbeddedCartridge(mySqlEmbeddableCartridge, application);
		assertThat(new ApplicationAssert(application)
				.hasEmbeddedCartridges(LatestVersionOf.mySQL()));
		
		// operation
		IUser user2 = new TestConnectionFactory().getConnection().getUser();
		IApplication user2Application = user2.getDefaultDomain().getApplicationByName(application.getName());
		user2Application.removeEmbeddedCartridges(LatestVersionOf.mySQL());
		assertThat(new ApplicationAssert(user2Application)
				.hasNotEmbeddableCartridges(LatestVersionOf.mySQL()));
		
		// verification
		application.refresh();
		assertThat(new ApplicationAssert(application)
				.hasNotEmbeddableCartridges(LatestVersionOf.mySQL()));		
		assertEquals(application.getEmbeddedCartridges().size(), user2Application.getEmbeddedCartridges().size());
	}
}
