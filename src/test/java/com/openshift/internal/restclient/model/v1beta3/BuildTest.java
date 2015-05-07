/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.v1beta3;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.jboss.dmr.ModelNode;
import org.junit.BeforeClass;
import org.junit.Test;

import com.openshift.internal.restclient.model.Build;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IBuild;
import com.openshift.restclient.utils.Samples;

/**
 * @author Jeff Cantrill
 */
public class BuildTest {
	
	private static IBuild build;
	
	@BeforeClass
	public static void setup(){
		IClient client = mock(IClient.class);
		ModelNode node = ModelNode.fromJSONString(Samples.V1BETA3_BUILD.getContentAsString());
		build = new Build(node, client, ResourcePropertiesRegistry.getInstance().get("v1beta3", ResourceKind.Build));
	}
	
	@Test
	public void getStatus(){
		assertEquals("Pending", build.getStatus());
	}

	@Test
	public void getMessage(){
		assertEquals("Some status message", build.getMessage());
	}
	
}
