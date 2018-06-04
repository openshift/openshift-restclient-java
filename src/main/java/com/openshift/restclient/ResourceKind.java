/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.restclient;

import java.util.Optional;

import com.openshift.restclient.model.IResource;
import org.apache.commons.lang.StringUtils;

/**
 * ResourceKind are the various types of Kubernetes resources that are of
 * interest
 *
 */
public interface ResourceKind {

    /**
     * @return the identifier for the resource kind
     */
    String getIdentifier();

    /**
     * @return the implementation class to be used for this resource kind
     */
    Optional<Class<? extends IResource>> getImplementationClass();

    static String pluralize(String kind) {
        return pluralize(kind, false, false);
    }

    static String pluralize(String kind, boolean lowercase, boolean uncapitalize) {
        if (StringUtils.isBlank(kind)) {
            return "";
        }
        if (kind.endsWith("y")) {
            kind = kind.substring(0, kind.length() - 1).concat("ies");
        } else if (!kind.endsWith("s")) {
            kind = kind.concat("s");
        }
        if (lowercase) {
            kind = kind.toLowerCase();
        }
        if (uncapitalize) {
            kind = StringUtils.uncapitalize(kind);
        }
        return kind;
    }

}
