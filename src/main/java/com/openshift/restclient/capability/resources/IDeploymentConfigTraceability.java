/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.capability.resources;

import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.model.IDeploymentConfig;

public interface IDeploymentConfigTraceability extends ICapability {
	
	/**
	 * Get the deployment configuration for a resource
	 * @return
	 */
	IDeploymentConfig getDeploymentConfig();

}
