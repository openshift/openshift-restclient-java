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
 *     Roland T. Lichti - implementation of user.openshift.io/v1/identities
 ******************************************************************************/

package com.openshift.internal.restclient.model.v1;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.internal.restclient.model.user.OpenShiftIdentity;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.user.IIdentity;
import com.openshift.restclient.utils.Samples;

/**
 * Test to validate the lookup paths are correct for the version
 */
public class IdentityTest {

    private static final String VERSION = "v1";
    private IIdentity identity;

    @Before
    public void setUp() {
        IClient client = mock(IClient.class);
        ModelNode node = ModelNode.fromJSONString(Samples.V1_IDENTITY.getContentAsString());
        identity = new OpenShiftIdentity(node, client,
                                         ResourcePropertiesRegistry.getInstance().get(VERSION, ResourceKind.IDENTITY));
    }

    @Test
    public void testUserName() {
        assertEquals("test-admin", identity.getUserName());
    }

    @Test
    public void testUid() {
        assertEquals("94b42e96-0faa-11e5-9467-080027893417", identity.getUID());
    }

    @Test
    public void testProviderName() {
        assertEquals("anypassword", identity.getProviderName());
    }

    @Test
    public void testUserReferenceName() {
        assertEquals("test-admin", identity.getUser().getName());
    }

    @Test
    public void testUserReferenceUid() {
        assertEquals("94b42e96-0faa-11e5-9467-080027893417", identity.getUser().getUID());
    }
}
