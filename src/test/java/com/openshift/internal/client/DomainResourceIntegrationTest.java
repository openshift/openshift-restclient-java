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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.openshift.client.ICartridge;
import com.openshift.client.IDomain;
import com.openshift.client.IUser;
import com.openshift.client.InvalidCredentialsOpenShiftException;
import com.openshift.client.OpenShiftEndpointException;
import com.openshift.client.OpenShiftException;
import com.openshift.client.utils.ApplicationTestUtils;
import com.openshift.client.utils.DomainTestUtils;
import com.openshift.client.utils.OpenShiftTestConfiguration;
import com.openshift.client.utils.StringUtils;
import com.openshift.client.utils.TestConnectionFactory;

/**
 * @author Andre Dietisheim
 */
public class DomainResourceIntegrationTest {

	private IUser user;

	@Before
	public void setUp() throws OpenShiftException, IOException {
		this.user = new TestConnectionFactory().getConnection().getUser();
	}

	@Test(expected = InvalidCredentialsOpenShiftException.class)
	public void shouldThrowInvalidCredentialsWhenConnectingWithInvalidCredentials() throws Exception {
		new TestConnectionFactory().getConnection(
				new OpenShiftTestConfiguration().getClientId(), "bogus-password").getUser();
	}
	
	@Test
	public void shouldReturnDomains() throws OpenShiftException {
		// operation
		List<IDomain> domains = user.getDomains();
		assertThat(domains).isNotNull();
	}

	@Test
	public void shouldCreateDomain() throws OpenShiftException {
		// pre-condition
		DomainTestUtils.silentlyDestroyAllDomains(user);

		// operation
		String id = StringUtils.createRandomString();
		IDomain domain = user.createDomain(id);

		// verification
		assertThat(domain.getId()).isEqualTo(id);
	}

	@Test
	public void shouldReturnDomainByName() throws OpenShiftException {
		// pre-condition
		DomainTestUtils.silentlyDestroyAllDomains(user);

		// operation
		String id = StringUtils.createRandomString();
		user.createDomain(id);

		// verification
		IDomain domainByNamespace = user.getDomain(id);
		assertThat(domainByNamespace.getId()).isEqualTo(id);
	}

	@Test
	public void shouldSetNamespace() throws Exception {
		// pre-condition
		IDomain domain = DomainTestUtils.getFirstDomainOrCreate(user);

		// operation
		String namespace = StringUtils.createRandomString();
		domain.rename(namespace);

		// verification
		IDomain domainByNamespace = user.getDomain(namespace);
		assertThat(domainByNamespace.getId()).isEqualTo(namespace);
	}

	@Test
	public void canWaitForDomainToBecomeAccessible() throws OpenShiftException {
		// IDomain domain = user.getDomain();
		// assertNotNull(domain);
		// String newDomainName = createRandomString();
		// domain.setNamespace(newDomainName);
		// assertEquals(newDomainName, domain.getNamespace());
		// assertTrue(domain.waitForAccessible(10 * 1024));
	}

	@Test
	public void shouldDeleteDomainWithoutApplications() throws Exception {
		// pre-condition
		DomainTestUtils.silentlyDestroyAllDomains(user);
		String id = StringUtils.createRandomString();
		IDomain domain = user.createDomain(id);

		// operation
		domain.destroy();

		// verification
		IDomain domainByNamespace = user.getDomain(id);
		assertThat(domainByNamespace).isNull();
	}

	@Test
	public void shouldNotDeleteDomainWithApplications() throws OpenShiftException, SocketTimeoutException {
		IDomain domain = null;
		try {
			// pre-condition
			domain = DomainTestUtils.getFirstDomainOrCreate(user);
			ApplicationTestUtils.getOrCreateApplication(domain);

			// operation
			domain.destroy();
			fail("OpenShiftEndpointException did not occurr");
		} catch (OpenShiftEndpointException e) {
			// verification
			assertThat(e.getRestResponse().getMessages().get(0).getExitCode()).isEqualTo(128);
		}
	}

	@Test
	public void shouldDeleteDomainWithApplications() throws OpenShiftException, SocketTimeoutException {
		// pre-condition
		IDomain domain = DomainTestUtils.getFirstDomainOrCreate(user);
		ApplicationTestUtils.getOrCreateApplication(domain);

		// operation
		domain.destroy(true);

		// verification
		assertThat(domain).isNotIn(user.getDomains());
		domain = null;
	}

	@Test
	public void shouldSeeNewApplicationAfterRefresh() throws OpenShiftException, FileNotFoundException, IOException {
		// pre-condition
		IDomain domain = DomainTestUtils.getFirstDomainOrCreate(user);
		int numOfApplications = domain.getApplications().size();

		IUser otherUser = new TestConnectionFactory().getConnection().getUser();
		IDomain otherDomain = otherUser.getDomain(domain.getId());
		assertNotNull(otherDomain);

		// operation
		String applicationName = StringUtils.createRandomString();
		otherDomain.createApplication(applicationName, ICartridge.PHP_53);
		assertThat(domain.getApplications().size()).isEqualTo(numOfApplications);
		domain.refresh();

		// verification
		assertThat(domain.getApplications().size()).isEqualTo(numOfApplications + 1);
	}
}
