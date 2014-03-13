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
import java.util.List;

import org.fest.assertions.Condition;
import org.junit.Before;
import org.junit.Test;

import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftEndpointException;
import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.ICartridge;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.IEmbeddedCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.cartridge.selector.LatestVersionOf;
import com.openshift.client.utils.ApplicationAssert;
import com.openshift.client.utils.ApplicationTestUtils;
import com.openshift.client.utils.Cartridges;
import com.openshift.client.utils.DomainTestUtils;
import com.openshift.client.utils.EmbeddedCartridgeAssert;
import com.openshift.client.utils.EmbeddedCartridgeTestUtils;
import com.openshift.client.utils.TestConnectionFactory;

/**
 * @author André Dietisheim
 */
public class EmbeddedCartridgeResourceIntegrationTest extends TestTimer {

	private IDomain domain;
	private IUser user;

	@Before
	public void setUp() throws OpenShiftException, IOException {
		this.user = new TestConnectionFactory().getConnection().getUser();
		this.domain = DomainTestUtils.ensureHasDomain(user);
	}

	@Test
	public void shouldReturnEmbeddedCartridgesForApplication() throws SocketTimeoutException, OpenShiftException {
		// pre-conditions
		IStandaloneCartridge jbossAs = LatestVersionOf.jbossAs().get(user);
		assertThat(jbossAs).isNotNull();
		IApplication application = ApplicationTestUtils.ensureHasExactly1Application(jbossAs, domain);

		// operation
		List<IEmbeddedCartridge> embeddedCartridges = application.getEmbeddedCartridges();
		// verification
		assertThat(embeddedCartridges).isNotNull();
	}

	@Test
	public void shouldNotContainTypeInEmbeddedCartridges() throws SocketTimeoutException, OpenShiftException {
		// pre-conditions
		final IStandaloneCartridge jbossAs = LatestVersionOf.jbossAs().get(user);
		assertThat(jbossAs).isNotNull();
		IApplication application = ApplicationTestUtils.ensureHasExactly1Application(jbossAs, domain);

		// operation
		List<IEmbeddedCartridge> embeddedCartridges = application.getEmbeddedCartridges();
		// verification
		assertThat(embeddedCartridges).doesNotSatisfy(new Condition<List<?>>() {
			
			@Override
			public boolean matches(List<?> values) {
				for(Object value : values) {
					if (!(value instanceof ICartridge)) {
						continue;
					}
					if (jbossAs.getName().equals(((ICartridge)value).getName())) {
						return true;
					};
				}
				return false;
			}
		});
	}

	@Test
	public void shouldEmbedMySQL() throws SocketTimeoutException, OpenShiftException, URISyntaxException {
		// pre-conditions
		IApplication application = ApplicationTestUtils.ensureHasExactly1Application(
				LatestVersionOf.jbossAs(), domain);
		EmbeddedCartridgeTestUtils.silentlyDestroyAllEmbeddedCartridges(application);
		IEmbeddableCartridge mysql = LatestVersionOf.mySQL().get(user);
		assertThat(mysql).isNotNull();
		assertThat(new ApplicationAssert(application)).hasNotEmbeddableCartridge(mysql);
		int numOfEmbeddedCartridges = application.getEmbeddedCartridges().size();

		// operation
		application.addEmbeddableCartridge(mysql);

		// verification
		assertThat(new ApplicationAssert(application))
				.hasEmbeddableCartridges(numOfEmbeddedCartridges + 1)
				.hasEmbeddedCartridge(LatestVersionOf.mySQL());
	}

	/**
	 * Verify in application created with different user instance.
	 */
	@Test
	public void shouldHaveUrlInEmbeddedMySQL() throws OpenShiftException, URISyntaxException, FileNotFoundException,
			IOException {
		// pre-conditions
		IApplication application = ApplicationTestUtils.ensureHasExactly1Application(
				LatestVersionOf.jbossAs(), domain);
		EmbeddedCartridgeTestUtils.ensureHasEmbeddedCartridges(LatestVersionOf.mySQL(), application);
		// verify using user instance that's not the one used to create
		IUser user2 = new TestConnectionFactory().getConnection().getUser();
		IApplication user2Application = user2.getDefaultDomain().getApplicationByName(application.getName());
		assertThat(new ApplicationAssert(user2Application))
				.hasEmbeddedCartridge(LatestVersionOf.mySQL());

		// operation

		// verification
		IEmbeddableCartridge mysql =
				LatestVersionOf.mySQL().get(user2);
		assertThat(mysql).isNotNull();		
		new EmbeddedCartridgeAssert(user2Application.getEmbeddedCartridge(mysql))
				.hasUrlProperty();
	}

