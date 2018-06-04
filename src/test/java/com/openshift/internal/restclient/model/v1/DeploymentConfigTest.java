/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.model.v1;

import static com.openshift.internal.util.JBossDmrExtentions.getPath;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.model.DeploymentConfig;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.PredefinedResourceKind;
import com.openshift.restclient.capability.resources.IDeployCapability;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IContainer;
import com.openshift.restclient.model.IPort;
import com.openshift.restclient.model.deploy.IDeploymentConfigChangeTrigger;
import com.openshift.restclient.model.deploy.IDeploymentImageChangeTrigger;
import com.openshift.restclient.model.deploy.IDeploymentTrigger;
import com.openshift.restclient.model.probe.IProbe;
import com.openshift.restclient.utils.Samples;

public class DeploymentConfigTest {

    private static final String CONTAINER2_NAME = "deployment";
    private static final String CONTAINER1_NAME = "ruby-helloworld-database";
    private static final String VERSION = "v1";
    private DeploymentConfig config;
    private IClient client;
    private ModelNode node;
    private Map<String, String[]> propertyKeys;
    private IContainer container1;
    private IContainer container2;

    @Before
    public void setup() {
        client = mock(IClient.class);
        node = ModelNode.fromJSONString(Samples.V1_DEPLOYMENT_CONIFIG.getContentAsString());
        propertyKeys = ResourcePropertiesRegistry.getInstance().get(VERSION, PredefinedResourceKind.DEPLOYMENT_CONFIG.getIdentifier());
        config = new DeploymentConfig(node, client, propertyKeys);
        container1 = config.getContainer(CONTAINER1_NAME);
        container2 = config.getContainer(CONTAINER2_NAME);
    }

    @Test
    public void testIsDeployCapable() {
        assertThat(config.supports(IDeployCapability.class)).isTrue();
    }

    @Test
    public void getLabels() {
        assertArrayEquals(new String[] { "template" }, config.getLabels().keySet().toArray(new String[] {}));
    }

    @Test
    public void getConfigChangeTrigger() {
        List<IDeploymentTrigger> trigger = new ArrayList<>(config.getTriggers());
        assertEquals("Exp. equal number of triggers", 2, trigger.size());
        assertTrue("Expected to find a config change trigger",
                IDeploymentConfigChangeTrigger.class.isAssignableFrom(trigger.get(0).getClass()));
        assertTrue("Expected to find a config change trigger",
                IDeploymentImageChangeTrigger.class.isAssignableFrom(trigger.get(1).getClass()));

        IDeploymentImageChangeTrigger ict = (IDeploymentImageChangeTrigger) trigger.get(1);
        assertEquals("foo", ict.getNamespace());
    }

    @Test
    public void getReplicas() {
        assertEquals(1, config.getReplicas());
    }

    @Test
    public void setReplicas() {
        config.setReplicas(3);
        assertEquals(3, config.getReplicas());
    }

    @Test
    public void setLatestVersionNumber() {
        config.setLatestVersionNumber(3);
        assertEquals(3, config.getLatestVersionNumber());
    }

    @Test
    public void setReplicaSelector() {
        Map<String, String> exp = new HashMap<String, String>();
        exp.put("foo", "bar");
        node = ModelNode.fromJSONString(Samples.V1_DEPLOYMENT_CONIFIG.getContentAsString());
        DeploymentConfig config = new DeploymentConfig(node, client,
                ResourcePropertiesRegistry.getInstance().get(VERSION, PredefinedResourceKind.DEPLOYMENT_CONFIG.getIdentifier()));
        config.setReplicaSelector(exp);
        assertEquals(exp, config.getReplicaSelector());
    }

    @Test
    public void getReplicaSelector() {
        Map<String, String> exp = new HashMap<String, String>();
        exp.put("name", "database");
        assertEquals(exp, config.getReplicaSelector());
    }

    @Test
    public void getTriggerTypes() {
        assertArrayEquals(new String[] { "ConfigChange", "ImageChange" },
                config.getTriggerTypes().toArray(new String[] {}));
    }

    @Test
    public void testGetDeploymentStrategyTypes() {
        assertEquals("Recreate", config.getDeploymentStrategyType());
    }

