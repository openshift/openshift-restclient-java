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
import org.junit.Ignore;
import org.junit.Test;

import com.openshift.client.ApplicationScale;
import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IGearProfile;
import com.openshift.client.IUser;
import com.openshift.client.Message;
import com.openshift.client.Message.Severity;
import com.openshift.client.OpenShiftEndpointException;
import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.cartridge.selector.LatestVersionOf;
import com.openshift.client.utils.ApplicationAssert;
import com.openshift.client.utils.ApplicationTestUtils;
import com.openshift.client.utils.DomainTestUtils;
import com.openshift.client.utils.MessageAssert;
import com.openshift.client.utils.StringUtils;
import com.openshift.client.utils.TestConnectionFactory;

/**
 * @author Andre Dietisheim
 */
public class DomainResourceIntegrationTest {

	private static final String QUICKSTART_REVEALJS_GITURL = "git://github.com/openshift-quickstart/reveal.js-openshift-quickstart.git";
	private static final String REVEALJS_INDEX = "revealjs.html";
	
	private IUser user;
	private IDomain domain;

	@Before
	public void setUp() throws OpenShiftException, IOException {
		this.user = new TestConnectionFactory().getConnection().getUser();
		this.domain = DomainTestUtils.ensureHasDomain(user);
	}
	
