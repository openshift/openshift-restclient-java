/*******************************************************************************
* Copyright (c) 2016-2019 Red Hat, Inc. Distributed under license by Red Hat, Inc.
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
import org.junit.Ignore;
import org.junit.Test;

import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.authorization.IRole;

public class AuthorizationKindsIntegrationTest {

    private IClient client;
    private IntegrationTestHelper helper = new IntegrationTestHelper();

    @Before
    public void setup() {
        client = helper.createClientForBasicAuth();
    }

    @Ignore("Role endpoint was deprecated in v1")
    @Test
    public void testListRolesAssumingClusterAdmin() {
        List<IRole> roles = client.list(ResourceKind.ROLE, IntegrationTestHelper.getDefaultNamespace());
        assertThat(roles).isNotEmpty();
    }

    @Ignore("Policy endpoint was deprecated in v1")
    @Test
    public void testListPoliciesAssumingClusterAdmin() {
        client.list(ResourceKind.POLICY, IntegrationTestHelper.getDefaultNamespace());
    }

    @Ignore("PolicyBinding endpoint was deprecated in v1")
    @Test
    public void testListPolicyBindingsAssumingClusterAdmin() {
        client.list(ResourceKind.POLICY_BINDING, IntegrationTestHelper.getDefaultNamespace());
    }

}
