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

public interface IImageStream extends IResource {

    /**
     * Get the image repository uri abstracted by this image stream
     * 
     */
    DockerImageURI getDockerImageRepository();

    /**
     * Set the image repository uri abstracted by this image stream
     * 
     */
    void setDockerImageRepository(DockerImageURI imageUri);

    /**
     * Set the image repository uri abstracted by this image stream
     * 
     */
    void setDockerImageRepository(String imageUri);

    /**
     * Sets a new tag in an image stream
     * 
     * @param newTag
     *            the new tag to create
     * @param fromTag
     *            existing tag the new tag is based from
     */
    void setTag(String newTag, String fromTag);

    /**
     * Add a tag to the list with the given name, and reference to the given kind
     * and name.
     * 
     */
    ITagReference addTag(String name, String fromKind, String fromName);

    /**
     * Add a tag to the list with the given name, namespace, and reference to the
     * given kind, namespace, and name.
     * 
     */
    ITagReference addTag(String name, String fromKind, String fromName, String fromNamespace);

    /**
     * Gets the long imagae id for the provided tag
     * 
     * @param tagName
     *            name of the image stream tag to interrogate
     */
    String getImageId(String tagName);

    /**
     * The collection of tag references for this imagestream
     * 
     */
    Collection<ITagReference> getTags();

    /**
     * The collection of tag names as listed in status.tags
     * 
     */
    Collection<String> getTagNames();
}
