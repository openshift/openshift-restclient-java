/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.capability.resources;

import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.resources.IDeploymentTraceability;
import com.openshift.restclient.model.IReplicationController;
import com.openshift.restclient.model.IResource;

/**
 * Determine which deployment caused a resource to
 * be deployed based on the information found in its
 * annotations
 * 
 * @author Jeff Cantrill
 */
public class DeploymentTraceability extends AssociationCapability implements IDeploymentTraceability {
	
	private static final String DEPLOYMENT_ANNOTATION = "deployment";

	public DeploymentTraceability(IResource resource, IClient client) {
		super(DeploymentTraceability.class.getSimpleName(), resource, client);
	}

	@Override
	public IReplicationController getDeployment() {
		return getAssociatedResource(ResourceKind.REPLICATION_CONTROLLER);
	}

	@Override
	protected String getAnnotationKey() {
		return DEPLOYMENT_ANNOTATION;
	}

}
