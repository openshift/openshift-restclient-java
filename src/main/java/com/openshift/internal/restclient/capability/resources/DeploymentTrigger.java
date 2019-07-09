/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package com.openshift.internal.restclient.capability.resources;

import java.util.Collections;
import java.util.Optional;

import com.openshift.internal.restclient.capability.AbstractCapability;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.api.ITypeFactory;
import com.openshift.restclient.capability.resources.IDeployCapability;
import com.openshift.restclient.capability.resources.IDeploymentTriggerable;
import com.openshift.restclient.http.IHttpConstants;
import com.openshift.restclient.model.IDeploymentConfig;
import com.openshift.restclient.model.deploy.IDeploymentRequest;

/**
 * 
 * @author Gabe Montero
 *
 */
public class DeploymentTrigger extends AbstractCapability implements IDeploymentTriggerable {
    private static final String DEPLOYMENT_ENDPOINT = "instantiate";
    private static final String DEPLOYMENT_REQUEST = "v1.DeploymentRequest";

    private IClient client;
    private IDeploymentConfig config;
    private ITypeFactory factory;
    private boolean latest;
    private boolean force;
    private String resourceName;

    public DeploymentTrigger(IDeploymentConfig resource, IClient client, ITypeFactory factory) {
        super(resource, client, DEPLOYMENT_ENDPOINT);
        this.client = client;
        this.config = resource;
        this.factory = factory;
    }

    @Override
    public String getName() {
        return DeploymentTrigger.class.getSimpleName();
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public IDeploymentConfig trigger() {
        if (super.isSupported()) {
            IDeploymentRequest request = (IDeploymentRequest) factory.stubKind(DEPLOYMENT_REQUEST,
                    Optional.of(config.getName()), Optional.empty());
            request.setForce(force);
            request.setLatest(latest);
            request.setName(resourceName);
            return (IDeploymentConfig) client.execute(client.getResourceFactory(), IHttpConstants.POST,
                    config.getKind(), config.getNamespaceName(), config.getName(), DEPLOYMENT_ENDPOINT, null, request,
                    Collections.emptyMap());
        } else {
            IDeployCapability deployer = config.getCapability(IDeployCapability.class);
            deployer.deploy();
            return client.get(ResourceKind.DEPLOYMENT_CONFIG, config.getName(), config.getNamespaceName());
        }
    }

    @Override
    public void setLatest(boolean latest) {
        this.latest = latest;
    }

    @Override
    public boolean isLatest() {
        return latest;
    }

    @Override
    public void setForce(boolean force) {
        this.force = force;
    }

    @Override
    public boolean isForce() {
        return force;
    }

    @Override
    public void setResourceName(String name) {
        this.resourceName = name;
    }

    @Override
    public String getResourceName() {
        return resourceName;
    }

}
