/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.v1;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static com.openshift.internal.util.JBossDmrExtentions.*;

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
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IPort;
import com.openshift.restclient.utils.Samples;

/**
 * @author Jeff Cantrill
 */
public class DeploymentConfigTest {
	
	private static final String VERSION = "v1";
	private DeploymentConfig config;
	private IClient client;
	private ModelNode node;
	private Map<String, String[]> propertyKeys;
	
	@Before
	public void setup(){
		client = mock(IClient.class);
		node = ModelNode.fromJSONString(Samples.V1_DEPLOYMENT_CONIFIG.getContentAsString());
		propertyKeys = ResourcePropertiesRegistry.getInstance().get(VERSION, ResourceKind.DEPLOYMENT_CONFIG);
		config = new DeploymentConfig(node, client, propertyKeys);
	}
	
	@Test 
	public void getLabels() {
		assertArrayEquals(new String[] {"template"},config.getLabels().keySet().toArray(new String[] {}));
	}
	
	@Test
	public void getReplicas(){
		assertEquals(1, config.getReplicas());
	}

	@Test
	public void setReplicas(){
		config.setReplicas(3);
		assertEquals(3, config.getReplicas());
	}
	
	@Test
	public void setLatestVersionNumber(){
		config.setLatestVersionNumber(3);
		assertEquals(3, config.getLatestVersionNumber());
	}
	
	@Test
	public void setReplicaSelector() {
		Map<String, String> exp = new HashMap<String, String>();
		exp.put("foo", "bar");
		node = ModelNode.fromJSONString(Samples.V1_DEPLOYMENT_CONIFIG.getContentAsString());
		DeploymentConfig config = new DeploymentConfig(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION, ResourceKind.DEPLOYMENT_CONFIG));
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
		assertArrayEquals(new String[] {"ConfigChange"}, config.getTriggerTypes().toArray(new String[] {}));
	}
	
	@Test
	public void testGetDeploymentStrategyTypes() {
		assertEquals("Recreate", config.getDeploymentStrategyType());
	}
	
	@Test
	public void testAddContainer() {
		//remove containers hack
		String[] path = getPath(DeploymentConfig.DEPLOYMENTCONFIG_CONTAINERS);
		node.get(path).clear();
		
		//setup
		DockerImageURI uri = new DockerImageURI("aproject/an_image_name");
		IPort port = mock(IPort.class);
		when(port.getProtocol()).thenReturn("TCP");
		when(port.getContainerPort()).thenReturn(8080);
		Set<IPort> ports = new HashSet<>();
		ports.add(port);
		
		config.addContainer(uri, ports, new HashMap<String, String>());
		
		List<ModelNode> containers = node.get(path).asList();
		assertEquals(1, containers.size());
		
		//expectations
		ModelNode portNode = new ModelNode();
		portNode.get("protocol").set(port.getProtocol());
		portNode.get("containerPort").set(port.getContainerPort());
		
		ModelNode exp = new ModelNode();
		exp.get("name").set(uri.getName());
		exp.get("image").set(uri.getUriWithoutHost());
		exp.get("ports").add(portNode);
		
		assertEquals(exp.toJSONString(false), containers.get(0).toJSONString(false));
	}
	

}
