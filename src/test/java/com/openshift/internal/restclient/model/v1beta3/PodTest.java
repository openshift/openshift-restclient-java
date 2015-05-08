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

import org.jboss.dmr.ModelNode;
import org.junit.BeforeClass;
import org.junit.Test;

import com.openshift.internal.restclient.model.Pod;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IPod;
import com.openshift.restclient.utils.Samples;

/**
 * @author Jeff Cantrill
 */
public class PodTest {

	private static final String VERSION = "v1beta3";
	private static IPod pod;
	
	@BeforeClass
	public static void setup(){
		IClient client = mock(IClient.class);
		ModelNode node = ModelNode.fromJSONString(Samples.V1BETA3_POD.getContentAsString());
		pod = new Pod(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION, ResourceKind.Pod));
	}
	
	@Test
	public void testGetHost(){
		assertEquals("127.0.0.1", pod.getHost());
	}
	
	@Test
	public void testGetStatus(){
		assertEquals("Failed", pod.getStatus());
	}
	
	@Test
	public void testGetImages(){
		String [] exp = new String []{"openshift/origin-sti-builder:v0.5"};
		assertArrayEquals(exp, pod.getImages().toArray());
	}

	@Test
	public void getIP() {
		assertEquals("172.17.0.2", pod.getIP());
	}

}
