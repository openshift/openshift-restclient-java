/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.model.v1;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.jboss.dmr.ModelNode;
import org.junit.BeforeClass;
import org.junit.Test;

import com.openshift.internal.restclient.model.Status;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IStatus;
import com.openshift.restclient.utils.Samples;

/**
 * Test to validate the lookup paths are correct for the version
 */
public class StatusTest {

    private static final String VERSION = "v1";
    private static IStatus status;

    @BeforeClass
    public static void setUp() {
        IClient client = mock(IClient.class);
        ModelNode node = ModelNode.fromJSONString(Samples.V1_Status.getContentAsString());
        status = new Status(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION, ResourceKind.STATUS));
    }

    @Test
    public void testGetMessage() {
        assertEquals("Unable to determine kind and namespace from url, /osapi/users", status.getMessage());
    }

    @Test
    public void testGetCode() {
        assertEquals(403, status.getCode());
    }

    @Test
    public void testGetStatus() {
        assertEquals("Failure", status.getStatus());
    }
}
