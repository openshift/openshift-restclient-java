/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 *
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 *     Roland T. Lichti - implementation of user.openshift.io/v1/groups
 ******************************************************************************/

package com.openshift.internal.restclient.model.v1;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.HashSet;
import java.util.Set;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.internal.restclient.model.user.OpenShiftGroup;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.user.IGroup;
import com.openshift.restclient.utils.Samples;

/**
 * Test to validate the lookup paths are correct for the version
 */
public class GroupTest {

    private static final String VERSION = "v1";
    private IGroup group;

    @Before
    public void setUp() {
        IClient client = mock(IClient.class);
        ModelNode node = ModelNode.fromJSONString(Samples.V1_GROUP.getContentAsString());
        group = new OpenShiftGroup(node, client,
                                   ResourcePropertiesRegistry.getInstance().get(VERSION, ResourceKind.GROUP));
    }

    @Test
    public void testGroupName() {
        assertEquals("test-group", group.getName());
    }

    @Test
    public void testUid() {
        assertEquals("5374bc7a-c985-11e8-8799-525400d45cd2", group.getUID());
    }

    @Test
    public void testUsers() {
        Set<String> userlist = new HashSet<>(1);
        userlist.add("test-admin");
        
        assertEquals(group.getUsers(), userlist);
    }
}
