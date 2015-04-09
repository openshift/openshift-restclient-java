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

import java.util.ArrayList;

import org.junit.Test;

import com.openshift.internal.restclient.capability.server.ServerTemplateProcessing;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.template.ITemplate;

/**
 * @author Jeff Cantrill
 */
public class ServerTemplateProcessingTest {

	@Test
	public void testTemplateConfigAdapter() {
		ITemplate template = mock(ITemplate.class);
		when(template.toString()).thenReturn("theToStringValue");
		final ArrayList<IResource> items = new ArrayList<IResource>();
		when(template.getItems()).thenReturn(items);
		final String namespace = "test";
		ServerTemplateProcessing.TemplateConfigAdapter adapter = new ServerTemplateProcessing.TemplateConfigAdapter (template, namespace);
		
		assertEquals(namespace, adapter.getNamespace());
		assertEquals(ResourceKind.TemplateConfig, adapter.getKind());
		assertEquals(template.toString(), adapter.toString());
		assertEquals(items, adapter.getItems());
	}

}
