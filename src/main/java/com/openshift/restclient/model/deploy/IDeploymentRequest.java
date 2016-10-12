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
package com.openshift.restclient.model.deploy;

import com.openshift.restclient.api.models.INameSetable;
import com.openshift.restclient.api.models.ITypeMeta;

/**
 * Resource payload for triggering a deployment via the instantiate endpoint
 * @author gmontero
 *
 */
public interface IDeploymentRequest extends ITypeMeta, INameSetable {
    
    /**
     * If set the true latest will update the deployment config with the latest state from all triggers.
     * @param latest
     */
    void setLatest(boolean latest);
    
    /**
     * Returns the current setting of the latest flag.
     * @return
     */
    boolean isLatest();
    
    /**
     * If set to try force will try to force a new deployment to run. If the deployment config is paused, 
     * then setting this to true will return an Invalid error.
     * @param force
     */
    void setForce(boolean force);
    
    /**
     * Returns the latest setting of the force flag.
     * @return
     */
    boolean isForce();
    
    /**
     * The name of the deployment config; note, the name in the corresponding oapi type
     * is not in the k8s metadata object
     * @param name of the deployment config
     */
    void setName(String name);
    
    /**
     * Returns the name of the deployment config seeded into the deployment request
     * @return
     */
    String getName();

}
