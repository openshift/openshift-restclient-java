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

package com.openshift.restclient.model.image;

import java.util.Collection;

import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.IStatus;

public interface IImageStreamImport extends IResource {

    /**
     * Set to true to import tags for the imagestream; false to just retrieve
     * 
     */
    void setImport(boolean importTags);

    /**
     * Are the tags to be imported for the imagestream
     * 
     * @return true if import; false otherwise
     */
    boolean isImport();

    /**
     * Add image info those being imorted
     * 
     * @param fromKind
     *            The indirection of where to find the image. Typically is
     *            DockerImage, ImageStreamTag
     */
    void addImage(String fromKind, DockerImageURI imageUri);

    /**
     * The status of the image retrieval
     * 
     */
    Collection<IStatus> getImageStatus();

    /**
     * Get the raw json docker metadata for the given uir. Tries to match uri
     * without tag to the beginning of image.dockerImageReference
     * 
     * @return json string or null if not matched.
     */
    @Deprecated
    String getImageJsonFor(DockerImageURI uri);

    /**
     * Get the raw json docker metadata for the given tag. Assumes the result was
     * success
     * 
     * @param tag
     *            a tag for the image
     * @return json string or null if not matched.
     */
    String getImageJsonFor(String tag);
}
