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

import com.openshift.restclient.model.deploy.IDeploymentTrigger;

public interface IDeploymentConfig extends IReplicationController {

    /**
     * Get the list of deployment triggers
     * 
     * @return a collection of trigger types
     */
    Collection<String> getTriggerTypes();

    /**
     * Returns the trigger of the given type or null if it doesn not exist
     * 
     */
    Collection<IDeploymentTrigger> getTriggers();

    /**
     * Convenience method to get the deployment strategy type
     * 
     * @return the type as a string
     */
    String getDeploymentStrategyType();

    /**
     * Add a trigger of the given type or null if the type is unrecognized
     * 
     */
    IDeploymentTrigger addTrigger(String type);

    /**
     * Get the latest version number
     * 
     */
    int getLatestVersionNumber();

    /**
     * Set the latest version number
     * 
     * @param new
     *            version number
     * 
     */
    void setLatestVersionNumber(int newVersionNumber);

    /**
     * Return whether deployments have fired because of triggers
     * 
     */
    boolean haveTriggersFired();

    /**
     * Return whether deployments have fired based on an image trigger for a
     * particular image
     * 
     * @param imageNameTag
     *            the image name:tag associated with an image trigger
     */
    boolean didImageTrigger(String imageNameTag);

    /**
     * Get the image hexadecimal ID for the image tag used with the latest image
     * change trigger
     * 
     * @param imageNameTag
     *            the image name:tag associated with an image trigger
     */
    String getImageHexIDForImageNameAndTag(String imageNameTag);

    /**
     * Get the image name:tag from a image change trigger firing
     * 
     */
    String getImageNameAndTagForTriggeredDeployment();
}
