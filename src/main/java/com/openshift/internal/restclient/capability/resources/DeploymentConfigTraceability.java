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
import com.openshift.restclient.capability.resources.IDeploymentConfigTraceability;
import com.openshift.restclient.model.IDeploymentConfig;
import com.openshift.restclient.model.IResource;

/**
 * Capability for a resource to determine to which deploymentconfig it is
 * associated
 */
public class DeploymentConfigTraceability extends AssociationCapability implements IDeploymentConfigTraceability {

    public DeploymentConfigTraceability(IResource resource, IClient client) {
        super(DeploymentConfigTraceability.class.getSimpleName(), resource, client);
    }

    @Override
    public IDeploymentConfig getDeploymentConfig() {
        return getAssociatedResource(ResourceKind.DEPLOYMENT_CONFIG);
    }

    @Override
    protected String getAnnotationKey() {
        return "deploymentconfig";
    }

}
