/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.model.v1;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.model.ExecAction;
import com.openshift.internal.restclient.model.Lifecycle;
import com.openshift.internal.restclient.model.Pod;
import com.openshift.internal.restclient.model.Port;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IContainer;
import com.openshift.restclient.model.IExecAction;
import com.openshift.restclient.model.IPod;
import com.openshift.restclient.model.IPort;
import com.openshift.restclient.utils.Samples;

public class PodTest {

    private static final String VERSION = "v1";

    private IClient client;
    private IPod pod1;
    private IContainer pod1container1;

    private IPod pod2;

    @Before
    public void setup() {
        this.client = mock(IClient.class);
        ModelNode node = ModelNode.fromJSONString(Samples.V1_POD.getContentAsString());
        this.pod1 = createPod(node);
        this.pod1container1 = pod1.getContainers().stream().findFirst().orElse(null);

        node = ModelNode.fromJSONString(Samples.V1_POD_MULTICONTAINER_READY.getContentAsString());
        this.pod2 = createPod(node);
    }

    @Test
    public void testGetHost() {
        assertEquals("127.0.0.1", pod1.getHost());
    }

    @Test
    public void testGetStatusPhase() {
        assertEquals("Running", pod1.getStatus());
    }

    @Test
    public void testGetStatusDeletion() {
        ((Pod) pod1).getNode().get("metadata", "deletionTimestamp").set("2016-11-02T16:31:55Z");
        assertEquals("Terminating", pod1.getStatus());
    }

    @Test
    public void testGetStatusWaitingReason() {
        ((Pod) pod1).getNode().get("status", "containerStatuses").asList().get(0).get("state").set("waiting",
                new ModelNode().set("reason", "ReasonNotToWork"));
        assertEquals("ReasonNotToWork", pod1.getStatus());
    }

    @Test
    public void testGetStatusTerminateReason() {
        ((Pod) pod1).getNode().get("status", "containerStatuses").asList().get(0).get("state").set("terminated",
                new ModelNode().set("reason", "ReasonToTerminate"));
        assertEquals("ReasonToTerminate", pod1.getStatus());
    }

    /**
     * Check that if both reason and exit code are set, status returns the reason.
     */
    @Test
    public void testGetStatusTerminateReasonAndExit() {
        ModelNode node = new ModelNode();
        node.get("reason").set("ReasonToTerminate");
        node.get("exitCode").set("Let's go! Time to exit!");
        ((Pod) pod1).getNode().get("status", "containerStatuses").asList().get(0).get("state").set("terminated", node);
        assertEquals("ReasonToTerminate", pod1.getStatus());
    }

    @Test
    public void testGetStatusTerminatedSignal() {
        ((Pod) pod1).getNode().get("status", "containerStatuses").asList().get(0).get("state").set("terminated",
                new ModelNode().set("signal", "Alarm! Terminate!"));
        assertEquals("Signal: Alarm! Terminate!", pod1.getStatus());
    }

    /**
     * Check that if both signal and exit code are set, status returns the signal.
     */
    @Test
    public void testGetStatusTerminatedSignalAndExit() {
        ModelNode node = new ModelNode();
        node.get("signal").set("Alarm! Terminate!");
        node.get("exitCode").set("Let's go! Time to exit!");
        ((Pod) pod1).getNode().get("status", "containerStatuses").asList().get(0).get("state").set("terminated", node);
        assertEquals("Signal: Alarm! Terminate!", pod1.getStatus());
    }

    @Test
    public void testGetStatusTerminatedExit() {
        ((Pod) pod1).getNode().get("status", "containerStatuses").asList().get(0).get("state").set("terminated",
                new ModelNode().set("exitCode", "Let's go! Time to exit!"));
        assertEquals("Exit Code: Let's go! Time to exit!", pod1.getStatus());
    }

    @Test
    public void testGetImages() {
        String[] exp = new String[] { "openshift/origin-deployer:v0.6" };
        assertArrayEquals(exp, pod1.getImages().toArray());
    }

    @Test
    public void getIP() {
        assertEquals("1.2.3.4", pod1.getIP());
    }

    @Test
    public void getContainerPorts() {
        Port port = new Port(new ModelNode());
        port.setName("http");
        port.setProtocol("TCP");
        port.setContainerPort(8080);
        Set<IPort> ports = new HashSet<>();
        ports.add(port);
        assertEquals(ports, pod1.getContainerPorts());
    }

    @Test
    public void testAddContainer() {
        Collection<IContainer> initial = pod1.getContainers();
        IContainer foo = pod1.addContainer("foo");

        foo.setLifecycle(new Lifecycle.Builder()
                .preStop(new ExecAction.Builder().command("cmd1").command("cmd2").build()).build());

        Collection<IContainer> containers = pod1.getContainers();
        assertEquals(initial.size() + 1, containers.size());

        Optional<IContainer> container = containers.stream()
                .filter(c -> "foo".equals(c.getName())
                        && "cmd1".equals(((IExecAction) c.getLifecycle().getPreStop().get()).getCommand().get(0)))
                .findFirst();
        assertTrue("Exp. the container to be added", container.isPresent());
    }

    @Test
    public void getContainerCommands() {
        List<String> cmd = pod1container1.getCommand();
        List<String> cmdArgs = pod1container1.getCommandArgs();
        assertEquals("/bin/sh", cmd.get(0));
        assertEquals("-c", cmdArgs.get(0));
        assertEquals("echo 'hello'", cmdArgs.get(1));
    }

    @Test
    public void getContainerResourceRequirements() {
        assertEquals("1", pod1container1.getRequestsCPU());
        assertEquals("128Mi", pod1container1.getRequestsMemory());
        assertEquals("4", pod1container1.getLimitsCPU());
        assertEquals("1Gi", pod1container1.getLimitsMemory());
    }

    @Test
    public void resetContainerResourceRequirements() {
        pod1container1.setRequestsMemory(null);
        pod1container1.setRequestsCPU(null);
        pod1container1.setLimitsMemory(null);
        pod1container1.setLimitsCPU(null);
        assertEquals("", pod1container1.getRequestsCPU());
        assertEquals("", pod1container1.getRequestsMemory());
        assertEquals("", pod1container1.getLimitsCPU());
        assertEquals("", pod1container1.getLimitsMemory());
    }
    
    @Test
    public void shouldBeReadyIfAllContainersAreReady() {
        assertThat(pod2.isReady()).isTrue();
    }

    @Test
    public void shouldNotBeReadyIfAtLeast1ContainerIsNotReady() {
        IPod nonReadyPod = setContainerReady(1, false, pod2, client);
        
        assertThat(nonReadyPod.isReady()).isFalse();
    }

    private IPod setContainerReady(int index, boolean ready, IPod pod, IClient client) {
        ModelNode node = ModelNode.fromJSONString(pod.toJson());
        ModelNode podStatusNode = node.get("status");
        ModelNode allContainerStatusNode = podStatusNode.get("containerStatuses");
        assertThat(allContainerStatusNode.isDefined()).isTrue();
        assertThat(allContainerStatusNode.get(index).isDefined()).isTrue();;
        ModelNode statusNode = allContainerStatusNode.get(index);
        ModelNode readyNode = statusNode.get("ready");
        readyNode.set(ready);
        return createPod(node);
    }
  
    private Pod createPod(ModelNode node) {
        return new Pod(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION, ResourceKind.POD));
    }
}
