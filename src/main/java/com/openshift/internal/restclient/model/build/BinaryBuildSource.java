/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.model.build;

import com.openshift.restclient.model.build.BuildSourceType;
import com.openshift.restclient.model.build.IBinaryBuildSource;

public class BinaryBuildSource implements IBinaryBuildSource {

    private String asFile;
    private String contextDir;

    public BinaryBuildSource(String asFile, String contextDir) {
        this.asFile = asFile;
        this.contextDir = contextDir;
    }

    @Override
    public String getType() {
        return BuildSourceType.BINARY;
    }

    @Override
    public String getAsFile() {
        return asFile;
    }

    public void setAsFile(String asFile) {
        this.asFile = asFile;
    }

    @Override
    public String getContextDir() {
        return contextDir;
    }

    public void setContextDir(String contextDir) {
        this.contextDir = contextDir;
    }

}
