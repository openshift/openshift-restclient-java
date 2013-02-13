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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import com.openshift.client.IApplication;
import com.openshift.client.ICartridge;
import com.openshift.client.IDomain;
import com.openshift.client.IEmbeddableCartridge;
import com.openshift.client.IEmbeddedCartridge;
import com.openshift.client.IUser;
import com.openshift.client.LatestVersionOf;
import com.openshift.client.OpenShiftEndpointException;
import com.openshift.client.OpenShiftException;
import com.openshift.client.utils.ApplicationAssert;
import com.openshift.client.utils.ApplicationTestUtils;
import com.openshift.client.utils.DomainTestUtils;
import com.openshift.client.utils.EmbeddableCartridgeAsserts;
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
	}

	@Test
	public void shouldReturnEmbeddedCartridges() throws SocketTimeoutException, OpenShiftException {
		assertTrue(application.getEmbeddedCartridges().size() >= 0);
	}
	
	@Test
	public void shouldEmbedMySQL() throws SocketTimeoutException, OpenShiftException {
		// pre-conditions
		EmbeddedCartridgeTestUtils.silentlyDestroy(LatestVersionOf.mySQL(), application);
		assertThat(application.getEmbeddedCartridges(LatestVersionOf.mySQL())).isEmpty();
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

	@Test
	public void shouldReturnThatHasMySQL() throws SocketTimeoutException, OpenShiftException {
		// pre-conditions
		IEmbeddableCartridge mySQL = 
				EmbeddedCartridgeTestUtils.getLatestMySqlCartridge(user.getConnection());
		EmbeddedCartridgeTestUtils.silentlyDestroy(mySQL, application);

		// operation
		application.addEmbeddableCartridge(mySQL);

		// verification
		assertTrue(application.hasEmbeddedCartridge(mySQL));
	}

	@Test
	public void shouldEmbedMongo() throws Exception {
		// pre-conditions
		EmbeddedCartridgeTestUtils.silentlyDestroy(LatestVersionOf.mongoDB(), application);
		assertThat(new ApplicationAssert(application)
				.hasNotEmbeddableCartridges(LatestVersionOf.mongoDB()));

		// operation
		application.addEmbeddableCartridge(LatestVersionOf.mongoDB());

		// verification
		assertThat(new ApplicationAssert(application)
				.hasEmbeddableCartridges(LatestVersionOf.mongoDB()));
	}

	@Test
	public void shouldEmbedRockMongo() throws Exception {
		// pre-conditions
		EmbeddedCartridgeTestUtils.silentlyDestroy(LatestVersionOf.mongoDB(), application);
		EmbeddedCartridgeTestUtils.silentlyDestroy(LatestVersionOf.rockMongo(), application);
		assertThat(new ApplicationAssert(application)
			.hasNotEmbeddableCartridges(LatestVersionOf.mongoDB())
			.hasNotEmbeddableCartridges(LatestVersionOf.rockMongo()));

		// operation
		application.addEmbeddableCartridge(LatestVersionOf.mongoDB());
		application.addEmbeddableCartridge(LatestVersionOf.rockMongo());

		// verification
		assertThat(new ApplicationAssert(application)
				.hasEmbeddableCartridges(LatestVersionOf.mongoDB())
				.hasEmbeddableCartridges(LatestVersionOf.rockMongo()));
	}

	@Test
	public void shouldEmbedPhpMyAdmin() throws Exception {
		// pre-conditions
		EmbeddedCartridgeTestUtils.silentlyDestroy(LatestVersionOf.mySQL(), application);
		EmbeddedCartridgeTestUtils.silentlyDestroy(LatestVersionOf.phpMyAdmin(), application);
		assertThat(new ApplicationAssert(application)
				.hasNotEmbeddableCartridges(LatestVersionOf.mySQL())
				.hasNotEmbeddableCartridges(LatestVersionOf.phpMyAdmin()));

		// operation
		application.addEmbeddableCartridge(LatestVersionOf.mySQL());
		application.addEmbeddableCartridge(LatestVersionOf.phpMyAdmin());

		// verification
		assertThat(new ApplicationAssert(application)
				.hasEmbeddableCartridges(LatestVersionOf.mySQL()))
				.hasEmbeddableCartridges(LatestVersionOf.phpMyAdmin());
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
		application.addEmbeddableCartridge(LatestVersionOf.jenkinsClient());

		// verification
		assertThat(new ApplicationAssert(application)
				.hasEmbeddableCartridges(LatestVersionOf.jenkinsClient()));
	}

	/**
	 * https://issues.jboss.org/browse/JBIDE-13631
	 */
	@Test
	public void embeddedCartridgeShouldHaveUrl() throws OpenShiftException, MalformedURLException {
		// pre-conditions
		IEmbeddableCartridge mySqlEmbeddableCartridge = EmbeddedCartridgeTestUtils.getLatestMySqlCartridge(user.getConnection());
		EmbeddedCartridgeTestUtils.ensureHasEmbeddedCartridge(mySqlEmbeddableCartridge, application);

		// operation
		IEmbeddedCartridge mySqlEmbeddedCartridge = application.getEmbeddedCartridge(mySqlEmbeddableCartridge);

		// verification
		assertThat(mySqlEmbeddedCartridge).isNotNull();
		assertThat(mySqlEmbeddedCartridge.getUrl()).isNotEmpty();
		new URL(mySqlEmbeddedCartridge.getUrl());
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
		EmbeddableCartridgeAsserts.assertThatDoesntContainCartridge(
				EmbeddedCartridgeTestUtils.getLatestMySqlCartridge(user.getConnection()),
				application.getEmbeddedCartridges());
	}

	@Test
	public void shouldNotRemoveEmbeddedCartridgeThatWasNotAdded() throws SocketTimeoutException, OpenShiftException {
		// pre-conditions
		EmbeddedCartridgeTestUtils.silentlyDestroy(LatestVersionOf.mySQL(), application);
		int numOfEmbeddedCartridges = application.getEmbeddedCartridges().size();

		// operation
		application.removeEmbeddedCartridges(LatestVersionOf.mySQL());

		// verification
		assertTrue(application.getEmbeddedCartridges().size() == numOfEmbeddedCartridges);
		IEmbeddableCartridge mySql = EmbeddedCartridgeTestUtils.getLatestMySqlCartridge(user.getConnection());
		EmbeddableCartridgeAsserts.assertThatDoesntContainCartridge(
				mySql.getName(), application.getEmbeddedCartridges());
	}

	@Test
	public void shouldSeeCartridgeRemovedWithOtherUser() throws Exception {
		// pre-condition
		IEmbeddableCartridge mySqlEmbeddableCartridge = EmbeddedCartridgeTestUtils.getLatestMySqlCartridge(user.getConnection());
		EmbeddedCartridgeTestUtils.ensureHasEmbeddedCartridge(mySqlEmbeddableCartridge, application);
		assertThat(new ApplicationAssert(application)
				.hasEmbeddableCartridges(LatestVersionOf.mySQL()));
		
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
