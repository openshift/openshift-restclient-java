/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client;

import static com.openshift.client.utils.Samples.GET_DOMAINS;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_3EMBEDDED;
import static com.openshift.client.utils.UrlEndsWithMatcher.urlEndsWith;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IGearGroup;
import com.openshift.client.IHttpClient;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.IDeployedStandaloneCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.cartridge.StandaloneCartridge;
import com.openshift.client.utils.CartridgeTestUtils;
import com.openshift.client.utils.Samples;
import com.openshift.client.utils.TestConnectionBuilder;
import com.openshift.internal.client.httpclient.request.JsonMediaType;
import com.openshift.internal.client.httpclient.request.Parameter;
import com.openshift.internal.client.response.CartridgeResourceDTO;
import com.openshift.internal.client.utils.IOpenShiftJsonConstants;

/**
 * @author Jeff Cantrill
 * @author Andre Dietisheim
 *
 */
public class StandaloneCartridgeResourceTest {

	private ApplicationResource application = mock(ApplicationResource.class);
	private CartridgeResourceDTO dto = mock(CartridgeResourceDTO.class);
	private IStandaloneCartridge as7Cartridge = new StandaloneCartridge(CartridgeTestUtils.JBOSSAS_7_NAME);
	private IStandaloneCartridge phpCartridge = new StandaloneCartridge(CartridgeTestUtils.PHP_53_NAME);
	private IDeployedStandaloneCartridge deployedPhpCartridge;

	private IUser user;
	private IDomain domain;
	private IApplication springeap6Application;
	private IHttpClient client;
	private HttpClientMockDirector mockDirector;

	@Before
	public void setup() throws Throwable {
		when(dto.getName()).thenReturn(CartridgeTestUtils.PHP_53_NAME);
		when(dto.getType()).thenReturn(CartridgeType.STANDALONE);
		this.deployedPhpCartridge = new StandaloneCartridgeResource(dto, application);
		this.mockDirector = new HttpClientMockDirector();
		this.client = mockDirector
				.mockGetDomains(GET_DOMAINS)
				.mockGetApplications(
						"foobarz", Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_1EMBEDDED)
				.mockGetApplication(
						"foobarz", "springeap6", Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_1EMBEDDED)
				.mockGetGearGroups(
						"foobarz", "springeap6", Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_GEARGROUPS)
				// ATTENTION: nothing changed in the mocked response (works
				// since application is not caching geargroups)
				.mockSetGearGroups(
						"foobarz", "springeap6", Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_GEARGROUPS)
				.client();
		this.user = new TestConnectionBuilder().defaultCredentials().create(client).getUser();
		this.domain = user.getDomain("foobarz");
		this.springeap6Application = domain.getApplicationByName("springeap6");
	}

	@Test
	public void testUpdateAdditionalGearStorage() throws Exception {
		HttpClientMockDirector builder = new HttpClientMockDirector();
		IHttpClient httpClient = builder
				.mockGetDomains(GET_DOMAINS)
				.mockGetApplications(
						"foobarz", GET_DOMAINS_FOOBARZ_APPLICATIONS_3EMBEDDED)
				.client();
		IUser user = new TestConnectionBuilder().defaultCredentials().create(httpClient).getUser();
		IDomain domain = user.getDomain("foobarz");
		IApplication application = domain.getApplicationByName("springeap6");

		IDeployedStandaloneCartridge cartridge = application.getCartridge();

		cartridge.setAdditionalGearStorage(40);

		verify(httpClient, times(1)).put(
				urlEndsWith("applications/springeap6/cartridges/jbosseap-6"),
				any(JsonMediaType.class),
				anyInt(),
				eq(new Parameter(IOpenShiftJsonConstants.PROPERTY_ADDITIONAL_GEAR_STORAGE, "40")));
	}

	@Test
	public void standaloneCartridgeShouldEqualStandaloneCartridgeResource() {
		// pre-conditions
		// operation
		// verification
		assertThat(phpCartridge).isEqualTo(deployedPhpCartridge);
		assertThat(as7Cartridge).isNotEqualTo(deployedPhpCartridge);
	}

	@Test
	public void standaloneCartridgeResourceAndStandAloneCartridgeShouldHaveSameHashCode() {
		// pre-conditions

		// operation
		// verification
		assertThat(deployedPhpCartridge.hashCode()).isEqualTo(phpCartridge.hashCode());
	}

	@Test
	public void shouldReportGearGroup() throws OpenShiftException, URISyntaxException {
		// precondition
		IDeployedStandaloneCartridge cartridge = springeap6Application.getCartridge();
		assertThat(cartridge).isNotNull();

		// operation
		IGearGroup gearGroup = cartridge.getGearGroup();

		// verification
		assertThat(gearGroup).isNotNull();
		assertThat(gearGroup.getCartridges()).contains(cartridge);
	}

	@Test
	public void shouldGetGearStorage() throws OpenShiftException, IOException {
		// precondition
		IDeployedStandaloneCartridge cartridge = springeap6Application.getCartridge();
		assertThat(cartridge).isNotNull();

		// operation
		int additionalGearStorage = cartridge.getAdditionalGearStorage();

		// verification
		// reload user info to ensure the storage info isnt cached
		assertThat(additionalGearStorage).isNotEqualTo(IGearGroup.NO_ADDITIONAL_GEAR_STORAGE);
	}

	@Test
	public void shouldSetGearStorage() throws OpenShiftException, IOException {
		// precondition
		IDeployedStandaloneCartridge cartridge = springeap6Application.getCartridge();
		assertThat(cartridge).isNotNull();
		int newAdditionalGearStorage = 12;

		// operation
		cartridge.setAdditionalGearStorage(newAdditionalGearStorage);

		// verification
		// reload user info to ensure the storage info isnt cached
		mockDirector.mockGetGearGroups("foobarz", "springeap6", Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_GEARGROUPS_12ADDITIONALGEARSTORAGE);
		assertThat(cartridge.getAdditionalGearStorage()).isEqualTo(newAdditionalGearStorage);
	}
}
