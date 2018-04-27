/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.restclient.capability;

public interface ICapability {

    /**
     * Allow the implementation of the capability to determine if it is supported on
     * the OpenShift server. Implementations should return false if they can not
     * 
     * @return true if the capability exists
     */
    boolean isSupported();

    /**
     * Well known name of the capability
     * 
     */
    String getName();
}
