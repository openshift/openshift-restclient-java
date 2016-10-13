package com.openshift.internal.restclient.capability.resources;

import java.util.logging.Logger;

import com.openshift.internal.restclient.capability.AbstractCapability;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.resources.IDeployCapability;
import com.openshift.restclient.capability.resources.IDeploymentTriggerable;
import com.openshift.restclient.model.IDeploymentConfig;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.deploy.IDeploymentRequest;

public class DeploymentTrigger extends AbstractCapability implements IDeploymentTriggerable {
    private static final String DEPLOYMENT_ENDPOINT = "instantiate";
    
    private IClient client;
    private IDeploymentConfig config;
    private boolean latest;
    private boolean force;
    private String resourceName;

    public DeploymentTrigger(IDeploymentConfig resource, IClient client) {
        super(resource, client, DEPLOYMENT_ENDPOINT);
        this.client = client;
        this.config = resource;
    }

    @Override
    public String getName() {
        return DeploymentTrigger.class.getSimpleName();
    }

    @Override
    public IDeploymentConfig trigger() {
        if (isSupported()) {
            IDeploymentRequest request = client.getResourceFactory().stub(ResourceKind.DEPLOYMENT_REQUEST, config.getName());
            request.setForce(force);
            request.setLatest(latest);
            request.setName(resourceName);
            return client.create(config.getKind(), config.getNamespace(), config.getName(), DEPLOYMENT_ENDPOINT, request);
        } else {
            IDeployCapability deployer = config.getCapability(IDeployCapability.class);
            deployer.deploy();
            return client.get(ResourceKind.DEPLOYMENT_CONFIG, config.getName(), config.getNamespace());
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
