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
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.model.Project;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.utils.Samples;

/**
 * Test to validate the lookup paths are correct for the version
 */
public class ProjectTest {

    private static final String VERSION = "v1";
    private IProject project;

    @Before
    public void setUp() {
        IClient client = mock(IClient.class);
        ModelNode node = ModelNode.fromJSONString(Samples.V1_PROJECT.getContentAsString());
        project = new Project(node, client,
                ResourcePropertiesRegistry.getInstance().get(VERSION, ResourceKind.PROJECT));
    }

    @Test
    public void testGetDisplayName() {
        assertEquals("OpenShift 3 Sample", project.getDisplayName());
    }

    @Test
    public void testGetDescription() {
        assertEquals("This is an example project to demonstrate OpenShift v3", project.getDescription());
    }
}