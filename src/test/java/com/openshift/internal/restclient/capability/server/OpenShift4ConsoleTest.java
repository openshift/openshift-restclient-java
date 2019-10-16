/*******************************************************************************
 * Copyright (c) 2019 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.capability.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import com.openshift.internal.restclient.model.ConfigMap;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.server.IConsole;
import com.openshift.restclient.model.IBuild;
import com.openshift.restclient.model.IBuildConfig;
import com.openshift.restclient.model.IConfigMap;
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
import com.openshift.restclient.utils.Samples;

@RunWith(Theories.class)
public class OpenShift4ConsoleTest {

    private static final String HOST = "http://openshifthost.testing";
    private static final String NAMESPACE = "grumpy";
    private static final String RESOURCE_NAME = "eap-app";

    @DataPoints
    public static DataPair[] dataPoints = new DataPair[] {
        new DataPair(mockResource(IBuild.class, ResourceKind.BUILD),
               HOST + "/k8s/ns/" + NAMESPACE + "/builds/" + RESOURCE_NAME),
        new DataPair(mockResource(IBuildConfig.class, ResourceKind.BUILD_CONFIG),
               HOST + "/k8s/ns/" + NAMESPACE + "/buildconfigs/" + RESOURCE_NAME),
        new DataPair(mockResource(IDeploymentConfig.class, ResourceKind.DEPLOYMENT_CONFIG),
               HOST + "/k8s/ns/" + NAMESPACE + "/deploymentconfigs/" + RESOURCE_NAME),
        new DataPair(mockResource(IEvent.class, ResourceKind.EVENT),
               HOST + "/k8s/ns/" + NAMESPACE + "/events/"),
        new DataPair(mockResource(IImageStream.class, ResourceKind.IMAGE_STREAM),
               HOST + "/k8s/ns/" + NAMESPACE + "/imagestreams/" + RESOURCE_NAME),
        new DataPair(mockResource(IPod.class, ResourceKind.POD),
               HOST + "/k8s/ns/" + NAMESPACE + "/pods/" + RESOURCE_NAME),
        new DataPair(mockResource(IProject.class, ResourceKind.PROJECT),
                HOST + "/overview/ns/" + NAMESPACE),
        new DataPair(mockResource(IPersistentVolumeClaim.class, ResourceKind.PVC),
                HOST + "/k8s/ns/" + NAMESPACE + "/persistentvolumeclaims/" + RESOURCE_NAME),
        new DataPair(mockResource(IReplicationController.class, ResourceKind.REPLICATION_CONTROLLER),
                HOST + "/k8s/ns/" + NAMESPACE + "/replicationcontrollers/" + RESOURCE_NAME),
        new DataPair(mockResource(IRoute.class, ResourceKind.ROUTE),
                HOST + "/k8s/ns/" + NAMESPACE + "/routes/" + RESOURCE_NAME),
        new DataPair(mockResource(IService.class, ResourceKind.SERVICE),
               HOST + "/k8s/ns/" + NAMESPACE + "/services/" + RESOURCE_NAME),
        new DataPair(null,
               HOST),
        // inexistant resource -> show project
        new DataPair(mockResource(IResource.class, "INEXISTANT_RESOURCE_TYPE"),
                HOST + "/overview/ns/" + NAMESPACE)
    };
    
    private IClient client;
    private IConsole console;

    private static <R extends IResource> R mockResource(Class<R> resourceClass, String kind) {
        R resource = mock(resourceClass);
        when(resource.getNamespaceName()).thenReturn(NAMESPACE);
        when(resource.getName()).thenReturn(RESOURCE_NAME);
        doReturn(kind).when(resource).getKind();
        return resource;
    }

    @Before
    public void setup() throws MalformedURLException {
        this.client = mockClient(HOST);
        this.console = new Console(client);
    }

    private IClient mockClient(String host) throws MalformedURLException {
        IClient client = mock(IClient.class);
        doReturn(4).when(client).getOpenShiftMajorVersion();
        doReturn(new URL(host)).when(client).getBaseURL();
        
        ConfigMap map = mock(ConfigMap.class);
        Map<String, String> data = new HashMap<String, String>();
        data.put(Console.CONFIGMAP_DATA_CONSOLE_URL, HOST);
        doReturn(data).when(map).getData();
        doReturn(map).when(client).get(eq(ResourceKind.CONFIG_MAP), eq(Console.CONFIGMAP_CONSOLE_PUBLIC), eq(Console.NAMESPACE_OPENSHIFT_CONFIG_MANAGED));

        return client;
    }

    @Test
    public void shouldBeSupportedForOpenShift4() {
        assertTrue("Exp. Console endpoint should be supported for OpenShift 4 server", console.isSupported());
    }

    @Test
    public void shouldReturnNullIfOpenShift4HasNoConfigmap() {
        // given
        doReturn(null).when(client).get(eq(ResourceKind.CONFIG_MAP), eq(Console.CONFIGMAP_CONSOLE_PUBLIC), eq(Console.NAMESPACE_OPENSHIFT_CONFIG_MANAGED));
        // when
        String consoleUrl = console.getConsoleUrl();
        assertThat(consoleUrl).isNull();
    }

    @Test
    public void shouldReturnNullIfOpenShift4HasNoConfigmapData() {
        // given
        ConfigMap map = mock(ConfigMap.class);
        doReturn(null).when(map).getData();
        doReturn(map).when(client).get(eq(ResourceKind.CONFIG_MAP), eq(Console.CONFIGMAP_CONSOLE_PUBLIC), eq(Console.NAMESPACE_OPENSHIFT_CONFIG_MANAGED));
        // when
        String consoleUrl = console.getConsoleUrl();
        // then
        assertThat(consoleUrl).isNull();
    }

    @Test
    public void shouldReturnNullIfOpenShift4EmptyConfigmapData() {
        // given
        IConfigMap map = mock(IConfigMap.class);
        doReturn(new HashMap<>()).when(map).getData();
        doReturn(map).when(client).get(eq(ResourceKind.CONFIG_MAP), eq(Console.CONFIGMAP_CONSOLE_PUBLIC), eq(Console.NAMESPACE_OPENSHIFT_CONFIG_MANAGED));
        // when
        String consoleUrl = console.getConsoleUrl();
        // then
        assertThat(consoleUrl).isNull();
    }

    @Test
    public void shouldReturnConsoleUrlForForOpenShift4ConfigMap() {
        // given
        ModelNode node = ModelNode.fromJSONString(Samples.V1_CONFIGMAP_CONSOLE_PUBLIC.getContentAsString());
        ConfigMap map = new ConfigMap(node, client, null);
        doReturn(map).when(client).get(eq(ResourceKind.CONFIG_MAP), anyString(), anyString());
        // when
        String consoleUrl = console.getConsoleUrl();
        // then
        assertThat(consoleUrl).isEqualTo("https://console-openshift-console.apps.crw.codereadyqe.com");
    }

    @Theory
    public void shouldReturnConsoleUrlForForOpenShift4ForResource(DataPair dataPair) {
        IResource resourceMock = dataPair.getResource();
        String consoleUrl = console.getConsoleUrl(resourceMock);
        assertEquals(dataPair.getExpected(), consoleUrl);
    }
}
