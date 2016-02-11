/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.model;

import java.util.Collection;

import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.image.ITagReference;

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
	 * Set a new tag related to a docker image
	 * 
	 * @param newTag the new tag to create
	 * @param imageId image id of the tag image
	 */
	void setTagWithImageId(String newTag, String fromImageId );
	
	/**
	 * Gets the long image id for the provided tag
	 * @param tagName   name of the image stream tag to interrogate
	 * @return
	 */
	String getImageId(String tagName);

	/**
	 * The collection of tag references for this imagestream
	 * @return
	 */
	Collection<ITagReference> getTags();
	
	/**
	 * The collection of tag names as listed
	 * in status.tags
	 * @return
	 */
	Collection<String> getTagNames();
}
