/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.v1;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import com.openshift.restclient.PredefinedResourceKind;
import org.apache.commons.lang.StringUtils;
import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.model.image.ImageStreamImport;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IStatus;
import com.openshift.restclient.model.image.IImageStreamImport;
import com.openshift.restclient.utils.Samples;

/**
 * @author Jeff Cantrill
 */
public class ImageStreamImportTest {
	private static final String VERSION = "v1";
	private static IClient client;
	private IImageStreamImport stream;

	@Before
	public void setup(){
		client = mock(IClient.class);
		ModelNode node = ModelNode.fromJSONString(Samples.V1_IMAGE_STREAM_IMPORT.getContentAsString());
		stream = new ImageStreamImport(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION, PredefinedResourceKind.IMAGE_STREAM_IMPORT.getIdentifier()));
	}
	
	@Test
	public void testImport() {
		assertFalse(stream.isImport());
		
		stream.setImport(true);
		assertTrue(stream.isImport());
	}
	
	@Test
	public void testGetImageStatus() {
		Collection<IStatus> status = stream.getImageStatus();
		assertEquals(1, status.size());
		assertEquals("Success", status.iterator().next().getStatus());
	}
	
	@Test
	public void testGetImageJsonFor() {
		assertTrue("Exp. to find the json blob for the given image", StringUtils.isNotBlank(stream.getImageJsonFor("latest")));

		assertNull("Exp. to not find the json blob", stream.getImageJsonFor("bar"));
	}
}
