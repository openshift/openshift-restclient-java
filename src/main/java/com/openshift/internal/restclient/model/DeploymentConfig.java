/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.internal.restclient.model;

import static com.openshift.internal.restclient.capability.CapabilityInitializer.initializeCapabilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

import com.openshift.internal.restclient.model.deploy.ConfigChangeTrigger;
import com.openshift.internal.restclient.model.deploy.DeploymentTrigger;
import com.openshift.internal.restclient.model.deploy.ImageChangeTrigger;
import com.openshift.restclient.IClient;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IDeploymentConfig;
import com.openshift.restclient.model.deploy.DeploymentTriggerType;
import com.openshift.restclient.model.deploy.IDeploymentTrigger;

public class DeploymentConfig extends ReplicationController implements IDeploymentConfig {

    public static final String DEPLOYMENTCONFIG_CONTAINERS = "spec.template.spec.containers";
    private static final String DEPLOYMENTCONFIG_TRIGGERS = "spec.triggers";
    private static final String DEPLOYMENTCONFIG_STRATEGY = "spec.strategy.type";
    private static final String DEPLOYMENTCONFIG_LATEST_VERSION = "status.latestVersion";
    private static final String DEPLOYMENTCONFIG_CAUSES = "status.details.causes";

    private static final String TYPE = "type";
    private static final String IMAGE_CHANGE = "ImageChange";
    private static final String IMAGE_TRIGGER = "imageTrigger";
    private static final String FROM = "from";
    private static final String NAME = "name";
    private static final String IMAGE_CHANGE_PARAMS = "imageChangeParams";
    private static final String LAST_TRIGGER_IMAGE = "lastTriggeredImage";
    private static final String IMAGE_TRIGGER_FROM_NAME = IMAGE_TRIGGER + "." + FROM + "." + NAME;
    private static final String IMAGE_CHANGE_PARAMS_FROM_NAME = IMAGE_CHANGE_PARAMS + "." + FROM + "." + NAME;
    private static final String IMAGE_CHANGE_PARAMS_LAST_TRIGGER_IMAGE = IMAGE_CHANGE_PARAMS + "." + LAST_TRIGGER_IMAGE;

    private final Map<String, String[]> propertyKeys;

    public DeploymentConfig(ModelNode node, IClient client, Map<String, String[]> propertyKeys) {
        super(node, client, propertyKeys);
        this.propertyKeys = propertyKeys;
        initializeCapabilities(getModifiableCapabilities(), this, getClient());
    }

    @Override
    public Collection<String> getTriggerTypes() {
        List<String> types = new ArrayList<>();
        ModelNode triggers = get(DEPLOYMENTCONFIG_TRIGGERS);
        for (ModelNode node : triggers.asList()) {
            types.add(asString(node, TYPE));
        }
        return types;
    }

    @Override
    public Collection<IDeploymentTrigger> getTriggers() {
        List<IDeploymentTrigger> triggers = new ArrayList<>();
        ModelNode list = get(DEPLOYMENTCONFIG_TRIGGERS);
        for (ModelNode node : list.asList()) {
            String type = asString(node, TYPE);
            switch (type) {
            case (DeploymentTriggerType.CONFIG_CHANGE):
                triggers.add(new ConfigChangeTrigger(node, propertyKeys));
                break;
            case (DeploymentTriggerType.IMAGE_CHANGE):
                triggers.add(new ImageChangeTrigger(node, propertyKeys));
                break;
            }
        }
        return triggers;
    }

    // FIXME
    public List<String> getImageNames() {
        List<String> names = new ArrayList<>();
        List<ModelNode> containers = get(DEPLOYMENTCONFIG_CONTAINERS).asList();
        for (ModelNode container : containers) {
            names.add(container.get("image").asString());
        }
        return names;
    }

    @Override
    public int getLatestVersionNumber() {
        return asInt(DEPLOYMENTCONFIG_LATEST_VERSION);
    }

    @Override
    public void setLatestVersionNumber(int newVersionNumber) {
        set(DEPLOYMENTCONFIG_LATEST_VERSION, newVersionNumber);
    }

    @Override
    public IDeploymentTrigger addTrigger(String type) {
        ModelNode triggers = get(DEPLOYMENTCONFIG_TRIGGERS);
        ModelNode triggerNode = triggers.add();
        triggerNode.get(TYPE).set(type);
        switch (type) {
        case DeploymentTriggerType.IMAGE_CHANGE:
            return new ImageChangeTrigger(triggerNode, propertyKeys);
        case DeploymentTriggerType.CONFIG_CHANGE:
        default:
        }
        return new DeploymentTrigger(triggerNode, propertyKeys);
    }

    @Override
    public String getDeploymentStrategyType() {
        return asString(DEPLOYMENTCONFIG_STRATEGY);
    }

    @Override
    public boolean haveTriggersFired() {
        ModelNode causes = get(DEPLOYMENTCONFIG_CAUSES);
        if (causes.getType() == ModelType.UNDEFINED || causes.getType() != ModelType.LIST) {
            return false;
        }
        return !causes.asList().isEmpty();
    }

    @Override
    public boolean didImageTrigger(String imageNameTag) {
        if (!haveTriggersFired() || imageNameTag == null) {
            return false;
        }
        ModelNode causes = get(DEPLOYMENTCONFIG_CAUSES);
        if (causes.getType() == ModelType.UNDEFINED || causes.getType() != ModelType.LIST) {
            return false;
        }
        for (ModelNode cause : causes.asList()) {
            String type = asString(cause, TYPE);
            if (type.equalsIgnoreCase(IMAGE_CHANGE)) {
                String triggerName = asString(cause, IMAGE_TRIGGER_FROM_NAME);
                DockerImageURI uri = new DockerImageURI(triggerName);
                if (imageNameTag.equals(uri.getNameAndTag())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getImageHexIDForImageNameAndTag(String imageNameTag) {
        ModelNode triggers = get(DEPLOYMENTCONFIG_TRIGGERS);
        if (triggers.getType() == ModelType.UNDEFINED || triggers.getType() != ModelType.LIST || imageNameTag == null) {
            return null;
        }
        for (ModelNode trigger : triggers.asList()) {
            if (asString(trigger, TYPE).equalsIgnoreCase(IMAGE_CHANGE)) {
                String nameTag = null;
                nameTag = asString(trigger, IMAGE_CHANGE_PARAMS_FROM_NAME);
                if (imageNameTag.equals(nameTag)) {
                    return asString(trigger, IMAGE_CHANGE_PARAMS_LAST_TRIGGER_IMAGE);
                }
            }
        }
        return null;
    }

    @Override
    public String getImageNameAndTagForTriggeredDeployment() {
        ModelNode causes = get(DEPLOYMENTCONFIG_CAUSES);
        if (causes.getType() == ModelType.UNDEFINED || causes.getType() != ModelType.LIST) {
            return null;
        }
        for (ModelNode cause : causes.asList()) {
            String type = asString(cause, TYPE);
            if (type.equalsIgnoreCase(IMAGE_CHANGE)) {
                String imageTag = asString(cause, IMAGE_TRIGGER_FROM_NAME);
                DockerImageURI uri = new DockerImageURI(imageTag);
                return uri.getNameAndTag();
            }
        }
        return null;
    }

}
