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

import com.openshift.internal.restclient.model.ReplicationController;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IReplicationController;
import com.openshift.restclient.utils.Samples;

/**
 * @author Jeff Cantrill
 */
public class ReplicationControllerTest {

	private static IReplicationController rc;
	
	@BeforeClass
	public static void setup(){
		IClient client = mock(IClient.class);
		ModelNode node = ModelNode.fromJSONString(Samples.V1BETA3_REPLICATION_CONTROLLER.getContentAsString());
		rc = new ReplicationController(node, client, ResourcePropertiesRegistry.getInstance().get("v1beta3", ResourceKind.REPLICATION_CONTROLLER));
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
		String [] exp = new String []{"openshift/mysql-55-centos7"};
		assertArrayEquals(exp , rc.getImages().toArray());
	}
}
