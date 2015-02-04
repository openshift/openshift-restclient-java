/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client.model;

import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift3.client.IClient;
import com.openshift3.client.images.ImageUri;
import com.openshift3.client.model.IImageRepository;

public class ImageRepository extends KubernetesResource implements IImageRepository {

	public ImageRepository(){
		this(new ModelNode(), null, null);
	}
	
	public ImageRepository(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
	}

	public ImageRepository(String json) {
		super(json);
	}

	public void setDockerImageRepository(ImageUri uri) {
		set(IMAGEREPO_DOCKER_IMAGE_REPO, uri.getAbsoluteUri());		
	}

}
