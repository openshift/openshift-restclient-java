/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient;

/**
 * This list of supported OpenShift API Models by this client
 * 
 */
public enum OpenShiftAPIVersion implements APIModelVersion {
    @Deprecated
    v1beta3(2), v1(3);

    private int order;

    OpenShiftAPIVersion(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }
}
