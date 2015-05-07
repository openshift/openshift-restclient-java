/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.v1beta1;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.model.Service;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IService;
import com.openshift.restclient.utils.Samples;

/**
 * Test to validate the lookup paths are correct for the version
 * @author Jeff Cantrill
 */
public class V1Beta1ServiceTest{

	private IService service;
	
	@Before
	public void setUp(){
		IClient client = mock(IClient.class);
		ModelNode node = ModelNode.fromJSONString(Samples.V1BETA1_SERVICE.getContentAsString());
		service = new Service(node, client, ResourcePropertiesRegistry.getInstance().get("v1beta1", ResourceKind.Service));
	}
	
	@Test
	public void testGetPortalIP() {
		assertEquals("172.30.17.23", service.getPortalIP());
	}

	@Test
	public void testGetContainerPort() {
		assertEquals(5000, service.getContainerPort());
	}

	@Test
	public void testSetContainerPort() {
		service.setContainerPort(5030);
		assertEquals(5030, service.getContainerPort());
	}

	@Test
	public void testGetPort() {
		assertEquals(5001, service.getPort());
	}

	@Test
	public void testSetPort() {
		service.setPort(5055);
		assertEquals(5055, service.getPort());
	}

	@Test
	public void testGetSelector() {
		Map<String, String> selector = new HashMap<String, String>();
		selector.put("name", "hello-openshift");
		assertEquals(selector, service.getSelector());
	}

	@Test
	public void testSetSelectorSimple() {
		Map<String, String> selector = new HashMap<String, String>();
		selector.put("name", "myselector");
		service.setSelector("name","myselector");
		assertEquals(selector, service.getSelector());
	}
	
	@Test
	public void testGetName() {
		assertEquals("hello-openshift-service", service.getName());
	}

	@Test
	public void testSetName() {
		service.setName("hello-openshift");
		assertEquals("hello-openshift", service.getName());
	}
	
	@Test
	public void testGetNamespace() {
		assertEquals("hello-openshift-project", service.getNamespace());
	}

	@Test
	public void testSetNamespace() {
		service.setNamespace("foo");
		assertEquals("foo", service.getNamespace());
	}
}
