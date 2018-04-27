/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.model.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.capability.resources.ITags;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.template.IParameter;
import com.openshift.restclient.model.template.ITemplate;

public class Template extends KubernetesResource implements ITemplate {

    private static final String TEMPLATE_PARAMETERS = "parameters";
    private static final String TEMPLATE_OBJECT_LABELS = "labels";

    public Template(ModelNode node, IClient client, Map<String, String[]> overrideProperties) {
        super(node, client, overrideProperties);
    }

    @Override
    public Map<String, String> getObjectLabels() {
        return Collections.unmodifiableMap(asMap(TEMPLATE_OBJECT_LABELS));
    }

    @Override
    public void addObjectLabel(String key, String value) {
        ModelNode labels = getNode().get(getPath(TEMPLATE_OBJECT_LABELS));
        labels.get(key).set(value);
    }

    @Override
    public Map<String, IParameter> getParameters() {
        Map<String, IParameter> params = new HashMap<String, IParameter>();
        ModelNode modelNode = get(TEMPLATE_PARAMETERS);
        if (modelNode.isDefined()) {
            Collection<ModelNode> nodes = modelNode.asList();
            for (ModelNode node : nodes) {
                Parameter p = new Parameter(node);
                params.put(p.getName(), p);
            }
        }
        return params;
    }

    @Override
    public Collection<IResource> getObjects() {
        if (!getNode().has(OBJECTS)) {
            return Collections.emptyList();
        }
        Collection<ModelNode> nodes = get(OBJECTS).asList();
        List<IResource> resources = new ArrayList<IResource>(nodes.size());
        IResourceFactory factory = getClient().getResourceFactory();
        if (factory != null) {
            for (ModelNode node : nodes) {
                resources.add(factory.create(node.toJSONString(true)));
            }
        }
        return resources;
    }

    @Override
    public void updateParameterValues(Collection<IParameter> parameters) {
        Map<String, IParameter> actuals = getParameters();
        for (IParameter param : parameters) {
            if (actuals.containsKey(param.getName())) {
                actuals.get(param.getName()).setValue(param.getValue());
            }
        }
    }

    @Override
    public void updateParameter(String key, String value) {
        Map<String, IParameter> params = getParameters();
        if (params.containsKey(key)) {
            params.get(key).setValue(value);
        }
    }

    /**
     * Returns <code>true</code> if this template contains the given text in name or
     * tags.
     * 
     */
    @Override
    public boolean isMatching(final String text) {
        if (StringUtils.isBlank(text)) {
            return true;
        }
        if (getName().contains(text)) {
            return true;
        }

        return accept(new CapabilityVisitor<ITags, Boolean>() {
            @Override
            public Boolean visit(ITags capability) {
                for (String tag : capability.getTags()) {
                    if (tag.contains(text)) {
                        return true;
                    }
                }
                return false;
            }
        }, false);
    }

}
