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
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_1EMBEDDED;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_1EMBEDDED;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_GEARGROUPS;
import static org.fest.assertions.Assertions.assertThat;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.openshift.client.GearState;
import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IGearGroup;
import com.openshift.client.IHttpClient;
import com.openshift.client.IUser;
import com.openshift.client.utils.GearGroupsAssert;
import com.openshift.client.utils.TestConnectionFactory;

/**
 * @author Xavier Coulon
 * @author Andre Dietisheim
 */
public class GearGroupsResourceTest {

	private IDomain domain;

	@Before
	public void setup() throws Throwable {
		IHttpClient client = new HttpClientMockDirector()
			.mockGetDomains(GET_DOMAINS)
			.mockGetApplications("foobarz", GET_DOMAINS_FOOBARZ_APPLICATIONS_1EMBEDDED)
			.mockGetApplication("foobarz", "springeap6", GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_1EMBEDDED)
			.mockGetGearGroups("foobarz", "springeap6", GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_GEARGROUPS)
			.client();
		IUser user = new TestConnectionFactory().getConnection(client).getUser();
		this.domain = user.getDomain("foobarz");
	}

	@Test
	public void shouldGetGearGroups() throws Throwable {
		// pre-conditions
		final IApplication app = domain.getApplicationByName("springeap6");
		// operation
		final Collection<IGearGroup> gearGroups = app.getGearGroups();
		// verifications
		assertThat(new GearGroupsAssert(gearGroups)).hasSize(2);
		assertThat(new GearGroupsAssert(gearGroups)).assertGroup("514207b84382ec1fef0000ab")
				.hasUUID("514207b84382ec1fef0000ab")
				.assertGear("514207b84382ec1fef000098").inState(GearState.IDLE)
				.assertGear("514207b84382ec1fef000098").hasSshUrl("ssh://52380549e0b8cd1e0e000032@springeap6-foobarz.rhcloud.com")
				.assertGear("5146f047500446f12d00002e").inState(GearState.BUILDING)
				.hasCartridges("jbosseap-6", "mongodb-2.2");
		assertThat(new GearGroupsAssert(gearGroups)).assertGroup("514212ce500446b64e0000c0")
				.hasUUID("514212ce500446b64e0000c0")
				.assertGear("514212ce500446b64e0000b4").hasSshUrl("ssh://523809b75973ca569600019b@523809b75973ca569600019b-foobarz.rhcloud.com")
				.assertGear("514212ce500446b64e0000b4").inState(GearState.DEPLOYING)
				.hasCartridges("mongodb-2.2");
	}
}
