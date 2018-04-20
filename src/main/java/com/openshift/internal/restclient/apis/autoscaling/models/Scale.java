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

package com.openshift.internal.restclient.apis.autoscaling.models;

import static com.openshift.internal.util.JBossDmrExtentions.asInt;
import static com.openshift.internal.util.JBossDmrExtentions.asMap;
import static com.openshift.internal.util.JBossDmrExtentions.asString;
import static com.openshift.internal.util.JBossDmrExtentions.get;
import static com.openshift.internal.util.JBossDmrExtentions.getPath;
import static com.openshift.internal.util.JBossDmrExtentions.set;

import java.util.Collections;
import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.api.models.TypeMeta;
import com.openshift.restclient.apis.autoscaling.models.IScale;

public class Scale extends TypeMeta implements IScale {

    private static final String SPEC_REPLICAS = "spec.replicas";

    public Scale() {
        super(new ModelNode(), Collections.emptyMap());
    }

    /**
     * 
     * @param propertyKeys
     *            overrides based on version
     */
    public Scale(ModelNode node, Map<String, String[]> propertyKeys) {
        super(node, propertyKeys);
    }

    @Override
    public int getSpecReplicas() {
        return asInt(getNode(), getPropertyKeys(), SPEC_REPLICAS);
    }

    @Override
    public void setSpecReplicas(int replicas) {
        set(getNode(), getPropertyKeys(), SPEC_REPLICAS, replicas);
    }

    @Override
    public String getName() {
        return asString(getNode(), getPropertyKeys(), METADATA_NAME);
    }

    @Override
    public void setName(String name) {
        set(getNode(), getPropertyKeys(), METADATA_NAME, name);
    }

    @Override
    public String getCreationTimeStamp() {
        return asString(getNode(), getPropertyKeys(), CREATION_TIMESTAMP);
    }

    @Override
    public String getNamespace() {
        return asString(getNode(), getPropertyKeys(), METADATA_NAMESPACE);
    }

    @Override
    public void setNamespace(String namespace) {
        set(getNode(), getPropertyKeys(), METADATA_NAMESPACE, namespace);
    }

    @Override
    public String getResourceVersion() {
        return asString(getNode(), getPropertyKeys(), METADATA_RESOURCE_VERSION);
    }

    @Override
    public Map<String, String> getLabels() {
        return asMap(getNode(), getPropertyKeys(), LABELS);
    }

    @Override
    public void addLabel(String key, String value) {
        ModelNode labels = getNode().get(getPath(LABELS));
        labels.get(key).set(value);
    }

    @Override
    public Map<String, String> getAnnotations() {
        return asMap(getNode(), getPropertyKeys(), ANNOTATIONS);
    }

    @Override
    public String getAnnotation(String key) {
        return getAnnotations().get(key);
    }

    @Override
    public void setAnnotation(String name, String value) {
        if (value == null) {
            return;
        }
        ModelNode annotations = get(getNode(), getPropertyKeys(), ANNOTATIONS);
        annotations.get(name).set(value);
    }

    @Override
    public boolean isAnnotatedWith(String key) {
        Map<String, String> annotations = getAnnotations();
        return annotations.containsKey(key);
    }

}
