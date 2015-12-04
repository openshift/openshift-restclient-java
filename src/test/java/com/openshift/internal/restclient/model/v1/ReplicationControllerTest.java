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
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.jboss.dmr.ModelNode;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.openshift.internal.restclient.model.ModelNodeBuilder;
import com.openshift.internal.restclient.model.ReplicationController;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IContainer;
import com.openshift.restclient.model.IPort;
import com.openshift.restclient.model.IReplicationController;
import com.openshift.restclient.model.volume.IVolume;
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
	
	@Test
	public void setReplicaSelector() {
		Map<String, String> exp = new HashMap<String, String>();
		exp.put("foo", "bar");
		node = ModelNode.fromJSONString(Samples.V1_REPLICATION_CONTROLLER.getContentAsString());
		rc = new ReplicationController(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION, ResourceKind.REPLICATION_CONTROLLER));
		rc.setReplicaSelector(exp);
		assertEquals(exp, rc.getReplicaSelector());
	}

	@Test
	public void setReplicaSelectorWithKeyValue() {
		Map<String, String> exp = new HashMap<String, String>();
		exp.put("foo", "bar");
		node = ModelNode.fromJSONString(Samples.V1_REPLICATION_CONTROLLER.getContentAsString());
		rc = new ReplicationController(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION, ResourceKind.REPLICATION_CONTROLLER));
		rc.setReplicaSelector("foo","bar");
		assertEquals(exp, rc.getReplicaSelector());
	}
	
	@Test
	public void getReplicaSelector() {
		Map<String, String> labels = new HashMap<String, String>();
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
	
	
}
