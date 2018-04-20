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

/**
 * Capability to trigger a new deployment of a deploymentconfig (e.g. oc deploy
 * foo)
 *
 */
public interface IDeployCapability extends ICapability {

    /**
     * Deploy the latest deployment
     * 
     * @throws an
     *             OpenShiftException if there is an error (e.g. 404)
     */
    void deploy();

}
