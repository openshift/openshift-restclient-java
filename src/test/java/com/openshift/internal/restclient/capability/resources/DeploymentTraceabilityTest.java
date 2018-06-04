/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.capability.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.openshift.restclient.IClient;
import com.openshift.restclient.PredefinedResourceKind;
import com.openshift.restclient.model.IPod;
import com.openshift.restclient.model.IReplicationController;

@RunWith(MockitoJUnitRunner.class)
public class DeploymentTraceabilityTest {

    private DeploymentTraceability capability;

    @Mock
    private IReplicationController deployment;
    @Mock
    private IPod resource;
    @Mock
    private IClient client;

    @Before
    public void setUp() {
        capability = new DeploymentTraceability(resource, client);

        when(resource.getNamespaceName()).thenReturn("mynamespace");

        when(client.get(eq(PredefinedResourceKind.REPLICATION_CONTROLLER.getIdentifier()), eq("foobar"), eq("mynamespace")))
                .thenReturn(deployment);
    }

    @Test
    public void supportedWhenAnnotationsHaveADeploymentKey() {
        when(resource.isAnnotatedWith(eq("deployment"))).thenReturn(true);
        when(resource.getAnnotation("deployment")).thenReturn("foobar");

        assertEquals("Exp. to get the deployment", deployment, capability.getDeployment());

        verify(client).get(eq(PredefinedResourceKind.REPLICATION_CONTROLLER.getIdentifier()), eq("foobar"), eq("mynamespace"));
    }

    @Test
    public void unsupportedWhenAnnotationsDoNotHaveADeploymentKey() {
        assertNull("Exp. to get the deployment", capability.getDeployment());
    }

}
