/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client.capability.resources;

import com.openshift3.client.IClient;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.capability.resources.IDeploymentConfigTraceability;
import com.openshift3.client.model.IDeploymentConfig;
import com.openshift3.client.model.IResource;

/**
 * Capability for a resource to determine
 * to which deploymentconfig it is associated
 */
public class AnnotationDeploymentConfigTraceability extends AnnotationCapability implements IDeploymentConfigTraceability {

	public AnnotationDeploymentConfigTraceability(IResource resource, IClient client) {
		super(AnnotationDeploymentConfigTraceability.class.getSimpleName(), resource, client);
	}

	@Override
	public IDeploymentConfig getDeploymentConfig() {
		return getAssociatedResource(ResourceKind.DeploymentConfig);
	}

	@Override
	protected String getAnnotationKey() {
		return "deploymentconfig";
	}

}
