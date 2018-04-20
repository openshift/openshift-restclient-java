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
import java.util.Set;

public interface IPod extends IResource {

    /**
     * Gets the IP of the Pod
     * 
     */
    String getIP();

    /**
     * Gets the name of the host on which the pod is running
     * 
     */
    String getHost();

    /**
     * Gets the collection of image names for the pod containers
     * 
     */
    Collection<String> getImages();

    /**
     * Gets the status of the pod
     * 
     */
    String getStatus();

    /**
     * Retrieve the set of ports that the containers are using
     */
    Set<IPort> getContainerPorts();

    /**
     * Add a container with the given name. This is useful if creating a pod
     * directly without a resource controller
     * 
     */
    IContainer addContainer(String name);

    /**
     * Retrieve all the containers spec'd for the pod
     * 
     * @return collection of containers
     */
    Collection<IContainer> getContainers();
}
