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
import com.openshift.express.client.IOpenShiftService;
import com.openshift.express.client.OpenShiftException;
import com.openshift.express.client.OpenShiftService;
import com.openshift.express.client.User;
import com.openshift.express.client.configuration.DefaultConfiguration;
import com.openshift.express.client.configuration.SystemConfiguration;
import com.openshift.express.client.configuration.UserConfiguration;
import com.openshift.express.internal.client.test.fakes.TestUser;
import com.openshift.express.internal.client.test.utils.ApplicationUtils;

/**
 * @author André Dietisheim
 */
public class EmbedIntegrationTest {

	private static final int WAIT_FOR_APPLICATION = 10 * 1024;
	private IOpenShiftService service;
	private User user;
	private IApplication application;

	@Before
	public void setUp() throws OpenShiftException, IOException {
		UserConfiguration userConfiguration = new UserConfiguration(new SystemConfiguration(new DefaultConfiguration()));
		this.service = new OpenShiftService(TestUser.ID, userConfiguration.getLibraServer());
		service.setEnableSSLCertChecks(Boolean.parseBoolean(System.getProperty("enableSSLCertChecks")));
		this.user = new TestUser(service);
		this.application = service.createApplication(ApplicationUtils.createRandomApplicationName(),
				ICartridge.JBOSSAS_7, user);
	}

	@After
	public void tearDown() {
		ApplicationUtils.silentlyDestroyAS7Application(application.getName(), user, service);
	}

	@Test
	public void canEmbedMySQL() throws Exception {
		service.addEmbeddedCartridge(application.getName(), IEmbeddableCartridge.MYSQL_51, user);
	}
	
	@Test
	public void embeddedMySQLShowsUpInEmbeddedCartridges() throws Exception {
		application.addEmbbedCartridge(IEmbeddableCartridge.MYSQL_51);
		assertThatContainsCartridge(IEmbeddableCartridge.MYSQL_51.getName(), application.getEmbeddedCartridges());
	}

	@Test
	public void canEmbedPhpMyAdmin() throws Exception {
		service.addEmbeddedCartridge(application.getName(), IEmbeddableCartridge.MYSQL_51, user);
		service.addEmbeddedCartridge(application.getName(), IEmbeddableCartridge.PHPMYADMIN_34, user);
	}

	@Test
	public void canEmbedJenkins() throws Exception {
		ApplicationUtils.silentlyDestroyAnyJenkinsApplication(user);
		String jenkinsAppName = "jenkins";
		try {
			IApplication jenkins = service.createApplication(jenkinsAppName, ICartridge.JENKINS_14, user);
			assertTrue(service.waitForApplication(jenkins.getHealthCheckUrl(), WAIT_FOR_APPLICATION));
			service.addEmbeddedCartridge(application.getName(), IEmbeddableCartridge.JENKINS_14, user);
		} finally {
			ApplicationUtils.silentlyDestroyJenkinsApplication(jenkinsAppName, user, service);
		}
	}
	
	@Test
	public void embeddedCartridgeHasUrl() throws OpenShiftException {
		String applicationName = ApplicationUtils.createRandomApplicationName();
		try {
			IApplication application = user.createApplication(applicationName, ICartridge.JBOSSAS_7);
			application.addEmbbedCartridge(IEmbeddableCartridge.MYSQL_51);
			IEmbeddableCartridge embeddedCartridge = application.getEmbeddedCartridge(IEmbeddableCartridge.MYSQL_51.getName());
			assertNotNull(embeddedCartridge);
			assertNotNull(embeddedCartridge.getUrl());
		} finally {
			ApplicationUtils.silentlyDestroyAS7Application(applicationName, user, service);
		}
	}

	@Test
	public void loadWithOtherUserReportsIdenticalResults() throws Exception {
		service.addEmbeddedCartridge(application.getName(), IEmbeddableCartridge.MYSQL_51, user);
		service.addEmbeddedCartridge(application.getName(), IEmbeddableCartridge.PHPMYADMIN_34, user);

		User newUser = new TestUser(service);
		IApplication reloadedApplication = newUser.getApplicationByName(application.getName());
		assertNotNull(reloadedApplication);
		List<IEmbeddableCartridge> embeddedCartridges = reloadedApplication.getEmbeddedCartridges();
		assertNotNull(embeddedCartridges);
		assertEquals(2, embeddedCartridges.size());
		assertThatContainsCartridge(IEmbeddableCartridge.MYSQL_51.getName(), embeddedCartridges);
		assertThatContainsCartridge(IEmbeddableCartridge.PHPMYADMIN_34.getName(), embeddedCartridges);
	}
	
	@Test
	public void canRemoveEmbeddedCartridge() throws Exception {
		service.addEmbeddedCartridge(application.getName(), IEmbeddableCartridge.MYSQL_51, user);
		service.removeEmbeddedCartridge(application.getName(), IEmbeddableCartridge.MYSQL_51, user);
	}

	@Test(expected=OpenShiftException.class)
	public void cannotRemoveEmbeddedCartridgeThatWasNotAdded() throws Exception {
		service.removeEmbeddedCartridge(application.getName(), IEmbeddableCartridge.MYSQL_51, user);
	}
}
