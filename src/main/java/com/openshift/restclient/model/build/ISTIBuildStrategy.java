/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.restclient.model.build;

import java.util.Map;

import com.openshift.restclient.images.DockerImageURI;

@Deprecated
public interface ISTIBuildStrategy extends IBuildStrategy {

    /**
     * Returns the Builder Image used to execute the build
     */
    DockerImageURI getImage();

    String getScriptsLocation();

    Map<String, String> getEnvironmentVariables();

    boolean incremental();

    @Deprecated
    boolean forceClean();

}
