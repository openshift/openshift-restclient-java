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

import java.util.ArrayList;

import org.junit.Test;

import com.openshift.internal.restclient.ResourceFactory;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IPod;
import com.openshift.restclient.model.IService;

/**
 * @author Jeff Cantrill
 */
public class ServiceTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testGetPods() {
		//setup
		IClient client = mock(IClient.class);
		when(client.list(anyString(), anyString(), anyMap()))
			.thenReturn(new ArrayList<IPod>());
		IResourceFactory factory = new ResourceFactory(client);
		IService service = factory.create("v1beta1", ResourceKind.SERVICE);
		service.addLabel("bar","foo");
		service.setSelector("foo", "bar");
		
		//exectute
		service.getPods();
		
		//confirm called with selector and not something else
		verify(client, times(1)).list(eq(ResourceKind.POD), anyString(), eq(service.getSelector()));
	}

}
