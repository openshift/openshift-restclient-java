/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.restclient.model.deploy;

import java.util.Collection;

import com.openshift.restclient.images.DockerImageURI;

public interface IDeploymentImageChangeTrigger extends IDeploymentTrigger {

    /**
     * Returns the name of the docker image repo to watch
     * 
     */
    DockerImageURI getFrom();

    /**
     * Automatically sets kind to "ImageStreamTag" if kind is empty
     * 
     */
    void setFrom(DockerImageURI fromImage);

    /**
     * The namespace of the ImageStreamTag
     * 
     */
    void setNamespace(String namespace);

    /**
     * @return The namespace of the ImageStreamTag
     * 
     */
    String getNamespace();

    void setKind(String kind);

    String getKind();

    boolean isAutomatic();

    void setAutomatic(boolean auto);

    /**
     * The container names for this trigger
     * 
     */
    Collection<String> getContainerNames();

    void setContainerNames(Collection<String> names);

    /**
     * Convenience method for setting a single container name
     * 
     */
    void setContainerName(String names);
}
