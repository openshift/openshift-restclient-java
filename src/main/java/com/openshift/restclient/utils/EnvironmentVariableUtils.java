/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.restclient.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import com.openshift.restclient.model.IEnvironmentVariable;

/**
 * @author Andre Dietisheim
 */
public class EnvironmentVariableUtils {

    private EnvironmentVariableUtils() {
    }

    public static Map<String, String> toMapOfStrings(Collection<IEnvironmentVariable> envVars) {
        if (envVars == null) {
            return null;
        }
        if (envVars.isEmpty()) {
            return Collections.emptyMap();
        }

        return envVars.stream().collect(Collectors.toMap(envVar -> envVar.getName(), envVar -> envVar.getValue()));
    }
}
