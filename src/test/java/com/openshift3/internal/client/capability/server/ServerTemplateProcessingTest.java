/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client.capability.server;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.junit.Test;

import com.openshift3.client.ResourceKind;
import com.openshift3.client.model.IResource;
import com.openshift3.client.model.template.ITemplate;

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
