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

import org.junit.Before;
import org.junit.Test;

import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IHttpClient;
import com.openshift.client.IUser;
import com.openshift.client.cartridge.IDeployedStandaloneCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.cartridge.StandaloneCartridge;
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

	private ApplicationResource app = mock(ApplicationResource.class);
	private CartridgeResourceDTO dto = mock(CartridgeResourceDTO.class);
	private IStandaloneCartridge cartridge = mock(IStandaloneCartridge.class);
	private StandaloneCartridgeResource cartridgeResource;

	@Before
	public void setup() {
		when(dto.getName()).thenReturn("cartridgeName");
		when(dto.getType()).thenReturn(CartridgeType.STANDALONE);
		when(cartridge.getName()).thenReturn("cartridgeName");
		this.cartridgeResource = new StandaloneCartridgeResource(dto, app);
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
	public void standaloneCartridgeResourceShouldEqualStandAloneCartridge() {
		// pre-conditions
		// operation
		// verification
		assertThat(cartridgeResource).isEqualTo(cartridge);

		when(cartridge.getName()).thenReturn("other");
		assertThat(cartridgeResource).isNotEqualTo(cartridge);
	}

	@Test
	public void standaloneCartridgeResourceAndStandAloneCartridgeShouldHaveSameHashCode() {
		// pre-conditions

		// operation
		// verification
		assertThat(cartridgeResource.hashCode()).isEqualTo(new StandaloneCartridge("cartridgeName").hashCode());
	}
}
