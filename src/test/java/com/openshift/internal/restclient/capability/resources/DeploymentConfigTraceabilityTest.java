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
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IDeploymentConfig;
import com.openshift.restclient.model.IPod;

@RunWith(MockitoJUnitRunner.class)
public class DeploymentConfigTraceabilityTest {

    private DeploymentConfigTraceability capability;

    @Mock
    private IDeploymentConfig config;
    @Mock
    private IPod resource;
    @Mock
    private IClient client;

    @Before
    public void setUp() {
        capability = new DeploymentConfigTraceability(resource, client);

        when(resource.getNamespaceName()).thenReturn("mynamespace");

        when(client.get(eq(ResourceKind.DEPLOYMENT_CONFIG), eq("foobar"), eq("mynamespace"))).thenReturn(config);
    }

    @Test
    public void supportedWhenAnnotationsHaveADeploymentKey() {
        when(resource.isAnnotatedWith(eq("deploymentconfig"))).thenReturn(true);
        when(resource.getAnnotation("deploymentconfig")).thenReturn("foobar");

        assertEquals("Exp. to get the deploymentConfig", config, capability.getDeploymentConfig());

        verify(client).get(eq(ResourceKind.DEPLOYMENT_CONFIG), eq("foobar"), eq("mynamespace"));
    }

    @Test
    public void unsupportedWhenAnnotationsDoNotHaveADeploymentKey() {
        assertNull("Exp. to get the deploymentConfig", capability.getDeploymentConfig());
    }

}
