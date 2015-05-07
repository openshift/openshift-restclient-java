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
import static org.mockito.Mockito.*;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.ResourceFactory;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.internal.restclient.model.template.Template;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.template.ITemplate;
import com.openshift.restclient.utils.Samples;

/**
 * Test to validate the lookup paths are correct for the version
 * 
 * @author Jeff Cantrill
 */
public class TemplateTest{

	private static final String VERSION = "v1beta3";
	private ITemplate template;
	
	@Before
	public void setUp(){
		IClient client = mock(IClient.class);
		when(client.getResourceFactory()).thenReturn(new ResourceFactory(client));
		ModelNode node = ModelNode.fromJSONString(Samples.V1BETA3_TEMPLATE.getContentAsString());
		template = new Template(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION, ResourceKind.Template));
	}
	@Test
	public void testGetApiVersion() {
		assertEquals(VERSION, template.getApiVersion());
	}

	@Test
	public void testGetItems() {
		assertEquals("Exp. the number of items to be more than zero", 8, template.getItems().size());
	}

	@Test
	public void testGetParameters() {
		assertEquals("Exp. the number of items to be more than zero",5, template.getParameters().size());
	}
	
	@Test
	public void testGetLabels() {
		assertEquals("Exp. to retrieve the labels",1, template.getLabels().size());
	}

}