	@Test
	public void shouldSetNamespace() throws Exception {
		// pre-condition
		IDomain domain = DomainTestUtils.ensureHasDomain(user);
		String namespace = DomainTestUtils.createRandomName();
		// cannot set namespace if there are applications
		ApplicationTestUtils.destroyAllApplications(domain);
		
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
		ApplicationTestUtils.destroyAllApplications(domain);
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
	public void shouldContainErrorMessageAndContainErrorCode128() throws OpenShiftException {
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
			assertThat(e.getRestResponseMessages().size()).isEqualTo(1);
			new MessageAssert(e.getRestResponseMessage(Message.FIELD_DEFAULT))
					.hasExitCode(128)
					.hasSeverity(Severity.ERROR)
					.hasText();
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

	@Test
	public void shouldGetApplicationByNameCaseInsensitive() throws OpenShiftException, FileNotFoundException, IOException {
		// pre-condition
		IDomain domain = DomainTestUtils.ensureHasDomain(user);
		IApplication application = ApplicationTestUtils.ensureHasExactly1Application(LatestVersionOf.jbossAs(), domain);
		assertThat(application).isNotNull();
		String name = application.getName();
		assertThat(name).isNotNull();
		
		// operation
		IApplication exactNameQueryResult = domain.getApplicationByName(name);
		IApplication upperCaseNameQueryResult = domain.getApplicationByName(name.toUpperCase());
		
		// verification
		assertThat(exactNameQueryResult).isNotNull();
		assertThat(exactNameQueryResult.getName()).isEqualTo(name);
		assertThat(upperCaseNameQueryResult).isNotNull();
		assertThat(upperCaseNameQueryResult.getName()).isEqualTo(name);
	}
	
	@Test
	public void shouldCreateNonScalableApplication() throws Exception {
		// pre-conditions
		ApplicationTestUtils.destroyIfMoreThan(2, domain);

		// operation
		String applicationName =
				ApplicationTestUtils.createRandomApplicationName();
		IStandaloneCartridge jbossas = LatestVersionOf.jbossAs().get(user);
		IApplication application =
				domain.createApplication(applicationName, jbossas);

		// verification
		assertThat(new ApplicationAssert(application))
				.hasName(applicationName)
				.hasUUID()
				.hasCreationTime()
				.hasCartridge(jbossas)
				.hasValidApplicationUrl()
				.hasValidGitUrl()
				.hasNoInitialGitUrl()
				.hasEmbeddableCartridges()
				.hasAlias();
	}
	
	@Test
	public void shouldCreateNonScalableApplicationWithSmallGear() throws Exception {
		// pre-conditions
		ApplicationTestUtils.destroyIfMoreThan(2, domain);

		// operation
		String applicationName =
				ApplicationTestUtils.createRandomApplicationName();
		IStandaloneCartridge jbossas = LatestVersionOf.jbossAs().get(user);
		IApplication application = domain.createApplication(applicationName, jbossas, IGearProfile.SMALL);

		// verification
		assertThat(new ApplicationAssert(application))
				.hasName(applicationName)
				.hasUUID()
				.hasCreationTime()
				.hasCartridge(jbossas)
				.hasValidApplicationUrl()
				.hasValidGitUrl()
				.hasNoInitialGitUrl()
				.hasEmbeddableCartridges()
				.hasAlias();
	}

	@Test
	public void shouldCreateNonScalableApplicationWithSmallGearAndGitUrl() throws Exception {
		// pre-conditions
		
		ApplicationTestUtils.destroyIfMoreThan(2, domain);

		// operation
		String applicationName =
				ApplicationTestUtils.createRandomApplicationName();
		IStandaloneCartridge php = LatestVersionOf.php().get(user);
		IApplication application = domain.createApplication(
				applicationName, php, ApplicationScale.NO_SCALE, GearProfile.SMALL, QUICKSTART_REVEALJS_GITURL);

		// verification
		new ApplicationAssert(application)
				.hasName(applicationName)
				.hasUUID()
				.hasCreationTime()
				.hasCartridge(php)
				.hasValidApplicationUrl()
				.hasInitialGitUrl(QUICKSTART_REVEALJS_GITURL)
				.hasEmbeddableCartridges()
				.hasAlias()
				.hasContent(REVEALJS_INDEX, "Reveal.js");
			}
	
	@Test
	public void shouldCreateScalableApplication() throws Exception {
		// pre-conditions
		ApplicationTestUtils.destroyAllApplications(domain);

		// operation
		String applicationName =
				ApplicationTestUtils.createRandomApplicationName();
		IStandaloneCartridge jbossas = LatestVersionOf.jbossAs().get(user);
		IApplication application = domain.createApplication(
				applicationName, jbossas, ApplicationScale.SCALE, GearProfile.SMALL);

		// verification
		assertThat(new ApplicationAssert(application))
				.hasName(applicationName)
				.hasUUID()
				.hasCreationTime()
				.hasCartridge(jbossas)
				.hasValidApplicationUrl()
				.hasValidGitUrl()
				.hasNoInitialGitUrl()
				// scalable apps always have ha-proxy embedded automatically
				.hasEmbeddedCartridge(LatestVersionOf.haProxy())
				.hasAlias();
	}

	@Test
	public void shouldCreateJenkinsApplication() throws Exception {
		// pre-conditions
		ApplicationTestUtils.destroyAllApplications(domain);

		// operation
		String applicationName =
				ApplicationTestUtils.createRandomApplicationName();
		IStandaloneCartridge jenkins = LatestVersionOf.jenkins().get(user);
		IApplication application = domain.createApplication(
				applicationName, jenkins );

		// verification
		assertThat(new ApplicationAssert(application))
				.hasName(applicationName)
				.hasUUID()
				.hasCreationTime()
				.hasCartridge(jenkins)
				.hasValidApplicationUrl()
				.hasValidGitUrl()
				.hasNoInitialGitUrl()
				.hasEmbeddableCartridges()
				.hasAlias();
	}
	
	@Test(expected = OpenShiftException.class)
	public void createDuplicateApplicationThrowsException() throws Exception {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);

		// operation
		domain.createApplication(application.getName(), LatestVersionOf.jbossAs().get(user));
	}

	@Test(expected = OpenShiftException.class)
	public void createApplicationWithSameButUppercaseNameThrowsException() throws Exception {
		// pre-condition
		IApplication application = ApplicationTestUtils.getOrCreateApplication(domain);
		String name = application.getName();
		
		// operation
		domain.createApplication(name.toUpperCase(), LatestVersionOf.jbossAs().get(user));
	}

}
