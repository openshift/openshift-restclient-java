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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;

import org.junit.Before;
import org.junit.Test;

import com.openshift.client.IDomain;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftEndpointException;
import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.selector.LatestVersionOf;
import com.openshift.client.utils.ApplicationTestUtils;
import com.openshift.client.utils.DomainTestUtils;
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
	
	@Test
	public void shouldSetNamespace() throws Exception {
		// pre-condition
		IDomain domain = DomainTestUtils.ensureHasDomain(user);
		String namespace = DomainTestUtils.createRandomName();

		// operation
		domain.rename(namespace);

		// verification
		IDomain domainByNamespace = user.getDomain(namespace);
		assertThat(domainByNamespace.getId()).isEqualTo(namespace);
	}

	@Test
	public void shouldDeleteDomainWithoutApplications() throws Exception {
		// pre-condition
		IDomain domain = DomainTestUtils.ensureHasDomain(user);
		String id = domain.getId();
		ApplicationTestUtils.silentlyDestroyAllApplications(domain);
		assertThat(domain.getApplications()).isEmpty();
		
		// operation
		domain.destroy();

		// verification
		IDomain domainByNamespace = user.getDomain(id);
		assertThat(domainByNamespace).isNull();
	}

	@Test
	public void shouldNotDeleteDomainWithApplications() throws OpenShiftException {
		IDomain domain = null;
		try {
			// pre-condition
			domain = DomainTestUtils.ensureHasDomain(user);
			ApplicationTestUtils.getOrCreateApplication(domain);
			assertThat(domain.getApplications()).isNotEmpty();
			
			// operation
			domain.destroy();
			// verification
			fail("OpenShiftEndpointException did not occurr");
		} catch (OpenShiftEndpointException e) {
			// verification
		}
	}

	@Test
	public void shouldReportErrorCode128() throws OpenShiftException {
		IDomain domain = null;
		try {
			// pre-condition
			domain = DomainTestUtils.ensureHasDomain(user);
			ApplicationTestUtils.getOrCreateApplication(domain);
			assertThat(domain.getApplications()).isNotEmpty();
			
			// operation
			domain.destroy();
			fail("OpenShiftEndpointException did not occurr");
		} catch (OpenShiftEndpointException e) {
			// verification
			assertThat(e.getRestResponse()).isNotNull();
			assertThat(e.getRestResponse().getMessages()).isNotEmpty();
			assertThat(e.getRestResponse().getMessages().get(0)).isNotNull();
			assertThat(e.getRestResponse().getMessages().get(0).getExitCode()).isEqualTo(128);
		}
	}

	@Test
	public void shouldDeleteDomainWithApplications() throws OpenShiftException, SocketTimeoutException {
		// pre-condition
		IDomain domain = DomainTestUtils.ensureHasDomain(user);
		ApplicationTestUtils.getOrCreateApplication(domain);
		assertThat(domain.getApplications()).isNotEmpty();
		
		// operation
		domain.destroy(true);

		// verification
		assertThat(domain).isNotIn(user.getDomains());
		domain = null;
	}

	@Test
	public void shouldSeeNewApplicationAfterRefresh() throws OpenShiftException, FileNotFoundException, IOException {
		// pre-condition
		IDomain domain = DomainTestUtils.ensureHasDomain(user);
		int numOfApplications = domain.getApplications().size();

		IUser otherUser = new TestConnectionFactory().getConnection().getUser();
		IDomain otherDomain = otherUser.getDomain(domain.getId());
		assertNotNull(otherDomain);

		// operation
		String applicationName = StringUtils.createRandomString();
		otherDomain.createApplication(applicationName, LatestVersionOf.php().get(otherUser));
		assertThat(domain.getApplications().size()).isEqualTo(numOfApplications);
		domain.refresh();

		// verification
		assertThat(domain.getApplications().size()).isEqualTo(numOfApplications + 1);
	}
}