	/**
	 * Verify in application created with different user instance.
	 */
	@Test
	public void shouldHaveDescriptionAndDisplayNameInEmbeddedMySQL() throws OpenShiftException, URISyntaxException, FileNotFoundException,
			IOException {
		// pre-conditions
		IApplication application = ApplicationTestUtils.ensureHasExactly1Application(
				LatestVersionOf.jbossAs(), domain);
		EmbeddedCartridgeTestUtils.ensureHasEmbeddedCartridges(LatestVersionOf.mySQL(), application);
		// verify using user instance that's not the one used to create

		// operation
		IEmbeddedCartridge mysql = application.getEmbeddedCartridge(LatestVersionOf.mySQL().get(user));
		
		// verification
		new EmbeddedCartridgeAssert(mysql)
				.hasDescription()
				.hasDisplayName();
	}

	@Test
	public void shouldReturnThatHasMySQL() throws OpenShiftException, FileNotFoundException, IOException {
		// pre-conditions
		IApplication application = ApplicationTestUtils.ensureHasExactly1Application(
				LatestVersionOf.jbossAs(), domain);
		EmbeddedCartridgeTestUtils.ensureHasEmbeddedCartridges(
				LatestVersionOf.mySQL(), application);
		// verify using user instance that's not the one used to create
		IUser user2 = new TestConnectionFactory().getConnection().getUser();
		IApplication user2Application = user2.getDefaultDomain().getApplicationByName(application.getName());
		EmbeddedCartridgeTestUtils.ensureHasEmbeddedCartridges(
				LatestVersionOf.mySQL(), user2Application);

		// operation

		// verification
		assertThat(new ApplicationAssert(user2Application))
				.hasEmbeddedCartridge(LatestVersionOf.mySQL());
	}

	@Test
	public void shouldEmbedPostgreSQL() throws SocketTimeoutException, OpenShiftException, URISyntaxException {
		// pre-conditions
		IApplication application = ApplicationTestUtils.ensureHasExactly1Application(
				LatestVersionOf.jbossAs(), domain);
		EmbeddedCartridgeTestUtils.silentlyDestroyAllEmbeddedCartridges(application);
		IEmbeddableCartridge postgres = LatestVersionOf.postgreSQL().get(user);
		assertThat(new ApplicationAssert(application))
			.hasNotEmbeddableCartridge(postgres);

		// operation
		application.addEmbeddableCartridge(postgres);

		// verification
		assertThat(new ApplicationAssert(application))
				.hasEmbeddedCartridge(LatestVersionOf.postgreSQL());
		new EmbeddedCartridgeAssert(application.getEmbeddedCartridge(postgres))
				.hasUrlProperty();
	}

	@Test
	public void shouldHaveUrlInEmbeddedPostgres() throws OpenShiftException, URISyntaxException, FileNotFoundException,
			IOException {
		// pre-conditions
		IApplication application = ApplicationTestUtils.ensureHasExactly1Application(
				LatestVersionOf.jbossAs(), domain);
		EmbeddedCartridgeTestUtils.ensureHasEmbeddedCartridges(
				LatestVersionOf.postgreSQL(), application);
		// verify using user instance that's not the one used to create
		IUser user2 = new TestConnectionFactory().getConnection().getUser();
		IApplication user2Application = user2.getDefaultDomain().getApplicationByName(application.getName());
		assertThat(new ApplicationAssert(user2Application)).hasEmbeddedCartridge(
				LatestVersionOf.postgreSQL());

		// operation

		// verification
		IEmbeddableCartridge postgres2 = LatestVersionOf.postgreSQL().get(user2);
		new EmbeddedCartridgeAssert(user2Application.getEmbeddedCartridge(postgres2))
				.hasUrlProperty();
	}

	@Test
	public void shouldEmbedMongo() throws Exception {
		// pre-conditions
		IApplication application = ApplicationTestUtils.ensureHasExactly1Application(LatestVersionOf.jbossAs(), domain);
		EmbeddedCartridgeTestUtils.silentlyDestroyAllEmbeddedCartridges(application);
		IEmbeddableCartridge mongo = LatestVersionOf.mongoDB().get(user);
		assertThat(new ApplicationAssert(application))
			.hasNotEmbeddableCartridge(mongo);

		// operation
		application.addEmbeddableCartridge(LatestVersionOf.mongoDB().get(user));

		// verification
		assertThat(new ApplicationAssert(application)
				.hasEmbeddedCartridge(LatestVersionOf.mongoDB()));
		new EmbeddedCartridgeAssert(application.getEmbeddedCartridge(mongo))
				.hasUrlProperty();
	}

