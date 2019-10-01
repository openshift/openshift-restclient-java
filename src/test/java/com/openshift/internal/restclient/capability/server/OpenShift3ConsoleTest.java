/*******************************************************************************
 * Copyright (c) 2019 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.capability.server;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.server.IConsole;
import com.openshift.restclient.model.IBuild;
import com.openshift.restclient.model.IBuildConfig;
import com.openshift.restclient.model.IDeploymentConfig;
import com.openshift.restclient.model.IEvent;
import com.openshift.restclient.model.IImageStream;
import com.openshift.restclient.model.IPod;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.IReplicationController;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.IService;
import com.openshift.restclient.model.route.IRoute;
import com.openshift.restclient.model.volume.IPersistentVolumeClaim;

@RunWith(Theories.class)
public class OpenShift3ConsoleTest {

    private static final String NAMESPACE = "namespace";

    private static final String RESOURCE_NAME = "qwerty";

    private static final String HOST = "http://openshifthost.testing";

    @DataPoints
    public static DataPair[] dataPoints = new DataPair[] {
        new DataPair(mockResource(IBuild.class, ResourceKind.BUILD),
               HOST + "/console/project/" + NAMESPACE + "/browse/builds/label/" + RESOURCE_NAME),
        new DataPair(mockResource(IBuildConfig.class, ResourceKind.BUILD_CONFIG),
               HOST + "/console/project/" + NAMESPACE + "/browse/builds/" + RESOURCE_NAME),
        new DataPair(mockResource(IDeploymentConfig.class, ResourceKind.DEPLOYMENT_CONFIG),
               HOST + "/console/project/" + NAMESPACE + "/browse/deployments/" + RESOURCE_NAME),
        new DataPair(mockResource(IEvent.class, ResourceKind.EVENT),
               HOST + "/console/project/" + NAMESPACE + "/browse/events/"),
        new DataPair(mockResource(IImageStream.class, ResourceKind.IMAGE_STREAM),
               HOST + "/console/project/" + NAMESPACE + "/browse/images/" + RESOURCE_NAME),
        new DataPair(mockResource(IPod.class, ResourceKind.POD),
               HOST + "/console/project/" + NAMESPACE + "/browse/pods/" + RESOURCE_NAME),
        new DataPair(mockResource(IProject.class, ResourceKind.PROJECT),
               HOST + "/console/project/" + NAMESPACE),
        new DataPair(mockResource(IPersistentVolumeClaim.class, ResourceKind.PVC),
               HOST + "/console/project/" + NAMESPACE + "/browse/persistentvolumeclaims/" + RESOURCE_NAME),
        new DataPair(mockResource(IReplicationController.class, ResourceKind.REPLICATION_CONTROLLER),
               HOST + "/console/project/" + NAMESPACE + "/browse/rc/" + RESOURCE_NAME),
        new DataPair(mockResource(IRoute.class, ResourceKind.ROUTE),
                HOST + "/console/project/" + NAMESPACE + "/browse/routes/" + RESOURCE_NAME),
        new DataPair(mockResource(IService.class, ResourceKind.SERVICE),
               HOST + "/console/project/" + NAMESPACE + "/browse/services/" + RESOURCE_NAME),
        new DataPair(null,
               HOST + "/console"),
        // inexistant resource -> show project
        new DataPair(mockResource(IResource.class, "INEXISTANT_RESOURCE_TYPE"),
                HOST + "/console/project/" + NAMESPACE)
    };
    
    private IClient client;
    private IConsole console;

    @Before
    public void setup() throws MalformedURLException {
        this.client = mockClient(HOST);
        this.console = new Console(client);
    }

    private static <R extends IResource> R mockResource(Class<R> resourceClass, String kind) {
        R resource = mock(resourceClass);
        when(resource.getNamespaceName()).thenReturn(NAMESPACE);
        when(resource.getName()).thenReturn(RESOURCE_NAME);
        when(resource.getLabels()).thenReturn(new HashMap<String, String>() {
            {
                put("buildconfig", "label");
            }
        });
        doReturn(kind).when(resource).getKind();
        return resource;
    }

    private IClient mockClient(String host) throws MalformedURLException {
        IClient client = mock(IClient.class);
        doReturn(3).when(client).getOpenShiftMajorVersion();
        doReturn(new URL(host)).when(client).getBaseURL();
        return client;
    }

    @Test
    public void shouldBeSupportedForOpenShift3() {
        assertTrue("Exp. Console capability should be supported for OpenShift 3 server", console.isSupported());
    }

    @Test
    public void shouldReturnConsoleUrlForOpenShift3() {
        assertThat(console.getConsoleUrl()).isEqualTo(HOST + "/console");
    }

    @Theory
    public void shouldReturnConsoleUrlForOpenShift3ForResource(DataPair dataPair) {
        IResource resourceMock = dataPair.getResource();
        String consoleUrl = console.getConsoleUrl(resourceMock);
        assertEquals(dataPair.getExpected(), consoleUrl);
    }
}
