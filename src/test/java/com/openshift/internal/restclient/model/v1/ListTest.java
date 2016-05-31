/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.v1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.Collection;

import org.jboss.dmr.ModelNode;
import org.junit.BeforeClass;
import org.junit.Test;

import com.openshift.internal.restclient.ResourceFactory;
import com.openshift.internal.restclient.model.List;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IList;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.utils.Samples;

/**
 * @author Jeff Cantrill
 */
public class ListTest {
	
	private static final String VERSION = "v1";
	private static IClient client;
	
	@BeforeClass
	public static void setup() throws Exception{
		client = mock(IClient.class);
		when(client.getBaseURL()).thenReturn(new URL("https://localhost:8443"));
		when(client.getOpenShiftAPIVersion()).thenReturn(VERSION);
		when(client.getResourceFactory()).thenReturn(new ResourceFactory(client));
	}

	private IList createList(Samples sample) {
		ModelNode node = ModelNode.fromJSONString(sample.getContentAsString());
		return new List(node, client, null);
	}
	
	@Test
	public void testItemKindsAreDefined() {
		IList resource = createList(Samples.V1_BUILD_CONFIG_LIST);
		Collection<IResource> items = resource.getItems();
		assertTrue("Expected to be entries in the list",items.size() >0 );
		assertEquals(ResourceKind.BUILD_CONFIG, items.iterator().next().getKind());
	}

	@Test
	public void testEmptyList() {
		IList resource = createList(Samples.V1_CONFIG_MAP_LIST_EMPTY);
		Collection<IResource> items = resource.getItems();
		assertEquals(0, items.size());
	}

}
