/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model;

import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.restclient.IClient;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IImageStream;

/**
 * @author Jeff Cantrill
 */
public class ImageStream extends KubernetesResource implements IImageStream {

	public ImageStream(){
		this(new ModelNode(), null, null);
	}
	
	public ImageStream(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
	}

	@Override
	public void setDockerImageRepository(DockerImageURI uri) {
		set(IMAGESTREAM_DOCKER_IMAGE_REPO, uri.getAbsoluteUri());		
	}

	@Override
	public DockerImageURI getDockerImageRepository() {
		return new DockerImageURI(asString(IMAGESTREAM_DOCKER_IMAGE_REPO));
	}

}
