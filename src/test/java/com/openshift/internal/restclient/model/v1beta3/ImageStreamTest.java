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

import com.openshift.internal.restclient.model.ImageStream;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IImageStream;
import com.openshift.restclient.utils.Samples;

/**
 * @author Jeff Cantrill
 */
public class ImageStreamTest {
	private static IImageStream repo;
	
	@BeforeClass
	public static void setup(){
		IClient client = mock(IClient.class);
		ModelNode node = ModelNode.fromJSONString(Samples.V1BETA3_IMAGE_STREAM.getContentAsString());
		repo = new ImageStream(node, client, ResourcePropertiesRegistry.getInstance().get("v1beta3", ResourceKind.ImageStream));
	}
	
	@Test
	public void getDockerImageRepository() {
		assertEquals(new DockerImageURI("172.30.159.174:5000/test/origin-ruby-sample"), repo.getDockerImageRepository());
	}

}
