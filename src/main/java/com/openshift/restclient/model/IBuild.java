/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.restclient.model;

import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.build.IBuildSource;
import com.openshift.restclient.model.build.IBuildStatus;
import com.openshift.restclient.model.build.IBuildStrategy;

public interface IBuild extends IResource {

    /**
     * Returns the status of the buld
     * 
     */
    String getStatus();

    /**
     * Returns the details about the status of this build
     * 
     */
    String getMessage();

    /**
     * Returns the name of the pod running the build
     * 
     */
    @Deprecated
    String getPodName();

    /**
     * Cancels a build if its status is not "Complete", "Failed", or "Cancelled"
     * 
     * @return if the build state was in fact changed
     */
    boolean cancel();

    DockerImageURI getOutputTo();

    String getOutputKind();

    <T extends IBuildSource> T getBuildSource();

    <T extends IBuildStrategy> T getBuildStrategy();

    String getPushSecret();

    IBuildStatus getBuildStatus();
}