	@Test
	public void shouldHaveUrlInEmbeddedMongo() throws OpenShiftException, URISyntaxException, FileNotFoundException,
			IOException {
		// pre-conditions
		IApplication jbossAs = ApplicationTestUtils.getOrCreateApplication(domain, LatestVersionOf.jbossAs().get(user));
		EmbeddedCartridgeTestUtils.silentlyDestroyAllEmbeddedCartridges(jbossAs);

		// operation
		jbossAs.addEmbeddableCartridge(LatestVersionOf.mongoDB().get(user));

		// verification
		new EmbeddedCartridgeAssert(jbossAs.getEmbeddedCartridge(LatestVersionOf.mongoDB().get(user)))
				.hasUrlProperty();
		// verify using user instance that's not the one used to create
		IUser user2 = new TestConnectionFactory().getConnection().getUser();
		IApplication user2Application = user2.getDefaultDomain().getApplicationByName(jbossAs.getName());
		IEmbeddableCartridge mongo = LatestVersionOf.mongoDB().get(user2);
		new EmbeddedCartridgeAssert(user2Application.getEmbeddedCartridge(mongo))
				.hasUrlProperty();
	}

	@Test
	public void shouldEmbedRockMongo() throws Exception {
		// pre-conditions
		// have to make sure have non-scalable app without cartridges
		IApplication jbossAs = ApplicationTestUtils.getOrCreateApplication(domain, LatestVersionOf.jbossAs().get(user));
		jbossAs = ApplicationTestUtils.destroyAndRecreateIfScalable(jbossAs);
		EmbeddedCartridgeTestUtils.silentlyDestroyAllEmbeddedCartridges(jbossAs);

		assertThat(new ApplicationAssert(jbossAs)
				.hasNotEmbeddableCartridges(LatestVersionOf.mongoDB())
				.hasNotEmbeddableCartridges(LatestVersionOf.rockMongo()));

		// operation
		jbossAs.addEmbeddableCartridge(LatestVersionOf.mongoDB().get(user));
		jbossAs.addEmbeddableCartridge(LatestVersionOf.rockMongo().get(user));

		// verification
		assertThat(new ApplicationAssert(jbossAs)
				.hasEmbeddedCartridge(LatestVersionOf.mongoDB())
				.hasEmbeddedCartridge(LatestVersionOf.rockMongo()));
	}

	@Test
	public void shouldHaveUrlInEmbeddedRockMongo() 
			throws OpenShiftException, URISyntaxException, FileNotFoundException, IOException {
		// pre-conditions
		IApplication jbossAs = ApplicationTestUtils.getOrCreateApplication(domain, LatestVersionOf.jbossAs().get(user));
		jbossAs = ApplicationTestUtils.destroyAndRecreateIfScalable(jbossAs);
		EmbeddedCartridgeTestUtils.silentlyDestroyAllEmbeddedCartridges(jbossAs);
		assertThat(new ApplicationAssert(jbossAs)
				.hasNotEmbeddableCartridges(LatestVersionOf.mongoDB())
				.hasNotEmbeddableCartridges(LatestVersionOf.rockMongo()));

		// operation
		jbossAs.addEmbeddableCartridge(LatestVersionOf.mongoDB().get(user));
		jbossAs.addEmbeddableCartridge(LatestVersionOf.rockMongo().get(user));

		// verification
		IEmbeddableCartridge rockMongo = LatestVersionOf.rockMongo().get(user);
		new EmbeddedCartridgeAssert(jbossAs.getEmbeddedCartridge(rockMongo))
				.hasUrlProperty();
		// verify using user instance that's not the one used to create
		IUser user2 = new TestConnectionFactory().getConnection().getUser();
		IApplication user2Application = user2.getDefaultDomain().getApplicationByName(jbossAs.getName());
		assertThat(new ApplicationAssert(user2Application)).hasEmbeddedCartridges(
				LatestVersionOf.mongoDB(), LatestVersionOf.rockMongo());
		new EmbeddedCartridgeAssert(user2Application.getEmbeddedCartridge(rockMongo))
				.hasUrlProperty();
	}