    @Test
    public void testAddContainer() {
        // remove containers hack
        String[] path = getPath(DeploymentConfig.DEPLOYMENTCONFIG_CONTAINERS);
        node.get(path).clear();

        // setup
        IPort port = mock(IPort.class);
        when(port.getProtocol()).thenReturn("TCP");
        when(port.getContainerPort()).thenReturn(8080);
        Set<IPort> ports = new HashSet<>();
        ports.add(port);

        DockerImageURI uri = new DockerImageURI("aproject/an_image_name");
        config.addContainer(uri, ports, new HashMap<String, String>());

        List<ModelNode> containers = node.get(path).asList();
        assertEquals(1, containers.size());

        // expectations
        ModelNode portNode = new ModelNode();
        portNode.get("protocol").set(port.getProtocol());
        portNode.get("containerPort").set(port.getContainerPort());

        ModelNode exp = new ModelNode();
        exp.get("name").set(uri.getName());
        exp.get("image").set(uri.getUriWithoutHost());
        exp.get("ports").add(portNode);

        assertEquals(exp.toJSONString(false), containers.get(0).toJSONString(false));
    }

    @Test
    public void shouldNotReturnLivenessProbe() {
        IProbe livenessProbe = container1.getLivenessProbe();
        assertThat(livenessProbe).isNull();
    }

    @Test
    public void shouldNotReturnReadinessProbe() {
        IProbe readinessProbe = container1.getReadinessProbe();
        assertThat(readinessProbe).isNull();
    }

    @Test
    public void shouldReturnLivenessProbe() {
        // given
        // when
        IProbe probe = container2.getLivenessProbe();

        // then
        assertThat(probe).isNotNull();
        assertThat(probe.getInitialDelaySeconds()).isEqualTo(11);
        assertThat(probe.getTimeoutSeconds()).isEqualTo(12);
        assertThat(probe.getPeriodSeconds()).isEqualTo(13);
        assertThat(probe.getSuccessThreshold()).isEqualTo(14);
        assertThat(probe.getFailureThreshold()).isEqualTo(15);
    }

    @Test
    public void shouldAlterLivenessProbe() {
        // given
        // when
        IProbe probe = container2.getLivenessProbe();
        probe.setInitialDelaySeconds(100);
        probe.setTimeoutSeconds(101);
        probe.setPeriodSeconds(102);
        probe.setSuccessThreshold(103);
        probe.setFailureThreshold(104);

        // then
        assertThat(probe).isNotNull();
        assertThat(probe.getInitialDelaySeconds()).isEqualTo(100);
        assertThat(probe.getTimeoutSeconds()).isEqualTo(101);
        assertThat(probe.getPeriodSeconds()).isEqualTo(102);
        assertThat(probe.getSuccessThreshold()).isEqualTo(103);
        assertThat(probe.getFailureThreshold()).isEqualTo(104);
    }

    @Test
    public void shouldReturnReadynessProbe() {
        // given
        // when
        IProbe probe = container2.getReadinessProbe();

        // then
        assertThat(probe).isNotNull();
        assertThat(probe.getInitialDelaySeconds()).isEqualTo(3);
        assertThat(probe.getTimeoutSeconds()).isEqualTo(4);
        assertThat(probe.getPeriodSeconds()).isEqualTo(5);
        assertThat(probe.getSuccessThreshold()).isEqualTo(6);
        assertThat(probe.getFailureThreshold()).isEqualTo(7);
    }

    @Test
    public void shouldAlterReadinessProbe() {
        // given
        // when
        IProbe probe = container2.getReadinessProbe();
        probe.setInitialDelaySeconds(200);
        probe.setTimeoutSeconds(201);
        probe.setPeriodSeconds(202);
        probe.setSuccessThreshold(203);
        probe.setFailureThreshold(204);

        // then
        assertThat(probe).isNotNull();
        assertThat(probe.getInitialDelaySeconds()).isEqualTo(200);
        assertThat(probe.getTimeoutSeconds()).isEqualTo(201);
        assertThat(probe.getPeriodSeconds()).isEqualTo(202);
        assertThat(probe.getSuccessThreshold()).isEqualTo(203);
        assertThat(probe.getFailureThreshold()).isEqualTo(204);
    }

}
