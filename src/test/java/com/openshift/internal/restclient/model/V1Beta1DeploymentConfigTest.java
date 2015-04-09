/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model;

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

public class V1Beta1DeploymentConfigTest {
	
	private static IDeploymentConfig config;
	
	@BeforeClass
	public static void setup(){
		IClient client = mock(IClient.class);
		ModelNode node = ModelNode.fromJSONString(Samples.DEPLOYMENT_CONFIG_MINIMAL.getContentAsString());
		config = new DeploymentConfig(node, client, ResourcePropertiesRegistry.getInstance().get("v1beta1", ResourceKind.DeploymentConfig));
	}
	
	@Test
	public void getReplicas(){
		assertEquals(1, config.getReplicas());
	}
	
	@Test
	public void getReplicaSelector() {
		Map<String, String> exp = new HashMap<String, String>();
		exp.put("name", "javaparks");
		assertEquals(exp, config.getReplicaSelector());
	}

}
