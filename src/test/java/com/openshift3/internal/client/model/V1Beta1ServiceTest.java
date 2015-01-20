package com.openshift3.internal.client.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.client.utils.Samples;
import com.openshift3.client.IClient;
import com.openshift3.client.model.IService;

public class V1Beta1ServiceTest<K> {

	private IService service;
	
	@Before
	public void setUp(){
		IClient client = mock(IClient.class);
		ModelNode node = ModelNode.fromJSONString(Samples.V1BETA1_SERVICE.getContentAsString());
		service = new Service(node, client);
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
