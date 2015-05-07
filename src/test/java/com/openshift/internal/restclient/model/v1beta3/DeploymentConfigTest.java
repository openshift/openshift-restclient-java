/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.v1beta3;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.junit.BeforeClass;
import org.junit.Test;

import com.openshift.internal.restclient.model.DeploymentConfig;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IDeploymentConfig;
import com.openshift.restclient.utils.Samples;

/**
 * @author Jeff Cantrill
 */
public class DeploymentConfigTest {
	
	private static final String VERSION = "v1beta3";
	private static IDeploymentConfig config;
	
	@BeforeClass
	public static void setup(){
		IClient client = mock(IClient.class);
		ModelNode node = ModelNode.fromJSONString(Samples.V1BETA3_DEPLOYMENT_CONIFIG.getContentAsString());
		config = new DeploymentConfig(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION, ResourceKind.DeploymentConfig));
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

}
