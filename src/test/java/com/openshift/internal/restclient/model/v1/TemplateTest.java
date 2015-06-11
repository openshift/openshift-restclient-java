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
import static org.mockito.Mockito.*;

import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.ResourceFactory;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.internal.restclient.model.template.Template;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.template.IParameter;
import com.openshift.restclient.model.template.ITemplate;
import com.openshift.restclient.utils.Samples;

/**
 * Test to validate the lookup paths are correct for the version
 * 
 * @author Jeff Cantrill
 */
public class TemplateTest{

	private static final String VERSION = "v1";
	private ITemplate template;
	
	@Before
	public void setUp(){
		IClient client = mock(IClient.class);
		when(client.getResourceFactory()).thenReturn(new ResourceFactory(client));
		ModelNode node = ModelNode.fromJSONString(Samples.V1_TEMPLATE.getContentAsString());
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
		Map<String, IParameter> parameters = template.getParameters();
		assertEquals("Exp. the number of items to be more than zero",5, parameters.size());
		IParameter param = parameters.get("MYSQL_PASSWORD");
		assertNotNull(param);
		assertEquals("",param.getValue());
		assertEquals("[a-zA-Z0-9]{8}",param.getFrom());
		assertEquals("expression",param.getGeneratorName());
		assertEquals("database password",param.getDescription());
	}
	
	@Test
	public void testGetLabels() {
		assertEquals("Exp. to retrieve the labels",2, template.getLabels().size());
		assertEquals("bar", template.getLabels().get("foo"));
	}

	@Test
	public void testGetObjectLabels() {
		assertEquals("Exp. to retrieve the object labels",1, template.getObjectLabels().size());
		assertEquals("application-template-stibuild", template.getObjectLabels().get("template"));
	}

}