	@Test
	public void shouldEmbedPhpMyAdmin() throws Exception {
		// pre-conditions
		IApplication jbossAs = ApplicationTestUtils.getOrCreateApplication(domain, LatestVersionOf.jbossAs().get(user));
		jbossAs = ApplicationTestUtils.destroyAndRecreateIfScalable(jbossAs);
		EmbeddedCartridgeTestUtils.silentlyDestroyAllEmbeddedCartridges(jbossAs);
		assertThat(new ApplicationAssert(jbossAs)
				.hasNotEmbeddableCartridges(LatestVersionOf.mySQL())
				.hasNotEmbeddableCartridges(LatestVersionOf.phpMyAdmin()));

		// operation
		jbossAs.addEmbeddableCartridge(LatestVersionOf.mySQL().get(user));
		jbossAs.addEmbeddableCartridge(LatestVersionOf.phpMyAdmin().get(user));

		// verification
		assertThat(new ApplicationAssert(jbossAs)
				.hasEmbeddedCartridge(LatestVersionOf.mySQL()))
				.hasEmbeddedCartridge(LatestVersionOf.phpMyAdmin());
	}

	@Test
	public void shouldHaveUrlInEmbeddedPhpMyadmin()
			throws OpenShiftException, URISyntaxException, FileNotFoundException, IOException {
		// pre-conditions
		// pre-conditions
		IApplication jbossAs = ApplicationTestUtils.getOrCreateApplication(domain, LatestVersionOf.jbossAs().get(user));
		jbossAs = ApplicationTestUtils.destroyAndRecreateIfScalable(jbossAs);
		EmbeddedCartridgeTestUtils.silentlyDestroyAllEmbeddedCartridges(jbossAs);
		assertThat(new ApplicationAssert(jbossAs)
				.hasNotEmbeddableCartridges(LatestVersionOf.mySQL())
				.hasNotEmbeddableCartridges(LatestVersionOf.phpMyAdmin()));

		// operation
		jbossAs.addEmbeddableCartridge(LatestVersionOf.mySQL().get(user));
		jbossAs.addEmbeddableCartridge(LatestVersionOf.phpMyAdmin().get(user));

		// verification
		IEmbeddableCartridge phpMyadmin = LatestVersionOf.phpMyAdmin().get(user);
		new EmbeddedCartridgeAssert(jbossAs.getEmbeddedCartridge(phpMyadmin))
				.hasUrlProperty();
		// verify using user instance that's not the one used to create
		IUser user2 = new TestConnectionFactory().getConnection().getUser();
		IApplication user2Application = user2.getDefaultDomain().getApplicationByName(jbossAs.getName());
		IEmbeddableCartridge phpMyAdmin = LatestVersionOf.phpMyAdmin().get(user2);
		new EmbeddedCartridgeAssert(user2Application.getEmbeddedCartridge(phpMyAdmin))
				.hasUrlProperty();
	}

	@Test
	public void shouldEmbedJenkinsClient() throws Exception {
		// pre-conditions
		// need 2 free gears; jenkins + builder
		IApplication application = ApplicationTestUtils.ensureHasExactly1Application(
				LatestVersionOf.jbossAs(), domain);
		EmbeddedCartridgeTestUtils.silentlyDestroyAllEmbeddedCartridges(application);
		ApplicationTestUtils.createApplication(
				LatestVersionOf.jenkins().get(user), domain);

		// operation
		application.addEmbeddableCartridge(LatestVersionOf.jenkinsClient().get(user));

		// verification
		assertThat(new ApplicationAssert(application)
				.hasEmbeddedCartridge(LatestVersionOf.jenkinsClient()));
	}

	/**
	 * Verify in application created with different user instance.
	 * 
	 * @throws SocketTimeoutException
	 * @throws OpenShiftException
	 * @throws URISyntaxException
	 */
	@Test
	public void shouldHaveUrlInEmbeddedJenkinsClient() throws OpenShiftException, URISyntaxException,
			FileNotFoundException, IOException {
		// pre-conditions
		IApplication jbossAs = ApplicationTestUtils.getOrCreateApplication(domain, LatestVersionOf.jbossAs().get(user));
		jbossAs = ApplicationTestUtils.destroyAndRecreateIfScalable(jbossAs);
		EmbeddedCartridgeTestUtils.silentlyDestroyAllEmbeddedCartridges(jbossAs);
		ApplicationTestUtils.getOrCreateApplication(domain, LatestVersionOf.jenkins().get(user));

		// operation
		EmbeddedCartridgeTestUtils.ensureHasEmbeddedCartridges(LatestVersionOf.jenkinsClient(), jbossAs);

		// verification
		new EmbeddedCartridgeAssert(jbossAs.getEmbeddedCartridge(LatestVersionOf.jenkinsClient().get(user)))
				.hasUrlProperty();
		// verify using user instance that's not the one used to create
		IUser user2 = new TestConnectionFactory().getConnection().getUser();
		IApplication user2Application = user2.getDefaultDomain().getApplicationByName(jbossAs.getName());
		IEmbeddableCartridge jenkinsClient =
				LatestVersionOf.jenkinsClient().get(user2);
		new EmbeddedCartridgeAssert(user2Application.getEmbeddedCartridge(jenkinsClient))
				.hasUrlProperty();
	}

