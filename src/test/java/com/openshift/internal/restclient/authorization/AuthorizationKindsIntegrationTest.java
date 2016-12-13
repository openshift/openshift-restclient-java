 /*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.authorization;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.authorization.IRole;

/**
 * @author Jeff Cantrill
 */
public class AuthorizationKindsIntegrationTest {

	private IClient client;
	private IntegrationTestHelper helper = new IntegrationTestHelper();

	@Before
	public void setup () {
		client = helper.createClientForBasicAuth();
	}
	
	@Test
	public void testListRolesAssumingClusterAdmin(){
	    List<IRole> roles= client.list(ResourceKind.ROLE, "default");
	    assertThat(roles).isNotEmpty();
	}
	
	@Test
	public void testListPoliciesAssumingClusterAdmin(){
	    client.list(ResourceKind.POLICY, "default");
	}

	@Test
	public void testListPolicyBindingsAssumingClusterAdmin(){
	    client.list(ResourceKind.POLICY_BINDING, "default");
	}

}
