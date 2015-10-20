/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.model;

import com.openshift.restclient.images.DockerImageURI;

/**
 * @author Jeff Cantrill
 */
public interface IImageStream extends IResource{

	/**
	 * Retrieve the docker image URI for which this image repository
	 * is responsible
	 * @return
	 */
	DockerImageURI getDockerImageRepository();
	
	void setDockerImageRepository(DockerImageURI imageUri);
	
	/**
	 * Sets a new tag in an image stream
	 * 
	 * @param newTag  the new tag to create
	 * @param fromTag existing tag the new tag is based from
	 */
	void setTag(String newTag, String fromTag);
	
	/**
	 * Gets the long imagae id for the provided tag
	 * @param tagName   name of the image stream tag to interrogate
	 * @return
	 */
	String getImageId(String tagName);
}
