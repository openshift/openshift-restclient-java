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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.openshift.client.IHttpClient;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.EmbeddableCartridge;
import com.openshift.client.cartridge.ICartridge;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.client.cartridge.IEmbeddedCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.cartridge.StandaloneCartridge;
import com.openshift.client.cartridge.selector.LatestEmbeddableCartridge;
import com.openshift.client.cartridge.selector.LatestVersionOf;
import com.openshift.client.cartridge.selector.LatestVersionQuery;
import com.openshift.client.cartridge.selector.StringPropertyQuery;
import com.openshift.client.utils.CartridgeAssert;
import com.openshift.client.utils.Cartridges;
import com.openshift.client.utils.Samples;
import com.openshift.client.utils.TestConnectionFactory;

/**
 * @author Andre Dietisheim
 */
public class QueryTest {

	private IOpenShiftConnection connection;

	@Before
	public void setUp() throws OpenShiftException, FileNotFoundException, IOException {
		IHttpClient client = new HttpClientMockDirector()
				.mockGetCartridges(Samples.GET_CARTRIDGES)
				.client();
		this.connection = new TestConnectionFactory().getConnection(client);
	}

	@Test
	public void shouldEqualsOtherCartridgeConstraint() {
		// pre-coniditions
		// operation
		// verification
		assertEquals(
				new LatestVersionQuery("redhat"),
				new LatestVersionQuery("redhat"));
		assertFalse(new LatestVersionQuery("redhat").equals(
				new LatestVersionQuery("jboss")));
	}

	@Test
	public void shouldMatchEmbeddableCartridge() {
		// pre-coniditions
		LatestVersionQuery redhatSelector = new LatestVersionQuery("redhat");
		LatestVersionQuery jbossSelector = new LatestVersionQuery("jboss");
		IEmbeddableCartridge redhat10 = new EmbeddableCartridge("redhat-1.0");
		IEmbeddableCartridge redhat30 = new EmbeddableCartridge("redhat-3.0");
		IEmbeddableCartridge jboss10 = new EmbeddableCartridge("jboss-1.0");
		LatestVersionQuery closedSourceSelector = new LatestVersionQuery("closedsource");
		// operation
		// verification
		assertTrue(redhatSelector.matches(redhat10));
		assertTrue(redhatSelector.matches(redhat30));
		assertFalse(redhatSelector.matches(jboss10));
		assertTrue(jbossSelector.matches(jboss10));
		assertFalse(closedSourceSelector.matches(jboss10));
	}

	@Test
	public void shouldReturnEmptyListOnNoCartridges() {
		// pre-coniditions
		List<IEmbeddedCartridge> embeddedCartridges = Collections.emptyList();

		LatestVersionQuery selector = new LatestVersionQuery("mysql");

		// operation
		IEmbeddedCartridge matchingCartridge = selector.get(embeddedCartridges);

		// verification
		assertThat(matchingCartridge).isNull();
	}

	@Test
	public void shouldReturnEmptyListOnNoMatchingCartridge() {
		// pre-coniditions
		List<IEmbeddedCartridge> embeddedCartridges = Arrays.asList(
				createEmbeddedCartridgeMock("eclipsecon-2013"),
				createEmbeddedCartridgeMock("community")
				);

		LatestVersionQuery selector = new LatestVersionQuery("fun");

		// operation
		IEmbeddedCartridge matchingCartridge = selector.get(embeddedCartridges);

		// verification
		assertThat(matchingCartridge).isNull();
	}

	@Test
	public void shouldMatchMysql() {
		// pre-coniditions
		List<IEmbeddedCartridge> embeddedCartridges =
				Arrays.asList(createEmbeddedCartridgeMock(Cartridges.MYSQL_51_NAME));
		LatestVersionQuery cartridgeConstraint = new LatestVersionQuery("mysql");

		// operation
		IEmbeddedCartridge matchingCartridge = cartridgeConstraint.get(embeddedCartridges);

		// verification
		assertThat(matchingCartridge).isNotNull();
		assertThat(matchingCartridge.getName()).isEqualTo(Cartridges.MYSQL_51_NAME);
	}

	@Test
	public void shouldMatchLatestMysql() {
		// pre-coniditions
		List<IEmbeddedCartridge> embeddedCartridges = Arrays.asList(
				createEmbeddedCartridgeMock(Cartridges.MYSQL_51_NAME),
				createEmbeddedCartridgeMock("mysql-5.0")
				);

		LatestVersionQuery cartridgeSelector = new LatestVersionQuery("mysql");

		// operation
		IEmbeddedCartridge latestMysql = cartridgeSelector.get(embeddedCartridges);

		// verification
		assertThat(latestMysql).isNotNull();
		assertThat(latestMysql.getName()).isEqualTo(Cartridges.MYSQL_51_NAME);
	}

