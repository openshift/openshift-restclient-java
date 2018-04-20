/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.restclient.model;

import java.util.List;
import java.util.Map;

/**
 * Kubernetes Service to access a Pod
 * 
 */
public interface IService extends IResource {

    /**
     * Sets the container port exposed by the image
     * 
     */
    void setTargetPort(int port);

    /**
     * Sets the exposed port that is mapped to a running image
     * 
     */
    void setPort(int port);

    /**
     * Returns the first exposed port that is mapped to a running image
     * 
     */
    int getPort();

    IServicePort addPort(int port, int targetPort);

    IServicePort addPort(int port, int targetPort, String name);

    /**
     * Sets the container name that the service routes traffic to.
     * 
     */
    void setSelector(Map<String, String> selector);

    /**
     * Convenience method for setting a selector that has a singular key/value pair.
     * 
     */
    void setSelector(String key, String value);

    /**
     * Returns the selector used to find the Pod to which this service is routing
     * 
     */
    Map<String, String> getSelector();

    /**
     * The port this service targets on the pod
     * 
     */
    String getTargetPort();

    /**
     * Returns the IP of the service.
     * 
     */
    @Deprecated
    String getPortalIP();

    /**
     * Returns the IP of the service.
     * 
     */
    String getClusterIP();

    /**
     * Retrieves the pods for this service
     * 
     */
    List<IPod> getPods();

    /**
     * Get the collection of ports for the service
     * 
     */
    List<IServicePort> getPorts();

    /**
     * Set the collection of ports for the service
     */
    void setPorts(List<IServicePort> ports);

    /**
     * Returns the type of the service.
     * 
     */
    String getType();

    /**
     * Sets the type of the service.
     */
    void setType(String type);
}
