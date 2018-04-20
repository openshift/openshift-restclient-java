/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.restclient.model.build;

public interface BuildTriggerType {
    @Deprecated
    static final String generic = "generic";

    @Deprecated
    static final String github = "github";

    @Deprecated
    static final String imageChange = "imageChange";

    static final String CONFIG_CHANGE = "ConfigChange";
    static final String GENERIC = "Generic";
    static final String GITHUB = "GitHub";
    static final String IMAGE_CHANGE = "ImageChange";

}
