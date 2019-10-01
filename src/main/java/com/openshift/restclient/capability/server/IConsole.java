/*******************************************************************************
 * Copyright (c) 2019 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.restclient.capability.server;

import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.model.IResource;

/**
 * Identifies an OpenShift server as capable of hosting a (web) console
 */
public interface IConsole extends ICapability {

    /**
     * Returns the console url
     * 
     * @return the console url (e.g. https://console-openshift-console.apps.com)
     */
    String getConsoleUrl();

    /**
     * Returns the url in the console for the given resource
     * 
     * @return the console url (e.g. https://console-openshift-console.apps.com)
     */
    <R extends IResource> String getConsoleUrl(R resource);
}
