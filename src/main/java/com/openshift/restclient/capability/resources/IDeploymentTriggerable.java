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

package com.openshift.restclient.capability.resources;

import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.model.IDeploymentConfig;

/**
 * Capability to trigger a deployment via the instantiate endpoint
 * @author gmontero
 */
public interface IDeploymentTriggerable extends ICapability {
    
    /**
     * Trigger a deployment based on a deployment config
     * @return The updated deployment config after the deployment was triggered
     */
    IDeploymentConfig trigger();
    
    /**
     * If set the true latest will update the deployment config with the latest state from all triggers.
     */
    void setLatest(boolean latest);
    
    /**
     * Returns the current setting of the latest flag.
     */
    boolean isLatest();
    
    /**
     * If set to try force will try to force a new deployment to run. If the deployment config is paused, 
     * then setting this to true will return an Invalid error.
     */
    void setForce(boolean force);
    
    /**
     * Returns the latest setting of the force flag.
     */
    boolean isForce();
    
    /**
     * The name of the deployment config; note, the name in the corresponding oapi type
     * is not in the k8s metadata object
     * @param name of the deployment config
     */
    void setResourceName(String name);
    
    /**
     * Returns the name of the deployment config seeded into the deployment request
     */
    String getResourceName();

}