	@Test
	public void shouldMatchMajorVersionedCartridge() {
		// pre-coniditions
		String cartridgeName = "vertx-2";
		List<IEmbeddedCartridge> embeddedCartridges = Arrays.asList(
				createEmbeddedCartridgeMock(cartridgeName),
				createEmbeddedCartridgeMock("mysql")
				);
		LatestEmbeddableCartridge constraint = new LatestEmbeddableCartridge("vertx");

		// operation
		IEmbeddedCartridge cartridge = constraint.get(embeddedCartridges);

		// verification
		assertThat(cartridge).isNotNull();
		assertThat(cartridge.getName()).isEqualTo(cartridgeName);
	}

	@Test
	public void shouldMatchLaterMajorVersion() {
		// pre-coniditions
		String mysql6Name = "mysql-6";
		List<IEmbeddedCartridge> embeddedCartridges = Arrays.asList(
				createEmbeddedCartridgeMock(mysql6Name),
				createEmbeddedCartridgeMock("mysql-5.0")
				);

		LatestVersionQuery cartridgeSelector = new LatestVersionQuery("mysql");

		// operation
		IEmbeddedCartridge latestMysql = cartridgeSelector.get(embeddedCartridges);

		// verification
		assertThat(latestMysql).isNotNull();
		assertThat(latestMysql.getName()).isEqualTo(mysql6Name);
	}

	@Test
	public void shouldMatchAlphanumericVersionedCartridge() {
		// pre-coniditions
		String cartridgeName = "somecartridge-7b";
		List<IEmbeddedCartridge> embeddedCartridges = Arrays.asList(
				createEmbeddedCartridgeMock(cartridgeName),
				createEmbeddedCartridgeMock("mysql-5.0")
				);
		LatestVersionQuery constraint = new LatestVersionQuery("somecartridge");

		// operation
		IEmbeddedCartridge cartridge = constraint.get(embeddedCartridges);

		// verification
		assertThat(cartridge).isNotNull();
		assertThat(cartridge.getName()).isEqualTo(cartridgeName);
	}

	@Test
	public void shouldMatchCartridgeNameWithDash() {
		// pre-coniditions
		String jenkins2 = "jenkins-client-2.0";
		List<IEmbeddedCartridge> embeddedCartridges = Arrays.asList(
				createEmbeddedCartridgeMock(jenkins2),
				createEmbeddedCartridgeMock("jenkins-client-1.4")
				);
		LatestVersionQuery constraint = new LatestVersionQuery("jenkins-client");

		// operation
		IEmbeddedCartridge cartridge = constraint.get(embeddedCartridges);

		// verification
		assertThat(cartridge).isNotNull();
		assertThat(cartridge.getName()).isEqualTo(jenkins2);
	}

	@Test
	public void shouldMatchingCartridgeName() {
		// pre-coniditions
		List<IEmbeddedCartridge> embeddedCartridges = Arrays.asList(
				createEmbeddedCartridgeMock("Timberlake"),
				createEmbeddedCartridgeMock("TimAndStrupi")
				);

		StringPropertyQuery selector = new StringPropertyQuery("Tim.*") {

			@Override
			protected <C extends ICartridge> String getProperty(C cartridge) {
				return cartridge.getName();
			}

		};

		// operation
		IEmbeddedCartridge matchingCartridge = selector.get(embeddedCartridges);
		Collection<IEmbeddedCartridge> matchingCartridges = selector.getAll(embeddedCartridges);

		// verification
		assertThat(matchingCartridge).isNotNull();
		assertThat(matchingCartridge.getName()).isEqualTo("Timberlake");
		assertThat(matchingCartridges).onProperty("name").containsOnly("Timberlake", "TimAndStrupi");
	}

	@Test
	public void shouldSelectNodeJsByDescriptionRegex() {
		// pre-coniditions
		List<StandaloneCartridge> standaloneCartridges = Arrays.asList(
				new StandaloneCartridge("bingobongo"),
				new StandaloneCartridge("bongomongo"),
				new StandaloneCartridge("nodejs-0.6", null,
						"Node.js is a platform built on Chrome's JavaScript runtime for easily building fast, "
								+ "scalable network applications. Node.js is perfect for data-intensive real-time "
								+ "applications that run across distributed devices.")
				);
		StringPropertyQuery query = new StringPropertyQuery(".+platform built on Chrome's JavaScript runtime.+") {

			@Override
			protected <C extends ICartridge> String getProperty(C cartridge) {
				return cartridge.getDescription();
			}
		};
		// operation
		IStandaloneCartridge nodeJs = query.get(standaloneCartridges);

		// verification
		new CartridgeAssert<IStandaloneCartridge>(nodeJs)
				.hasName("nodejs-0.6");

	}

