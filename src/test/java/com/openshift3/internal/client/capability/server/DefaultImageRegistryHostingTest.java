/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client.capability.server;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;

import com.openshift3.client.IClient;
import com.openshift3.client.OpenShiftException;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.capability.server.IImageRegistryHosting;
import com.openshift3.client.model.IService;
import com.openshift3.internal.client.IResourceFactory;
import com.openshift3.internal.client.ResourceFactory;
import com.openshift3.internal.client.model.Status;

public class DefaultImageRegistryHostingTest {

	private IClient client;
	private IService service;
	private IImageRegistryHosting capability;
	
	@Before
	public void setUp(){
		client = mock(IClient.class);
		IResourceFactory factory = new ResourceFactory(client);
		service = factory.create("v1beta1", ResourceKind.Service);
		capability = new DefaultImageRegistryHosting(client);
		
	}

	@Test
	public void testExistsWhenServiceExists() throws MalformedURLException {
		when(client.get(any(ResourceKind.class), anyString(), anyString())).thenReturn(service);
		assertTrue("Exp. capability to be enabled", capability.isSupported());
	}
	
	@Test
	public void testDoesNotExistWhenServiceDoesNotExists() throws MalformedURLException {
		OpenShiftException e = mock(OpenShiftException.class);
		when(e.getStatus()).thenReturn(mock(Status.class));
		when(client.get(any(ResourceKind.class), anyString(), anyString())).thenThrow(e);
		assertFalse("Exp. capability to be disabled", capability.isSupported());
	}
	

}
