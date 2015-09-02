/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.OpenShiftAPIVersion;
import com.openshift.internal.restclient.ResourceFactory;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IPod;
import com.openshift.restclient.model.IService;
import com.openshift.restclient.model.IServicePort;

/**
 * @author Jeff Cantrill
 */
public class ServiceTest {

	private IService service;
	private IClient client;

	@Before
	public void setup() {
		client = mock(IClient.class);
		when(client.getOpenShiftAPIVersion()).thenReturn(OpenShiftAPIVersion.v1.name());
		IResourceFactory factory = new ResourceFactory(client){};
		service = factory.stub(ResourceKind.SERVICE, OpenShiftAPIVersion.v1.name());
	}
	
	@Test
	public void testSetPorts() {
		List<IServicePort> ports = new ArrayList<>();
		ServicePort port = new ServicePort(new ModelNode());
		port.setName("foo");
		port.setTargetPort(12345);
		port.setProtocol("tcp");
		ports.add(port);
		service.setPorts(ports);
		assertEquals(1,service.getPorts().size());
		assertEquals("foo", service.getPorts().get(0).getName());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetPods() {
		//setup
		when(client.list(anyString(), anyString(), anyMap()))
			.thenReturn(new ArrayList<IPod>());
		
		service.addLabel("bar","foo");
		service.setSelector("foo", "bar");
		
		//exectute
		service.getPods();
		
		//confirm called with selector and not something else
		verify(client, times(1)).list(eq(ResourceKind.POD), anyString(), eq(service.getSelector()));
	}

}
