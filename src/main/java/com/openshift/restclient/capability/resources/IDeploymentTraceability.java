/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.restclient.capability.resources;

import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.model.IReplicationController;

/**
 * Trace the source of what caused a resource to be deployed
 * 
 */
public interface IDeploymentTraceability extends ICapability {

    /**
     * Get the deployment of a resource. The value returned when the capability is
     * not supported is not guaranteed.
     * 
     * @return IReplicationController if the capability is supported.
     */
    IReplicationController getDeployment();
}
