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
	 * Get the image repository uri abstracted by this
	 * image stream
	 * @return
	 */
	DockerImageURI getDockerImageRepository();
	
	/**
	 * Set the image repository uri abstracted by this
	 * image stream
	 * @return
	 */
	void setDockerImageRepository(DockerImageURI imageUri);
	
	/**
	 * Set the image repository uri abstracted by this
	 * image stream
	 * @return
	 */
	void setDockerImageRepository(String imageUri);

	/**
	 * Sets a new tag in an image stream
	 * 
	 * @param newTag  the new tag to create
	 * @param fromTag existing tag the new tag is based from
	 */
	void setTag(String newTag, String fromTag);
	
	/**
	 * Add a tag to the list with the given name, and reference
	 * to the given kind and name.
	 * @param name
	 * @param fromKind
	 * @param fromName
	 * @return
	 */
	ITagReference addTag(String name, String fromKind, String fromName);
	
	/**
	 * Gets the long imagae id for the provided tag
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
