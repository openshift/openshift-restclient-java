/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.capability.resources;

import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.resources.IImageStreamImportCapability;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.image.IImageStreamImport;

/**
 * 
 * @author jeff.cantrill
 *
 */
public class ImageStreamImportCapability implements IImageStreamImportCapability {

	private IClient client;
	private IProject project;

	public ImageStreamImportCapability(IProject project, IClient client) {
		this.project = project;
		this.client = client;
	}
	
	@Override
	public IImageStreamImport importImageMetadata(DockerImageURI uri) {
		
		IImageStreamImport streamImport = client.getResourceFactory().stub(ResourceKind.IMAGE_STREAM_IMPORT, "jbosstools-openshift-deployimage", project.getName());
		streamImport.setImport(false);
		streamImport.addImage("DockerImage", uri);
		return client.create(streamImport);
	}


	@Override
	public boolean isSupported() {
		return true;
	}

	@Override
	public String getName() {
		return ImageStreamImportCapability.class.getSimpleName();
	}

}
