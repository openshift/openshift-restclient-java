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

package com.openshift.restclient.model.build;

import java.util.List;

import com.openshift.restclient.model.IResource;

/**
 * Resource payload for triggering a build
 *
 */
public interface IBuildRequest extends IResource {
    /**
     * Set the commit level for the git clone extraction of the source code the
     * build operates against
     * 
     * @param commitId
     *            the specific hexadecimal commit ID associated with a git log level
     */
    void setCommitId(String commitId);

    /**
     * Get the commit level for the git clone extraction of the source code the
     * build operates against
     * 
     * @return the specific hexadecimal commit ID associated with a git log level
     */
    String getCommitId();

    /**
     * Add a human readable short explanation of why this build request was issued
     * 
     * @param cause
     *            the description to add to the list of causes for this request
     */
    void addBuildCause(String cause);

    /**
     * Get the list of human readable short explanations of why this build request
     * was issued
     * 
     * @return list of reasons for the build
     */
    List<String> getBuildCauses();

    /**
     * Sets an environment variable in this build request
     * 
     * @param name
     *            The name of the environment variable to set
     * @param value
     *            The value of the variable
     */
    void setEnvironmentVariable(String name, String value);

}
