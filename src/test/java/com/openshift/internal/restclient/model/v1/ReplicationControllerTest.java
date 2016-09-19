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
import static org.fest.assertions.MapAssert.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.jboss.dmr.ModelNode;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.openshift.internal.restclient.model.ModelNodeBuilder;
import com.openshift.internal.restclient.model.ReplicationController;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.internal.restclient.model.volume.EmptyDirVolumeSource;
import com.openshift.internal.restclient.model.volume.SecretVolumeSource;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IConfigMapKeySelector;
import com.openshift.restclient.model.IContainer;
import com.openshift.restclient.model.IEnvironmentVariable;
import com.openshift.restclient.model.IEnvironmentVariable.IEnvVarSource;
import com.openshift.restclient.model.IObjectFieldSelector;
import com.openshift.restclient.model.IPort;
import com.openshift.restclient.model.IReplicationController;
import com.openshift.restclient.model.ISecretKeySelector;
import com.openshift.restclient.model.volume.IVolume;
import com.openshift.restclient.model.volume.IVolumeMount;
import com.openshift.restclient.model.volume.IVolumeSource;
import com.openshift.restclient.utils.Samples;

/**
 * @author Jeff Cantrill
 */
public class ReplicationControllerTest {

	private static final String VERSION = "v1";
	private static IReplicationController rc;
	private static ModelNode node;
	private static IClient client;
	
