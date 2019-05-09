/*******************************************************************************
 * Copyright (c) 2015-2019 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IApiTypeMapper;
import com.openshift.restclient.IApiTypeMapper.IVersionedType;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.ResourceFactoryException;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.UnsupportedVersionException;
import com.openshift.restclient.model.IResource;

/**
 * ResourceFactory creates a list of resources from a json string
 * 
 */
public class ResourceFactory implements IResourceFactory {

    private static final String KIND = "kind";
    private static final String APIVERSION = "apiVersion";
    private static final Map<String, Class<? extends IResource>> IMPL_MAP = new HashMap<>();

    private IClient client;

    public ResourceFactory(IClient client) {
        this.client = client;
    }

    public static Map<String, Class<? extends IResource>> getImplMap() {
        return Collections.unmodifiableMap(IMPL_MAP);
    }

    public List<IResource> createList(String json, String kind) {
        ModelNode data = ModelNode.fromJSONString(json);
        final String dataKind = data.get(KIND).asString();
        if (!(kind.toString() + "List").equals(dataKind)) {
            throw new RuntimeException(
                    String.format("Unexpected container type '%s' for desired kind: %s", dataKind, kind));
        }

        try {
            final String version = data.get(APIVERSION).asString();
            return buildList(version, data.get("items").asList(), kind);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<IResource> buildList(final String version, List<ModelNode> items, String kind)
            throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        List<IResource> resources = new ArrayList<IResource>(items.size());
        for (ModelNode item : items) {
            resources.add(create(item, version, kind));
        }
        return resources;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IResource create(InputStream input) {
        try {
            String resource = IOUtils.toString(input, "UTF-8");
            return create(resource);
        } catch (IOException e) {
            throw new ResourceFactoryException(e, "There was an exception creating the resource from the InputStream");
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T extends IResource> T create(String response) {
        try {
            ModelNode node = ModelNode.fromJSONString(response);
            String version = node.get(APIVERSION).asString();
            String kind = node.get(KIND).asString();
            return (T) create(node, version, kind);
        } catch (UnsupportedVersionException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceFactoryException(e, "There was an exception creating the resource from: %s", response);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IResource> T create(String version, String kind) {
        return (T) create(new ModelNode(), version, kind);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends IResource> T create(String version, String kind, String name) {
        T resource = (T) create(new ModelNode(), version, kind);
        ((KubernetesResource) resource).setName(name);
        return resource;
    }

    private IResource create(ModelNode node, String version, String kind) {
        try {
            node.get(APIVERSION).set(version);
            node.get(KIND).set(kind.toString());
            Map<String, String[]> properyKeyMap = ResourcePropertiesRegistry.getInstance().get(version, kind);
            if (kind.endsWith("List")) {
                return new com.openshift.internal.restclient.model.List(node, client, properyKeyMap);
            }
            Class<? extends IResource> klass = getResourceClass(version, kind);
            if (klass != null) {
                Constructor<? extends IResource> constructor = klass.getConstructor(ModelNode.class, IClient.class,
                        Map.class);
                return constructor.newInstance(node, client, properyKeyMap);
            }
            return new KubernetesResource(node, client, properyKeyMap);
        } catch (UnsupportedVersionException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceFactoryException(e, "Unable to create %s resource kind %s from %s", version, kind, node);
        }
    }

    @Override
    public Object createInstanceFrom(String response) {
        return create(response);
    }
    
    @SuppressWarnings("unchecked")
    private Class<? extends IResource> getResourceClass(String version, String kind) {
        IApiTypeMapper mapper = this.client.adapt(IApiTypeMapper.class);
        if (mapper != null) {
            try {
                IVersionedType type = mapper.getType(version, kind);
                return (Class<? extends IResource>) TypeRegistry.getInstance().getRegisteredType(type.getApiGroupNameAndVersion() + IApiTypeMapper.DOT + type.getKind());
            } catch (Exception e) {
                return (Class<? extends IResource>) TypeRegistry.getInstance().getRegisteredType(version + IApiTypeMapper.DOT + kind);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IResource> T stub(String kind, String name, String namespace) {
        String[] elements = ResourceKind.parse(kind);
        IVersionedType type = client.adapt(IApiTypeMapper.class).getType(elements[0], elements[1]);
        if (type != null) {
            KubernetesResource resource = (KubernetesResource) create(type.getApiGroupNameAndVersion(), elements[1]);
            resource.setName(name);
            resource.setNamespace(namespace);
            if (StringUtils.isNotEmpty(namespace)) {
                resource.setNamespace(namespace);
            }
            return (T) resource;
        } else {
            throw new ResourceFactoryException(null, "Unable to create resource from kind %s", kind);
        }
    }

    @Override
    public <T extends IResource> T stub(String kind, String name) {
        return stub(kind, name, null);
    }
    
    @Override
    public Object stubKind(String kind, Optional<String> name, Optional<String> namespace) {
        return stub(kind, name.get(), namespace.get());
    }

    @Override
    public void setClient(IClient client) {
        this.client = client;
    }

}
