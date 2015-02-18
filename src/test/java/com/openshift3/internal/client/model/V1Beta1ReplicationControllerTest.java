/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.junit.BeforeClass;
import org.junit.Test;

import com.openshift.client.utils.Samples;
import com.openshift3.client.IClient;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.model.IReplicationController;
import com.openshift3.internal.client.model.properties.ResourcePropertiesRegistry;

public class V1Beta1ReplicationControllerTest {

	private static IReplicationController rc;
	
	@BeforeClass
	public static void setup(){
		IClient client = mock(IClient.class);
		ModelNode node = ModelNode.fromJSONString(Samples.V1BETA1_REPLICATION_CONTROLLER.getContentAsString());
		rc = new ReplicationController(node, client, ResourcePropertiesRegistry.getInstance().get("v1beta1", ResourceKind.ReplicationController));
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
	public void getCurrentReplicaCount(){
		assertEquals(2, rc.getCurrentReplicaCount());
	}
	
	@Test
	public void testGetImages(){
		String [] exp = new String []{"mysql"};
		assertArrayEquals(exp , rc.getImages().toArray());
	}
}
