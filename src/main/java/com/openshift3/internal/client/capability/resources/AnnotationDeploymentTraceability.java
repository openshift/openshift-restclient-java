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
import com.openshift3.client.capability.resources.IDeploymentTraceability;
import com.openshift3.client.model.IReplicationController;
import com.openshift3.client.model.IResource;

/**
 * Determine which deployment caused a resource to
 * be deployed based on the information found in its
 * annotations
 */
public class AnnotationDeploymentTraceability extends AnnotationCapability implements IDeploymentTraceability {
	
	private static final String DEPLOYMENT_ANNOTATION = "deployment";

	public AnnotationDeploymentTraceability(IResource resource, IClient client) {
		super(AnnotationDeploymentTraceability.class.getSimpleName(), resource, client);
	}

	@Override
	public IReplicationController getDeployment() {
		return getAssociatedResource(ResourceKind.ReplicationController);
	}

	@Override
	protected String getAnnotationKey() {
		return DEPLOYMENT_ANNOTATION;
	}

}
