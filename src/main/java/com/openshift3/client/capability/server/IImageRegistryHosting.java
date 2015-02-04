/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.client.capability.server;

import com.openshift3.client.capability.ICapability;

/**
 * Identifies an OpenShift server as capable of
 * hosting images via its own registry
 */
public interface IImageRegistryHosting extends ICapability{
	
	/**
	 * Get the Image Registry URI
	 * @return the registry URI (e.g. 172.121.17.212:5001)
	 */
	String getRegistryUri();
}
