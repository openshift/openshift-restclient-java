/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.openshift.internal.restclient.OpenShiftAPIVersion;
import com.openshift.internal.restclient.ResourceFactory;
import com.openshift.internal.restclient.model.Project;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IService;

/**
 * @author Jeff Cantrill
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectTest {

	@Mock private IClient client;
	private Project project;
	
	@Before
	public void setup(){
		project = new ResourceFactory(client){}.create(OpenShiftAPIVersion.v1beta1.toString(), ResourceKind.PROJECT);
		project.setName("aprojectname");
	}
	
	@Test
	public void getResourcesShouldUseProjectNameForNamespaceWhenGettingResources() {
		ArrayList<IService> services = new ArrayList<IService>();
		when(client.<IService>list(eq(ResourceKind.SERVICE), anyString())).thenReturn(services);
		List<IService> resources = project.getResources(ResourceKind.SERVICE);
		
		assertEquals("Exp. a list of services", services, resources);
		verify(client).list(eq(ResourceKind.SERVICE), eq(project.getName()));
	}

}
