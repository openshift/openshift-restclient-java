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

package com.openshift.restclient.capability.resources;

import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.image.IImageStreamImport;

/**
 * Import tags from a repository for an image
 * 
 *
 */
public interface IImageStreamImportCapability extends ICapability {

    /**
     * Import docker image metadata
     * 
     */
    IImageStreamImport importImageMetadata(DockerImageURI uri);

}
