/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.client.utils.Samples;
import com.openshift3.client.IClient;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.model.template.ITemplate;
import com.openshift3.internal.client.ResourceFactory;
import com.openshift3.internal.client.model.properties.ResourcePropertiesRegistry;
import com.openshift3.internal.client.model.template.Template;

/**
 * Test to validate the lookup paths are correct for the version
 */
public class V1Beta1TemplateTest{

	private ITemplate template;
	
	@Before
	public void setUp(){
		IClient client = mock(IClient.class);
		when(client.getResourceFactory()).thenReturn(new ResourceFactory(client));
		ModelNode node = ModelNode.fromJSONString(Samples.V1BETA1_TEMPLATE.getContentAsString());
		template = new Template(node, client, ResourcePropertiesRegistry.getInstance().get("v1beta1", ResourceKind.Template));
	}
	
	@Test
	public void testGetItems() {
		assertEquals("Exp. the number of items to be more than zero", 8, template.getItems().size());
	}

	@Test
	public void testGetParameters() {
		assertEquals("Exp. the number of items to be more than zero",4, template.getParameters().size());
	}

}
