/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.client.capability;

/**
 * ICapable allows a source to be queried and identify its
 * capabilities
 */
public interface ICapable {

	/**
	 * Get the capability of the desired type
	 * 
	 * @param capability
	 * @return an implementation of the given capability
	 */
	<T extends ICapability> T getCapability(Class<T> capability);
	
	/**
	 * Determine if the client supports the desired capability
	 *  
	 * @param capability
	 * @return true if the client is able to offer this capability
	 */
	boolean supports(Class<? extends ICapability> capability);
	
	/**
	 * Use the given visitor to access the desired capability if it
	 * is supported
	 *
	 * @param visitor  A visitor looking for a given Capability type
	 */
	<T extends ICapability> void accept(CapabilityVisitor<T> visitor);
}
