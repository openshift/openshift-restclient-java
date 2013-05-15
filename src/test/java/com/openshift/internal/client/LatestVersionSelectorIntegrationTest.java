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

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.openshift.client.IUser;
import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.ICartridge;
import com.openshift.client.cartridge.IEmbeddedCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.cartridge.selector.LatestVersionOf;
import com.openshift.client.utils.TestConnectionFactory;

/**
 * @author Andre Dietisheim
 */
public class LatestVersionSelectorIntegrationTest {

	private IUser user;

	@Before
	public void setUp() throws OpenShiftException, FileNotFoundException, IOException {
		this.user = new TestConnectionFactory().getConnection().getUser();
	}
	
	@Test
	public void shouldSelectJBossAs() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IStandaloneCartridge.NAME_JBOSSAS, LatestVersionOf.jbossAs().get(user)); 
	}

	@Test
	public void shouldSelectJBossEap() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IStandaloneCartridge.NAME_JBOSSEAP, LatestVersionOf.jbossEap().get(user)); 
	}

	@Test
	public void shouldSelectJBossEws() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IStandaloneCartridge.NAME_JBOSSEWS, LatestVersionOf.jbossEws().get(user)); 
	}

	@Test
	public void shouldSelectJenkins() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IStandaloneCartridge.NAME_JENKINS, LatestVersionOf.jenkins().get(user)); 
	}

	@Test
	public void shouldSelectPerl() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IStandaloneCartridge.NAME_PERL, LatestVersionOf.perl().get(user)); 
	}

	@Test
	public void shouldSelectPhp() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IStandaloneCartridge.NAME_PHP, LatestVersionOf.php().get(user)); 
	}

	@Test
	public void shouldSelectPython() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IStandaloneCartridge.NAME_PYTHON, LatestVersionOf.python().get(user)); 
	}

	@Test
	public void shouldSelectRuby() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IStandaloneCartridge.NAME_RUBY, LatestVersionOf.ruby().get(user)); 
	}

	@Test
	public void shouldSelectZend() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IStandaloneCartridge.NAME_ZEND, LatestVersionOf.zend().get(user)); 
	}

	@Test
	public void shouldSelectMmsAgent() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IEmbeddedCartridge.NAME_10GEN_MMS_AGENT, LatestVersionOf.mmsAgent().get(user)); 
	}

	
	@Test
	public void shouldSelectHaProxy() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IEmbeddedCartridge.NAME_HAPROXY, LatestVersionOf.haProxy().get(user)); 
	}

	@Test
	public void shouldSelectJenkinsClient() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IEmbeddedCartridge.NAME_JENKINS_CLIENT, LatestVersionOf.jenkinsClient().get(user)); 
	}

	@Test
	public void shouldSelectMetrics() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IEmbeddedCartridge.NAME_METRICS, LatestVersionOf.metrics().get(user)); 
	}

	@Test
	public void shouldSelectMongoDb() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IEmbeddedCartridge.NAME_MONGODB, LatestVersionOf.mongoDB().get(user)); 
	}

	@Test
	public void shouldSelectMySql() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IEmbeddedCartridge.NAME_MYSQL, LatestVersionOf.mySQL().get(user)); 
	}

	@Test
	public void shouldSelectPhpMyAdmin() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IEmbeddedCartridge.NAME_PHPMYADMIN, LatestVersionOf.phpMyAdmin().get(user)); 
	}

	@Test
	public void shouldSelectpostgreSql() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IEmbeddedCartridge.NAME_POSTGRESQL, LatestVersionOf.postgreSQL().get(user)); 
	}

	@Test
	public void shouldSelectRockmongo() {
		// pre-coniditions
		// operation
		// verification
		assertCartridge(IEmbeddedCartridge.NAME_ROCKMONGO, LatestVersionOf.rockMongo().get(user)); 
	}

	private void assertCartridge(String expectedName, ICartridge cartridge) {
		assertThat(cartridge).isNotNull();
		assertThat(cartridge.getName()).startsWith(expectedName);
	}
	
}
