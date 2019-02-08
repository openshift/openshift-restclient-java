/*******************************************************************************
 * Copyright (c) 2015-2018 Red Hat, Inc. Distributed under license by Red Hat, Inc.
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
     * Returns the IP of this pod.
     * 
     * @return ip of this pod.
     */
    String getIP();

    /**
     * Returns the hostname of the host on which the pod is running.
     * 
     * @return the hostname of this pod.
     * 
     */
    String getHost();

    /**
     * Returns the names of the images that the containers of this pod are using.
     * 
     * @return the image names for the containers of this pod.
     */
    Collection<String> getImages();

    /**
     * Returns the status of the pod. The pod status is derived from the status of
     * all it's containers. The current implementation is limited to the status of
     * the 1st container though. The string that's returned is built out of
     * different properties of the container:
     * <ul>
     * <li>state.waiting.reason</li>
     * <li>state.terminated.reason</li>,
     * <li>state.terminated.signal</li>
     * <li>state.terminated.exitCode</li>
     * </ul>
     * 
     * @return the status of this pod
     */
    String getStatus();

    /**
     * Returns the ports that the containers of this pod are using.
     * 
     * @returns the ports of the containers for this pod.
     */
    Set<IPort> getContainerPorts();

    /**
     * Adds a container with the given name. This is useful if creating a pod
     * directly without a resource controller. Returns the container for the given name.
     * 
     * @param the name of the container
     * 
     * @return the container that was added.
     */
    IContainer addContainer(String name);

    /**
     * Returns all the containers for this pod.
     * 
     * @return the containers for this pod.
     */
    Collection<IContainer> getContainers();
}
