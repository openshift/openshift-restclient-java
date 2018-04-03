/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.v1;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.openshift.restclient.PredefinedResourceKind;
import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.model.PortFactory;
import com.openshift.internal.restclient.model.Service;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IService;
import com.openshift.restclient.model.IServicePort;
import com.openshift.restclient.utils.Samples;

/**
 * Test to validate the lookup paths are correct for the version
 * @author Jeff Cantrill
 */
public class ServiceTest{

	private static final String VERSION = "v1";
	private IService service;
	
	@Before
	public void setUp(){
		IClient client = mock(IClient.class);
		ModelNode node = ModelNode.fromJSONString(Samples.V1_SERVICE.getContentAsString());
		service = new Service(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION, PredefinedResourceKind.SERVICE.getIdentifier()));
	}
	
	@Test
	public void testGetPortalIP() {
		assertEquals("172.30.57.114", service.getPortalIP());
	}

    @Test
    public void testGetClusterIP() {
        assertEquals("172.30.57.114", service.getClusterIP());
    }

	@Test
	public void testGetTargetPort() {
		assertEquals("3306", service.getTargetPort());
	}

	@Test
	public void testSetTargetPort() {
		service.setTargetPort(5030);
		assertEquals("5030", service.getTargetPort());
	}

	@Test
	public void testGetPort() {
		assertEquals(5434, service.getPort());
	}

	@Test
	public void testSetPort() {
		service.setPort(5055);
		assertEquals(5055, service.getPort());
	}

	@Test
	public void testGetSelector() {
		Map<String, String> selector = new HashMap<String, String>();
		selector.put("name", "database");
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
		assertEquals("database", service.getName());
	}

	@Test
	public void testSetName() {
		((Service) service).setName("hello-openshift");
		assertEquals("hello-openshift", service.getName());
	}
	
	@Test
	public void testGetNamespace() {
		assertEquals("test", service.getNamespaceName());
	}

	@Test
	public void testSetNamespace() {
		// pre-condition
		// operation
		((Service) service).setNamespace("foo");
		// verification
		assertEquals("foo", service.getNamespaceName());
	}
	
	@Test
	public void testGetPorts() {
		IServicePort [] ports = service.getPorts().toArray(new IServicePort[] {});
		assertEquals(2, ports.length);
		assertEquals(PortFactory.createServicePort("db", "TCP", 5434, 3306), ports[0]);
		assertEquals(PortFactory.createServicePort("other", "TCP", 9999, 8888), ports[1]);
	}

	@Test
	public void testSetPorts() {
		IServicePort port = PortFactory.createServicePort("newport", "TCP", 4444, 5555);
		service.setPorts(Arrays.asList(port));
		IServicePort [] ports = service.getPorts().toArray(new IServicePort[] {});
		assertEquals(1, service.getPorts().size());
		assertEquals(port, ports[0]);
	}

	@Test
	public void testAddPorts() {
		IServicePort port = service.addPort(8299, 9299, "testport");
		service.setPorts(Arrays.asList(port));
		IServicePort [] ports = service.getPorts().toArray(new IServicePort[] {});
		assertEquals(1, service.getPorts().size());
		assertEquals(port, ports[0]);
	}

	@Test
	public void testSetType() {
		service.setType("NodePort");
		assertEquals("NodePort", service.getType());
	}
}
