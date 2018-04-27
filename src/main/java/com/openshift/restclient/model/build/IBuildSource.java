/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.restclient.model.build;

public interface IBuildSource {

    /**
     * The BuildSourceType
     * 
     * @return {@link BuildSourceType}
     */
    String getType();

    /**
     * The URI to the source repo
     * 
     */
    String getURI();

    /**
     * The sub-directory relative to the repo root where the source code for the
     * application exists. This allows to have buildable sources in directory other
     * than root of repository.
     * 
     */
    String getContextDir();
}
