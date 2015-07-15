/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.v1beta3;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;


import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.model.project.OpenshiftProjectRequest;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.project.IProjectRequest;
import com.openshift.restclient.utils.Samples;

/**
 * Test to validate the lookup paths are correct for the version
 * @author Jeff Cantrill
 */
public class ProjectRequestTest{

	private static final String VERSION = "v1beta3";
	private IProjectRequest request;
	
	@Before
	public void setUp(){
		IClient client = mock(IClient.class);
		ModelNode node = ModelNode.fromJSONString(Samples.V1BETA3_PROJECT_REQUEST.getContentAsString());
		request = new OpenshiftProjectRequest(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION, ResourceKind.PROJECT_REQUEST));
	}
	
	@Test
	public void setDisplayName() {
		request.setDisplayName("the other display name");
		assertEquals("the other display name", request.getDisplayName());
	}
	
	@Test
	public void testGetDisplayName() {
		assertEquals("the display name", request.getDisplayName());
	}

	@Test
	public void testGetDescription() {
		assertEquals("The project description", request.getDescription());
	}

	@Test
	public void testSetDescription() {
		request.setDescription("The other project description");
		assertEquals("The other project description", request.getDescription());
	}
}