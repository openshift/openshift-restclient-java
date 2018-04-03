/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.v1;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import com.openshift.restclient.PredefinedResourceKind;
import org.jboss.dmr.ModelNode;
import org.junit.BeforeClass;
import org.junit.Test;

import com.openshift.internal.restclient.model.Build;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IBuild;
import com.openshift.restclient.model.build.IBuildStatus;
import com.openshift.restclient.utils.Samples;

/**
 * @author Jeff Cantrill
 */
public class BuildTest {
	
	private static final String VERSION = "v1";
	private static IBuild build;
	
	@BeforeClass
	public static void setup(){
		IClient client = mock(IClient.class);
		ModelNode node = ModelNode.fromJSONString(Samples.V1_BUILD.getContentAsString());
		build = new Build(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION, PredefinedResourceKind.BUILD.getIdentifier()));
	}
	
	@Test
	public void getStatus(){
		assertEquals("Running", build.getStatus());
	}

	@Test
	public void getMessage(){
		assertEquals("Some status message", build.getMessage());
	}

	@Test
	public void getOutputTo(){
		assertEquals("origin-ruby-sample:latest", build.getOutputTo().toString());
	}

	@Test
	public void getOutputKind(){
		assertEquals("ImageStreamTag", build.getOutputKind());
	}
	
	@Test
	public void getBuildStatus() {
		IBuildStatus status = build.getBuildStatus();
		assertNotNull(status);
		assertEquals("Running", status.getPhase());
		assertEquals("172.30.224.48:5000/rails-demo/rails-demo:latest", status.getOutputDockerImage().toString());
		assertEquals("2015-06-10T20:00:51Z", status.getStartTime());
		assertEquals(76895000000000L, status.getDuration());
	}
	
}
