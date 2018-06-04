/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.openshift.internal.restclient.OpenShiftAPIVersion;
import com.openshift.internal.restclient.ResourceFactory;
import com.openshift.restclient.IClient;
import com.openshift.restclient.PredefinedResourceKind;
import com.openshift.restclient.model.IService;

@RunWith(MockitoJUnitRunner.class)
public class ProjectTest {

    @Mock
    private IClient client;
    private Project project;

    @Before
    public void setup() {
        project = new ResourceFactory(client) {
        }.create(OpenShiftAPIVersion.v1.toString(), PredefinedResourceKind.PROJECT.getIdentifier());
        project.setName("aprojectname");
    }

    @Test
    public void getResourcesShouldUseProjectNameForNamespaceWhenGettingResources() {
        ArrayList<IService> services = new ArrayList<IService>();
        when(client.<IService>list(eq(PredefinedResourceKind.SERVICE.getIdentifier()), anyString())).thenReturn(services);
        List<IService> resources = project.getResources(PredefinedResourceKind.SERVICE.getIdentifier());

        assertEquals("Exp. a list of services", services, resources);
        verify(client).list(eq(PredefinedResourceKind.SERVICE.getIdentifier()), eq(project.getName()));
    }

}
