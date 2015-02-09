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
import static org.mockito.Mockito.mock;

import org.jboss.dmr.ModelNode;
import org.junit.BeforeClass;
import org.junit.Test;

import com.openshift.client.utils.Samples;
import com.openshift3.client.IClient;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.images.DockerImageURI;
import com.openshift3.client.model.IImageRepository;

public class V1Beta1ImageRepositoryTest {
	private static IImageRepository repo;
	
	@BeforeClass
	public static void setup(){
		IClient client = mock(IClient.class);
		ModelNode node = ModelNode.fromJSONString(Samples.IMAGE_REPOSITORY_MINIMAL.getContentAsString());
		repo = new ImageRepository(node, client, ResourcePropertiesRegistry.getInstance().get("v1beta1", ResourceKind.ImageRepository));
	}
	
	@Test
	public void getD() {
		assertEquals(new DockerImageURI("172.30.17.3:5001/test/origin-ruby-sample"), repo.getDockerImageRepository());
	}

}
