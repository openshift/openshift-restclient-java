/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.express.internal.client.test;

import static com.openshift.express.internal.client.test.utils.EmbeddableCartridgeAsserts.assertThatContainsCartridge;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.openshift.express.client.IApplication;
import com.openshift.express.client.ICartridge;
import com.openshift.express.client.IEmbeddableCartridge;
import com.openshift.express.client.IJenkinsApplication;
import com.openshift.express.client.IOpenShiftService;
import com.openshift.express.client.OpenShiftException;
import com.openshift.express.client.OpenShiftService;
import com.openshift.express.client.User;
import com.openshift.express.client.configuration.DefaultConfiguration;
import com.openshift.express.client.configuration.OpenShiftConfiguration;
import com.openshift.express.client.configuration.SystemConfiguration;
import com.openshift.express.client.configuration.UserConfiguration;
import com.openshift.express.internal.client.JenkinsClientEmbeddableCartridge;
import com.openshift.express.internal.client.MetricsEmbeddableCartridge;
import com.openshift.express.internal.client.MySqlEmbeddableCartridge;
import com.openshift.express.internal.client.PHPMyAdminEmbeddableCartridge;
import com.openshift.express.internal.client.test.fakes.TestUser;
import com.openshift.express.internal.client.test.utils.ApplicationUtils;

/**
 * @author André Dietisheim
 */
public class EmbedIntegrationTest {

	private static final int WAIT_FOR_APPLICATION = 10 * 1000;
	private IOpenShiftService service;
	private User user;
	private IApplication application;

	@Before
	public void setUp() throws OpenShiftException, IOException {
		service = new OpenShiftService(TestUser.ID, new OpenShiftConfiguration().getLibraServer());
		service.setEnableSSLCertChecks(Boolean.parseBoolean(System.getProperty("enableSSLCertChecks")));
		user = new TestUser(service);
		application = service.createJBossASApplication(ApplicationUtils.createRandomApplicationName(), user );
	}

	@After
	public void tearDown() {
		ApplicationUtils.silentlyDestroyApplication(application.getName(), application.getCartridge(), user, service);
	}

	@Test
	public void canEmbedMySQL() throws Exception {
		MySqlEmbeddableCartridge mysql = new MySqlEmbeddableCartridge(service, user);
		IEmbeddableCartridge cartridge = service.addEmbeddedCartridge(application.getName(), mysql, user);
		assertEquals(mysql.getName(), cartridge.getName());
		assertThatContainsCartridge(mysql.getName(), application.getEmbeddedCartridges());
	}
	
	@Test
	public void canEmbedMetrics() throws Exception {
		MetricsEmbeddableCartridge metrics = new MetricsEmbeddableCartridge(service, user);
		IEmbeddableCartridge cartridge = service.addEmbeddedCartridge(application.getName(), metrics, user);
		assertEquals(metrics.getName(), cartridge.getName());
		assertThatContainsCartridge(metrics.getName(), application.getEmbeddedCartridges());
	}

	@Test
	public void canEmbedPhpMyAdmin() throws Exception {
		MySqlEmbeddableCartridge mysql = new MySqlEmbeddableCartridge(service, user);
		IEmbeddableCartridge cartridge = service.addEmbeddedCartridge(application.getName(), mysql, user);
		assertEquals(mysql.getName(), cartridge.getName());
		
		PHPMyAdminEmbeddableCartridge myadmin = new PHPMyAdminEmbeddableCartridge(service, user);
		cartridge = service.addEmbeddedCartridge(application.getName(), myadmin, user);
		assertEquals(myadmin.getName(), cartridge.getName());
		assertThatContainsCartridge(myadmin.getName(), application.getEmbeddedCartridges());
	}

	@Test
	public void canEmbedJenkins() throws Exception {
		ApplicationUtils.silentlyDestroyAnyJenkinsApplication(user);
		String jenkinsAppName = "jenkins";
		IJenkinsApplication jenkins = null;
		try {
			jenkins = service.createJenkinsApplication(jenkinsAppName, user);
			assertTrue(jenkins.waitForAccessible(WAIT_FOR_APPLICATION * 10));
			service.addEmbeddedCartridge(application.getName(), new JenkinsClientEmbeddableCartridge(service, user), user);
		} finally {
			ApplicationUtils.silentlyDestroyApplication(jenkinsAppName, jenkins.getCartridge(), user, service);
		}
	}
	
	@Test
	public void embeddedCartridgeHasUrl() throws OpenShiftException {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		IApplication application = null;
		try {
			application = service.createJBossASApplication(applicationName, user);
			MySqlEmbeddableCartridge mysql = new MySqlEmbeddableCartridge(service, user);
			application.addEmbbedCartridge(mysql);
			IEmbeddableCartridge embeddedCartridge = application.getEmbeddedCartridge(mysql.getName());
			assertNotNull(embeddedCartridge);
			assertNotNull(embeddedCartridge.getUrl());
		} finally {
			ApplicationUtils.silentlyDestroyApplication(applicationName, application.getCartridge(), user, service);
		}
	}

	@Test
	public void loadWithOtherUserReportsIdenticalResults() throws Exception {
		MySqlEmbeddableCartridge mysql = new MySqlEmbeddableCartridge(service, user);
		IEmbeddableCartridge cartridge = service.addEmbeddedCartridge(application.getName(), mysql, user);
		assertEquals(mysql.getName(), cartridge.getName());
		
		PHPMyAdminEmbeddableCartridge myadmin = new PHPMyAdminEmbeddableCartridge(service, user);
		cartridge = service.addEmbeddedCartridge(application.getName(), myadmin, user);
		assertEquals(myadmin.getName(), cartridge.getName());

		User newUser = new TestUser(service);
		IApplication reloadedApplication = newUser.getApplicationByName(application.getName());
		assertNotNull(reloadedApplication);
		List<IEmbeddableCartridge> embeddedCartridges = reloadedApplication.getEmbeddedCartridges();
		assertNotNull(embeddedCartridges);
		assertEquals(2, embeddedCartridges.size());
		assertThatContainsCartridge(mysql.getName(), embeddedCartridges);
		assertThatContainsCartridge(myadmin.getName(), embeddedCartridges);
	}
	
	@Test
	public void canRemoveEmbeddedCartridge() throws Exception {
		MySqlEmbeddableCartridge mysql = new MySqlEmbeddableCartridge(service, user);
		IEmbeddableCartridge cartridge = service.addEmbeddedCartridge(application.getName(), mysql, user);
		assertEquals(mysql.getName(), cartridge.getName());
		
		service.removeEmbeddedCartridge(application.getName(), mysql, user);
	}

	@Test(expected=OpenShiftException.class)
	public void cannotRemoveEmbeddedCartridgeThatWasNotAdded() throws Exception {
		MySqlEmbeddableCartridge mysql = new MySqlEmbeddableCartridge(service, user);
		service.removeEmbeddedCartridge(application.getName(), mysql, user);
	}
}
