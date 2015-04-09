/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.capability.server;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.ResourceFactory;
import com.openshift.internal.restclient.capability.server.DefaultImageRegistryHosting;
import com.openshift.internal.restclient.model.Status;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.server.IImageRegistryHosting;
import com.openshift.restclient.model.IService;

/**
 * @author Jeff Cantrill
 */
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