	@Test
	public void shouldEmbedDownloadableCartridge() throws Exception {
		// pre-conditions
		IApplication application = ApplicationTestUtils.ensureHasExactly1Application(LatestVersionOf.jbossAs(), domain);
		EmbeddedCartridgeTestUtils.silentlyDestroyAllEmbeddedCartridges(application);
		assertThat(new ApplicationAssert(application))
			.hasNotEmbeddableCartridge(Cartridges.foreman063());

		// operation
		application.addEmbeddableCartridge(Cartridges.foreman063());

		// verification
		new ApplicationAssert(application)
				.hasEmbeddedCartridgeNames(Cartridges.foreman063().getName());
	}
	
	@Test(expected = OpenShiftEndpointException.class)
	public void shouldNotAddEmbeddedCartridgeTwice() throws Exception {
		// pre-conditions
		IApplication application = ApplicationTestUtils.ensureHasExactly1Application(
				LatestVersionOf.jbossAs(), domain);
		EmbeddedCartridgeTestUtils.ensureHasEmbeddedCartridges(LatestVersionOf.mySQL(), application);

		// operation
		application.addEmbeddableCartridge(LatestVersionOf.mySQL().get(user));
	}

	@Test
	public void shouldRemoveEmbeddedCartridge() throws Exception {
		// pre-conditions
		IApplication application = ApplicationTestUtils.ensureHasExactly1Application(
				LatestVersionOf.jbossAs(), domain);
		EmbeddedCartridgeTestUtils.ensureHasEmbeddedCartridges(LatestVersionOf.mySQL(), application);
		int numOfEmbeddedCartridges = application.getEmbeddedCartridges().size();

		// operation
		application.removeEmbeddedCartridge(LatestVersionOf.mySQL().get(user));

		// verification
		assertTrue(application.getEmbeddedCartridges().size() == numOfEmbeddedCartridges - 1);
		assertThat(new ApplicationAssert(application))
				.hasNotEmbeddableCartridge(LatestVersionOf.mySQL());
	}

	@Test
	public void shouldNotRemoveEmbeddedCartridgeThatWasNotAdded() throws SocketTimeoutException, OpenShiftException {
		// pre-conditions
		IApplication application = ApplicationTestUtils.ensureHasExactly1Application(
				LatestVersionOf.jbossAs(), domain);
		EmbeddedCartridgeTestUtils.silentlyDestroy(LatestVersionOf.mySQL(), application);
		int numOfEmbeddedCartridges = application.getEmbeddedCartridges().size();

		// operation
		application.removeEmbeddedCartridge(LatestVersionOf.mySQL().get(user));

		// verification
		IEmbeddableCartridge mySql = LatestVersionOf.mySQL().get(user);
		assertThat(new ApplicationAssert(application))
				.hasEmbeddableCartridges(numOfEmbeddedCartridges)
				.hasNotEmbeddableCartridge(mySql.getName());
	}

	@Test
	public void shouldSeeCartridgeRemovedWithOtherUser() throws Exception {
		// pre-condition
		IApplication application = ApplicationTestUtils.ensureHasExactly1Application(
				LatestVersionOf.jbossAs(), domain);
		IEmbeddableCartridge mySqlEmbeddableCartridge =
				LatestVersionOf.mySQL().get(user);
		EmbeddedCartridgeTestUtils.ensureHasEmbeddedCartridge(mySqlEmbeddableCartridge, application);
		assertThat(new ApplicationAssert(application)
				.hasEmbeddedCartridge(LatestVersionOf.mySQL()));

		// operation
		// use user instance that's not the one used to create
		IUser user2 = new TestConnectionFactory().getConnection().getUser();
		IApplication user2Application = user2.getDefaultDomain().getApplicationByName(application.getName());
		user2Application.removeEmbeddedCartridge(LatestVersionOf.mySQL().get(user2));
		assertThat(new ApplicationAssert(user2Application)
				.hasNotEmbeddableCartridges(LatestVersionOf.mySQL()));

		// verification
		application.refresh();
		assertThat(new ApplicationAssert(application)
				.hasNotEmbeddableCartridges(LatestVersionOf.mySQL()));
		assertEquals(application.getEmbeddedCartridges().size(), user2Application.getEmbeddedCartridges().size());
	}
}
