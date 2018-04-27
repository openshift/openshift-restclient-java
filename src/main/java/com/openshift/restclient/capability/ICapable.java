/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.restclient.capability;

/**
 * ICapable allows a source to be queried and identify its capabilities
 * 
 */
public interface ICapable {

    /**
     * Gets the capability of the desired type
     * 
     * @param capability
     * @return an implementation of the given capability
     */
    <T extends ICapability> T getCapability(Class<T> capability);

    /**
     * Determines if the client supports the desired capability
     * 
     * @return true if the client is able to offer this capability
     */
    boolean supports(Class<? extends ICapability> capability);

    /**
     * Uses the given visitor to access the desired capability if it is supported
     *
     * @param visitor
     *            A visitor looking for a given Capability type
     * @param <T>
     *            visitor A capability visitor
     * @param <R>
     *            unsupportedCapabililityValue The value to return when the
     *            capability is not supported
     * @return <R> A type that is expected by the caller
     */
    <T extends ICapability, R> R accept(CapabilityVisitor<T, R> visitor, R unsupportedCapabililityValue);
}
