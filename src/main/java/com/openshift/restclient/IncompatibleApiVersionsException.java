/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.restclient;

import java.util.Collection;

import com.openshift.internal.restclient.KubernetesAPIVersion;

public class IncompatibleApiVersionsException extends OpenShiftException {

    private static final long serialVersionUID = 1L;

    public IncompatibleApiVersionsException(Collection<KubernetesAPIVersion> clientVersions,
            Collection<KubernetesAPIVersion> serverVersions) {
        super(String.format("The client %s and server %s do not have compatible API versions.", clientVersions,
                serverVersions));
    }

    public IncompatibleApiVersionsException(String clientVersions, String serverVersions) {
        super(String.format("The client %s and server %s do not have compatible API versions.", clientVersions,
                serverVersions));
    }
}
