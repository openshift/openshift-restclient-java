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

import static com.openshift.client.utils.Samples.GET_DOMAINS;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_GEARGROUPS;
import static com.openshift.client.utils.UrlEndsWithMatcher.urlEndsWith;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.openshift.client.GearState;
import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IGearGroup;
import com.openshift.client.IHttpClient;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftConnectionFactory;
import com.openshift.client.utils.GearGroupsAssert;
import com.openshift.client.utils.Samples;

/**
 * @author Xavier Coulon
 * @author Andre Dietisheim
 */
public class GearGroupsResourceTest {

	private IDomain domain;
	private IHttpClient mockClient;

	@Before
	public void setup() throws Throwable {
		mockClient = mock(IHttpClient.class);
		when(mockClient.get(urlEndsWith("/broker/rest/api")))
				.thenReturn(Samples.GET_API.getContentAsString());
		when(mockClient.get(urlEndsWith("/user"))).thenReturn(
				Samples.GET_USER_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains"))).thenReturn(GET_DOMAINS.getContentAsString());
		final IOpenShiftConnection connection =
				new OpenShiftConnectionFactory().getConnection(
						new RestService("http://mock", "clientId", mockClient), "foo@redhat.com", "bar");
		IUser user = connection.getUser();
		this.domain = user.getDomain("foobarz");
	}

	@Test
	public void shouldGetGearGroups() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications/springeap6")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications/springeap6/gear_groups")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_GEARGROUPS.getContentAsString());
		final IApplication app = domain.getApplicationByName("springeap6");
		// operation
		final Collection<IGearGroup> gearGroups = app.getGearGroups();
		// verifications
		assertThat(new GearGroupsAssert(gearGroups)).hasSize(3);
		assertThat(new GearGroupsAssert(gearGroups)).assertGroup("514207b84382ec1fef0000ab")
				.hasUUID("514207b84382ec1fef0000ab")
				.assertGear("514207b84382ec1fef000098").inState(GearState.IDLE)
				.assertGear("5146f047500446f12d00002e").inState(GearState.BUILDING);
		assertThat(new GearGroupsAssert(gearGroups)).assertGroup("514208014382ec1fef0000c8")
				.hasUUID("514208014382ec1fef0000c8")
				.assertGear("514208014382ec1fef0000bc").inState(GearState.STARTED);
		assertThat(new GearGroupsAssert(gearGroups)).assertGroup("514212ce500446b64e0000c0")
				.hasUUID("514212ce500446b64e0000c0")
				.assertGear("514212ce500446b64e0000b4").inState(GearState.DEPLOYING);
	}
}
