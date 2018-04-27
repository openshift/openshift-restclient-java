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

package com.openshift.restclient.model;

/**
 * Environment variable representation to allow more complex values then
 * name/value pairs. An environmentVariable will have either a value or
 * valueFrom but not both.
 * 
 *
 */
public interface IEnvironmentVariable extends JSONSerializeable {

    /**
     * The name of the env var
     * 
     */
    String getName();

    /**
     * The value of the environment variable or null if not defined.
     * 
     */
    String getValue();

    /**
     * The ref value or null if not defined
     * 
     */
    IEnvVarSource getValueFrom();

    /**
     * Marker interface for sources of environment variables
     * 
     *
     */
    static interface IEnvVarSource {

    }

    @Override
    String toJson();

}
