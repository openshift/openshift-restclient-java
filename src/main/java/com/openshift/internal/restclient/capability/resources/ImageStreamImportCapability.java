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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.UnsupportedEndpointException;
import com.openshift.restclient.authorization.ResourceForbiddenException;
import com.openshift.restclient.capability.resources.IImageStreamImportCapability;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.IStatus;
import com.openshift.restclient.model.image.IImageStreamImport;

/**
 * 
 * @author jeff.cantrill
 *
 */
public class ImageStreamImportCapability implements IImageStreamImportCapability {

	private static final Logger LOG = LoggerFactory.getLogger(IImageStreamImportCapability.class);
	private IClient client;
	private IProject project;

	public ImageStreamImportCapability(IProject project, IClient client) {
		this.project = project;
		this.client = client;
	}
	
	@Override
	public IImageStreamImport importImageMetadata(DockerImageURI uri) {
		
		LOG.debug("first trying imagestreamimport against OpenShift server...");
		IImageStreamImport streamImport = client.getResourceFactory().stub(ResourceKind.IMAGE_STREAM_IMPORT, "jbosstools-openshift-deployimage", project.getName());
		streamImport.setImport(false);
		streamImport.addImage("DockerImage", uri);
		try {
			IImageStreamImport result = client.create(streamImport);
			for (IStatus status : result.getImageStatus()) {
				if(IStatus.SUCCESS.equalsIgnoreCase(status.getStatus())) {
					return result;
				}
			}
		}catch(ResourceForbiddenException | UnsupportedEndpointException e) {
			LOG.info("Unsuccessful in trying OpenShift server. ImageStreamImport is not supported.");
		}
		LOG.debug("Unsuccessful in trying OpenShift server.  Trying dockerhub v2 registry...");
		DockerRegistryImageStreamImportCapability reg = new DockerRegistryImageStreamImportCapability(this.project, client.getResourceFactory(), client);
		return reg.importImageMetadata(uri);
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
