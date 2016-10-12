/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.model.deploy;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.internal.restclient.model.ModelNodeAdapter;
import com.openshift.internal.restclient.model.properties.ResourcePropertyKeys;
import com.openshift.internal.util.JBossDmrExtentions;
import com.openshift.restclient.IClient;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.deploy.IDeploymentRequest;

public class DeploymentRequest extends ModelNodeAdapter implements IDeploymentRequest, ResourcePropertyKeys {
    
    private static final String LATEST = "latest";
    private static final String FORCE = "force";
    
    public DeploymentRequest(ModelNode node, Map<String, String[]> overrideProperties) {
        super(node, overrideProperties);
    }

    @Override
    public void setLatest(boolean latest) {
        JBossDmrExtentions.set(getNode(), getPropertyKeys(), LATEST, latest);
    }

    @Override
    public boolean isLatest() {
        return getNode().get(JBossDmrExtentions.getPath(getPropertyKeys(), LATEST)).asBoolean();
    }

    @Override
    public void setForce(boolean force) {
        JBossDmrExtentions.set(getNode(), getPropertyKeys(), FORCE, force);
    }

    @Override
    public boolean isForce() {
        return getNode().get(JBossDmrExtentions.getPath(getPropertyKeys(), FORCE)).asBoolean();
    }

    @Override
    public String getName() {
        return getNode().get(JBossDmrExtentions.getPath(getPropertyKeys(), NAME)).asString();
    }

    @Override
    public void setName(String name) {
        // the DeploymentRequest has the name at the top level vs. the k8s metadata struct
        JBossDmrExtentions.set(getNode(), getPropertyKeys(), NAME, name);
    }

    @Override
    public Map<String, String> getMetadata() {
        return JBossDmrExtentions.asMap(getNode(), getPropertyKeys(), METADATA);
    }

    @Override
    public Set<Class<? extends ICapability>> getCapabilities() {
        return new HashSet<Class<? extends ICapability>>();
    }

    @Override
    public String getCreationTimeStamp() {
        return JBossDmrExtentions.asString(getNode(), getPropertyKeys(), CREATION_TIMESTAMP);
    }

    @Override
    public String getNamespace() {
        return JBossDmrExtentions.asString(getNode(), getPropertyKeys(), METADATA_NAMESPACE);
    }

    @Override
    public IProject getProject() {
        return null;
    }

    @Override
    public Map<String, String> getLabels() {
        return JBossDmrExtentions.asMap(getNode(), getPropertyKeys(), LABELS);
    }

    @Override
    public void addLabel(String key, String value) {
        getNode().get(JBossDmrExtentions.getPath(getPropertyKeys(), LABELS)).get(key).set(value);
        
    }

    @Override
    public boolean isAnnotatedWith(String key) {
        return getAnnotations().containsKey(key);
    }

    @Override
    public String getAnnotation(String key) {
        return getAnnotations().get(key);
    }

    @Override
    public void setAnnotation(String key, String value) {
        getNode().get(ANNOTATIONS).get(key).set(value);
    }

    @Override
    public void removeAnnotation(String key) {
        getNode().get(ANNOTATIONS).remove(key);
    }

    @Override
    public Map<String, String> getAnnotations() {
        return JBossDmrExtentions.asMap(getNode(), getPropertyKeys(), ANNOTATIONS);
    }

    @Override
    public String getResourceVersion() {
        return JBossDmrExtentions.asString(getNode(), getPropertyKeys(), METADATA_RESOURCE_VERSION);
    }

    @Override
    public <T extends ICapability> T getCapability(Class<T> capability) {
        return null;
    }

    @Override
    public boolean supports(Class<? extends ICapability> capability) {
        return false;
    }

    @Override
    public <T extends ICapability, R> R accept(CapabilityVisitor<T, R> visitor,
            R unsupportedCapabililityValue) {
        return null;
    }

    @Override
    public String toJson() {
        return JBossDmrExtentions.toJsonString(getNode(), false);
    }

    @Override
    public String getApiVersion() {
        return JBossDmrExtentions.asString(getNode(), getPropertyKeys(), APIVERSION);
    }

    @Override
    public String getKind() {
        return JBossDmrExtentions.asString(getNode(), getPropertyKeys(), KIND);
    }

}