	@Before
	public void setup(){
		client = mock(IClient.class);
		node = ModelNode.fromJSONString(Samples.V1_REPLICATION_CONTROLLER.getContentAsString());
		rc = new ReplicationController(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION, ResourceKind.REPLICATION_CONTROLLER));
	}
	
	public void testGetEnvironmentVariablesWithValueFrom() {

		Collection<IEnvironmentVariable> envVars = rc.getEnvironmentVariables();
		
		//fieldref
		Optional<IEnvironmentVariable> envVar = envVars.stream().filter(e->"OPENSHIFT_KUBE_PING_NAMESPACE".equals(e.getName())).findFirst();
		assertTrue("Exp. to find env var", envVar.isPresent());
		IEnvVarSource from = envVar.get().getValueFrom();
		assertTrue(from instanceof IObjectFieldSelector);
		IObjectFieldSelector selector = (IObjectFieldSelector)from;
		assertEquals("v1",selector.getApiVersion());
		assertEquals("metadata.namespace",selector.getFieldPath());

		//configmapkeyref
		envVar = envVars.stream().filter(e->"OPENSHIFT_CONFIGMAP_KEY_REF".equals(e.getName())).findFirst();
		assertTrue("Exp. to find env var", envVar.isPresent());
		from = envVar.get().getValueFrom();
		assertTrue(from instanceof IConfigMapKeySelector);
		IConfigMapKeySelector configSelector = (IConfigMapKeySelector) from;
		assertEquals("xyz",configSelector.getName());
		assertEquals("abc123",configSelector.getKey());

		//secretkeyref
		envVar = envVars.stream().filter(e->"OPENSHIFT_SECRET_KEY_REF".equals(e.getName())).findFirst();
		assertTrue("Exp. to find env var", envVar.isPresent());
		from = envVar.get().getValueFrom();
		assertTrue(from instanceof ISecretKeySelector);
		ISecretKeySelector secretKeySelector = (ISecretKeySelector) from;
		assertEquals("bar",secretKeySelector.getName());
		assertEquals("foo",secretKeySelector.getKey());
	}
	
	@Test
	public void testEnvironmentVariable() {
		//add
		rc.setEnvironmentVariable("foo", "bar");
		Collection<IEnvironmentVariable> envVars = rc.getEnvironmentVariables();
		Optional<IEnvironmentVariable> envVar = envVars.stream().filter(e->"foo".equals(e.getName())).findFirst();
		assertTrue("Exp. to find env var", envVar.isPresent());
		assertEquals("bar", envVar.get().getValue());

		//update
		int size = rc.getEnvironmentVariables().size();
		rc.setEnvironmentVariable("foo", "baz");
		assertEquals(size, rc.getEnvironmentVariables().size());
		envVars = rc.getEnvironmentVariables();
		envVar = envVars.stream().filter(e->"foo".equals(e.getName())).findFirst();
		assertEquals("baz", envVar.get().getValue());

		rc.removeEnvironmentVariable("foo");
		assertEquals(size - 1, rc.getEnvironmentVariables().size());
	}

	@Test
	public void testEnvironmentVariableFromMissingEnv() {
		String[] path = getPath(ReplicationController.SPEC_TEMPLATE_CONTAINERS);
		ModelNode containers = node.get(path);
		containers.get(0).remove("env");
		testEnvironmentVariable();
	}

	@Test
	public void testEnvironmentVariableForANamedContainer() {
		rc.setEnvironmentVariable("ruby-helloworld-database", "fooz", "balls");
		Collection<IEnvironmentVariable> envVars = rc.getEnvironmentVariables("ruby-helloworld-database");
		Optional<IEnvironmentVariable> envVar = envVars.stream().filter(e->"fooz".equals(e.getName())).findFirst();
		assertTrue("Exp. to find env var", envVar.isPresent());
		assertEquals("balls", envVar.get().getValue());
	}
	
	@Test
	public void setReplicaSelector() {
		Map<String, String> exp = new HashMap<>();
		exp.put("foo", "bar");
		rc.setReplicaSelector(exp);
		assertEquals(exp, rc.getReplicaSelector());
	}

	@Test
	public void setReplicaSelectorFromMissingSelector() {
		String[] path = new String[]{"status"};
		node.get(path).clear();
		setReplicaSelector();
	}


	@Test
	public void setReplicaSelectorWithKeyValue() {
		Map<String, String> exp = new HashMap<>();
		exp.put("foo", "bar");
		rc.setReplicaSelector("foo","bar");
		assertEquals(exp, rc.getReplicaSelector());
	}
	
	@Test
	public void getReplicaSelector() {
		Map<String, String> labels = new HashMap<>();
		labels.put("name", "database");
		labels.put("deployment", "database-1");
		labels.put("deploymentconfig", "database");
		assertEquals(labels, rc.getReplicaSelector());
	}
	
	@Test
	public void getDesiredReplicaCount(){
		assertEquals(1, rc.getDesiredReplicaCount());
	}

	@Test
	public void setDesiredReplicaCount(){
		rc.setDesiredReplicaCount(9);
		assertEquals(9, rc.getDesiredReplicaCount());

		rc.setReplicas(6);
		assertEquals(6, rc.getDesiredReplicaCount());
	}
	
	@Test
	public void getCurrentReplicaCount(){
		assertEquals(2, rc.getCurrentReplicaCount());
	}
	
	@Test
	public void testGetImages(){
		String [] exp = new String []{"openshift/mysql-55-centos7:latest"};
		assertArrayEquals(exp , rc.getImages().toArray());
	}
	
	@Test 
	public void testGetContainer() {
		assertNull(rc.getContainer(" "));
		assertNotNull(rc.getContainer("ruby-helloworld-database"));
	}
	@Test 
	public void testGetContainers() {
		Collection<IContainer> containers = rc.getContainers();
		assertNotNull(containers);
		assertEquals(1, containers.size());
		
		String[] path = getPath(ReplicationController.SPEC_TEMPLATE_CONTAINERS);
		node.get(path).clear();
		assertNotNull(rc.getContainers());
	}
	
	@Test
	public void testAddContainer() throws JSONException {
		//remove containers hack
		String[] path = getPath(ReplicationController.SPEC_TEMPLATE_CONTAINERS);
		node.get(path).clear();
		//setup
		DockerImageURI uri = new DockerImageURI("aproject/an_image_name");
		IPort port = mock(IPort.class);
		when(port.getProtocol()).thenReturn("TCP");
		when(port.getContainerPort()).thenReturn(8080);
		Set<IPort> ports = new HashSet<>();
		ports.add(port);
		
		IContainer container = rc.addContainer(uri.getName(), uri, ports, new HashMap<String, String>(), Arrays.asList("/tmp"));
		
		List<ModelNode> containers = node.get(path).asList();
		assertEquals(1, containers.size());
		
		ModelNode exp = new ModelNodeBuilder()
			.set("name", uri.getName())
			.set("image",uri.toString())
			.add("ports", new ModelNodeBuilder()
				.set("containerPort", port.getContainerPort())
				.set("protocol", port.getProtocol())
			)
			.add("volumeMounts", new ModelNodeBuilder()
				.set("name", uri.getName() +"-"+1)
				.set("mountPath", "/tmp")
				.set("readOnly", false)
			)
			.build();
		
		JSONAssert.assertEquals(exp.toJSONString(false), container.toJSONString(), true);
		
		Object [] sourceNames = rc.getVolumes().stream().map(IVolumeSource::getName).toArray();

		Object [] contVolNames = container.getVolumes().stream().map(IVolume::getName).toArray();
		assertArrayEquals(sourceNames,contVolNames);
	}
	
	@Test
	public void testAddContainerAllowsContainerToBeFurtherManipulated()  throws JSONException{
		//remove containers hack
		String[] path = getPath(ReplicationController.SPEC_TEMPLATE_CONTAINERS);
		node.get(path).clear();
		
		//setup
		DockerImageURI uri = new DockerImageURI("arepo/aproject/an_image_name");
		IPort port = mock(IPort.class);
		when(port.getProtocol()).thenReturn("TCP");
		when(port.getContainerPort()).thenReturn(8080);
		Set<IPort> ports = new HashSet<>();
		ports.add(port);
		
		IVolumeMount mount = mock(IVolumeMount.class);
		when(mount.getName()).thenReturn(uri.getName() +"-"+1);
		when(mount.getMountPath()).thenReturn("/tmp");
		when(mount.isReadOnly()).thenReturn(Boolean.FALSE);
		Set<IVolumeMount> mounts = new HashSet<>();
		mounts.add(mount);
		
		IContainer container = rc.addContainer(uri.getName());
		container.setImage(uri);
		container.setPorts(ports);
		container.setVolumeMounts(mounts);
		
		ModelNode exp = new ModelNodeBuilder()
				.set("name", uri.getName())
				.set("image",uri.toString())
				.add("ports", new ModelNodeBuilder()
					.set("containerPort", port.getContainerPort())
					.set("protocol", port.getProtocol())
				)
				.add("volumeMounts", new ModelNodeBuilder()
					.set("name", uri.getName() +"-"+1)
					.set("mountPath", "/tmp")
					.set("readOnly", false)
				)
				.build();
		
		JSONAssert.assertEquals(exp.toJSONString(false), container.toJSONString(), true);
	}
	
	@Test
	public void shouldReturnTemplateLabels() {
		Map<String, String> labels = rc.getTemplateLabels();
		assertThat(labels)
				.hasSize(3)
				.includes(entry("deployment", "database-1"))
				.includes(entry("deploymentconfig", "database"))
				.includes(entry("name", "database"));
	}

	@Test
	public void testSetVolumes() {
		IVolumeSource source = new EmptyDirVolumeSource("myvolume");
		rc.setVolumes(Collections.singleton(source));
		Set<IVolumeSource> volumes = rc.getVolumes();
		assertEquals(1, volumes.size());
		assertEquals("myvolume", volumes.iterator().next().getName());
	}

	@Test
	public void testAddSecretVolumeToPodSpec() throws JSONException {
            IVolumeMount volumeMount = new IVolumeMount() {
                    public String getName() { return "my-secret"; }
                    public String getMountPath() { return "/path/to/my/secret/"; }
                    public boolean isReadOnly() { return true; }
                    public void setName(String name) {}
                    public void setMountPath(String path) {}
                    public void setReadOnly(boolean readonly) {}
                };
            SecretVolumeSource source = new SecretVolumeSource(volumeMount.getName());
		    source.setSecretName("the-secret");
	        rc.addVolume(source);
            Set<IVolumeSource> podSpecVolumes = rc.getVolumes();
            Optional vol = podSpecVolumes.stream()
                .filter(v->v.getName().equals(volumeMount.getName()))
                .findFirst();
            assertTrue("Expected to find secret volume in pod spec", vol.isPresent());
        }
}
