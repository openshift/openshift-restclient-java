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

package com.openshift.internal.restclient.capability.resources;

import static com.openshift.internal.util.JBossDmrExtentions.get;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.restclient.capability.resources.IPropertyAccessCapability;
import com.openshift.restclient.model.IResource;

public class PropertyAccessCapability implements IPropertyAccessCapability {

    private KubernetesResource resource;

    public PropertyAccessCapability(IResource resource) {
        if (resource instanceof KubernetesResource) {
            this.resource = (KubernetesResource) resource;
        }
    }

    @Override
    public String asString(String path) {
        ModelNode node = get(resource.getNode(), null, path);
        if (!node.isDefined()) {
            throw new UnresolvablePathException();
        }
        return node.asString();
    }

    @Override
    public Map<String, Object> asMap(String path) {
        return asMap(get(resource.getNode(), null, path));
    }

    private Map<String, Object> asMap(ModelNode node) {
        if (!node.isDefined()) {
            throw new UnresolvablePathException();
        }
        Map<String, Object> result = new HashMap<>();
        for (String key : node.keys()) {
            ModelNode value = node.get(key);
            switch (value.getType()) {
            case OBJECT:
                result.put(key, asMap(value));
                break;
            case LIST:
                result.put(key, asList(value));
                break;
            case STRING:
                result.put(key, value.asString());
                break;
            case INT:
                result.put(key, value.asInt());
                break;
            case BIG_INTEGER:
                result.put(key, value.asBigInteger());
                break;
            case BIG_DECIMAL:
                result.put(key, value.asBigDecimal());
                break;
            case LONG:
                result.put(key, value.asBigDecimal());
                break;
            case BOOLEAN:
                result.put(key, value.asBoolean());
                break;
            default:
                result.put(key, value.asString());
            }
        }
        return result;
    }

    private List<Object> asList(ModelNode node) {
        List<Object> list = new ArrayList<>();
        for (ModelNode entry : node.asList()) {
            switch (entry.getType()) {
            case OBJECT:
                list.add(asMap(entry));
                break;
            case LIST:
                list.add(asList(entry));
                break;
            case STRING:
                list.add(entry.asString());
                break;
            case INT:
                list.add(entry.asInt());
                break;
            case BIG_INTEGER:
                list.add(entry.asBigInteger());
                break;
            case BIG_DECIMAL:
                list.add(entry.asBigDecimal());
                break;
            case LONG:
                list.add(entry.asBigDecimal());
                break;
            case BOOLEAN:
                list.add(entry.asBoolean());
                break;
            default:
                list.add(entry.asString());
            }

        }
        return list;
    }

    @Override
    public boolean isSupported() {
        return resource != null;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

}