	@Test
	public void shouldSelectJBossAs() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IStandaloneCartridge.NAME_JBOSSAS, LatestVersionOf.jbossAs().get(connection.getStandaloneCartridges()));
	}

	@Test
	public void shouldSelectJBossEap() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IStandaloneCartridge.NAME_JBOSSEAP, LatestVersionOf.jbossEap().get(connection.getStandaloneCartridges()));
	}

	@Test
	public void shouldSelectJBossEws() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IStandaloneCartridge.NAME_JBOSSEWS, LatestVersionOf.jbossEws().get(connection.getStandaloneCartridges()));
	}

	@Test
	public void shouldSelectJenkins() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IStandaloneCartridge.NAME_JENKINS, LatestVersionOf.jenkins().get(connection.getStandaloneCartridges()));
	}

	@Test
	public void shouldSelectPerl() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IStandaloneCartridge.NAME_PERL, LatestVersionOf.perl().get(connection.getStandaloneCartridges()));
	}

	@Test
	public void shouldSelectPhp() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IStandaloneCartridge.NAME_PHP, LatestVersionOf.php().get(connection.getStandaloneCartridges()));
	}

	@Test
	public void shouldSelectPython() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IStandaloneCartridge.NAME_PYTHON, LatestVersionOf.python().get(connection.getStandaloneCartridges()));
	}

	@Test
	public void shouldSelectRuby() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IStandaloneCartridge.NAME_RUBY, LatestVersionOf.ruby().get(connection.getStandaloneCartridges()));
	}

	@Test
	public void shouldSelectZend() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IStandaloneCartridge.NAME_ZEND, LatestVersionOf.zend().get(connection.getStandaloneCartridges()));
	}

	@Test
	public void shouldSelectMmsAgent() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IEmbeddedCartridge.NAME_10GEN_MMS_AGENT, 
				LatestVersionOf.mmsAgent().get(connection.getEmbeddableCartridges()));
	}

	@Test
	public void shouldSelectHaProxy() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IEmbeddedCartridge.NAME_HAPROXY, 
				LatestVersionOf.haProxy().get(connection.getEmbeddableCartridges()));
	}

	@Test
	public void shouldSelectJenkinsClient() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IEmbeddedCartridge.NAME_JENKINS_CLIENT, 
				LatestVersionOf.jenkinsClient().get(connection.getEmbeddableCartridges()));
	}

	@Test
	public void shouldSelectMetrics() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IEmbeddedCartridge.NAME_METRICS, 
				LatestVersionOf.metrics().get(connection.getEmbeddableCartridges()));
	}

	@Test
	public void shouldSelectMongoDb() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IEmbeddedCartridge.NAME_MONGODB, 
				LatestVersionOf.mongoDB().get(connection.getEmbeddableCartridges()));
	}

	@Test
	public void shouldSelectMySql() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IEmbeddedCartridge.NAME_MYSQL, 
				LatestVersionOf.mySQL().get(connection.getEmbeddableCartridges()));
	}

	@Test
	public void shouldSelectPhpMyAdmin() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IEmbeddedCartridge.NAME_PHPMYADMIN, 
				LatestVersionOf.phpMyAdmin().get(connection.getEmbeddableCartridges()));
	}

	@Test
	public void shouldSelectpostgreSql() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IEmbeddedCartridge.NAME_POSTGRESQL, 
				LatestVersionOf.postgreSQL().get(connection.getEmbeddableCartridges()));
	}

	@Test
	public void shouldSelectRockmongo() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IEmbeddedCartridge.NAME_ROCKMONGO, 
				LatestVersionOf.rockMongo().get(connection.getEmbeddableCartridges()));
	}

	private IEmbeddedCartridge createEmbeddedCartridgeMock(String name) {
		IEmbeddedCartridge mock = mock(IEmbeddedCartridge.class);
		when(mock.getName()).thenReturn(name);
		return mock;
	}

	private void assertCartridge(String expectedName, ICartridge cartridge) {
		assertThat(cartridge).isNotNull();
		assertThat(cartridge.getName()).startsWith(expectedName);
	}
}
