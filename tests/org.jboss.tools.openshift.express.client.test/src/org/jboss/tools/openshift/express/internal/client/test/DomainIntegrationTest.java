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
package org.jboss.tools.openshift.express.internal.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jboss.tools.openshift.express.client.IDomain;
import org.jboss.tools.openshift.express.client.OpenShiftService;
import org.jboss.tools.openshift.express.client.SSHKeyPair;
import org.jboss.tools.openshift.express.internal.client.test.fakes.TestSSHKey;
import org.jboss.tools.openshift.express.internal.client.test.fakes.TestUser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class DomainIntegrationTest {

	private OpenShiftService openShiftService;
	private TestUser user;

	@Before
	public void setUp() {
		this.openShiftService = new OpenShiftService(TestUser.ID);
		this.user = new TestUser();
	}

	@Ignore
	@Test
	public void canCreateDomain() throws Exception {

		String domainName = createRandomString();
		SSHKeyPair sshKey = TestSSHKey.create();
		IDomain domain = openShiftService.createDomain(domainName, sshKey, user);

		assertNotNull(domain);
		assertEquals(domainName, domain.getNamespace());
	}

	@Ignore
	@Test
	public void canChangeDomain() throws Exception {

		String domainName = createRandomString();
		SSHKeyPair sshKey = TestSSHKey.create();
		IDomain domain = openShiftService.changeDomain(domainName, sshKey, user);

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
}
