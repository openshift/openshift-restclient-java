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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.openshift.express.client.IApplication;
import com.openshift.express.client.IDomain;
import com.openshift.express.client.IUser;
import com.openshift.express.client.NotFoundOpenShiftException;
import com.openshift.express.client.OpenShiftException;
import com.openshift.express.client.OpenShiftService;
import com.openshift.express.client.SSHKeyPair;
import com.openshift.express.client.configuration.DefaultConfiguration;
import com.openshift.express.client.configuration.OpenShiftConfiguration;
import com.openshift.express.client.configuration.SystemConfiguration;
import com.openshift.express.client.configuration.UserConfiguration;
import com.openshift.express.internal.client.test.fakes.TestSSHKey;
import com.openshift.express.internal.client.test.fakes.TestUser;

public class DomainIntegrationTest {

	private OpenShiftService service;
	private TestUser user;

	@Before
	public void setUp() throws OpenShiftException, IOException {
		service = new OpenShiftService(TestUser.ID, new OpenShiftConfiguration().getLibraServer());
		service.setEnableSSLCertChecks(Boolean.parseBoolean(System.getProperty("enableSSLCertChecks")));
		
		user = new TestUser(service);
		ensureDomainExists(user);
		ensureNoApplicationsExist(user);
	}

	@Test
	public void canChangeDomain() throws Exception {
		String domainName = createRandomString();
		SSHKeyPair sshKey = TestSSHKey.create();
		IDomain domain = service.changeDomain(domainName, sshKey, user);

		assertNotNull(domain);
		assertEquals(domainName, domain.getNamespace());
	}

	@Test
	public void canSetNamespaceOnDomain() throws Exception {
		IDomain domain = user.getDomain();
		assertNotNull(domain);
		String newDomainName = createRandomString();
		domain.setNamespace(newDomainName);
		assertEquals(newDomainName, domain.getNamespace());
	}

	private String createRandomString() {
		return String.valueOf(System.currentTimeMillis());
	}

	@Test
	public void canWaitForDomainToBecomeAccessible() throws OpenShiftException {
		IDomain domain = user.getDomain();
		assertNotNull(domain);
		String newDomainName = createRandomString();
		domain.setNamespace(newDomainName);
		assertEquals(newDomainName, domain.getNamespace());
		assertTrue(domain.waitForAccessible(10 * 1024));
	}

	@Test
	public void canDeleteDomain() throws Exception {
		IDomain domain = user.getDomain();
		assertNotNull(domain);

		domain.destroy();
		assertNull(user.getDomain());
	}

	@Test
	public void canCreateDomain() throws Exception {
		IDomain domain = user.getDomain();
		assertNotNull(domain);
		domain.destroy();

		String domainName = createRandomString();
		SSHKeyPair sshKey = TestSSHKey.create();
		domain = service.createDomain(domainName, sshKey, user);

		assertNotNull(domain);
		assertEquals(domainName, domain.getNamespace());
	}

	@Test(expected = OpenShiftException.class)
	public void cannotDeleteDomainIfApplicationsPresent() throws OpenShiftException {
		user.createTestApplication();
		IDomain domain = user.getDomain();
		assertNotNull(domain);
		domain.destroy();
		assertNotNull(domain);
	}

	private void ensureNoApplicationsExist(TestUser user) throws OpenShiftException {
		try {
			List<IApplication> allApplications = new ArrayList<IApplication>();
			allApplications.addAll(user.getApplications());
			for (IApplication application : allApplications) {
				application.destroy();
			}
		} catch (NotFoundOpenShiftException e) {
			// no domain present, ignore
		}
	}

	private void ensureDomainExists(IUser user) throws OpenShiftException, IOException {
		try {
			user.getDomain();
		} catch (OpenShiftException e) {
			// no domain present
			SSHKeyPair sshKey = TestSSHKey.create();
			service.createDomain(createRandomString(), sshKey, user);
		}
	}

}
