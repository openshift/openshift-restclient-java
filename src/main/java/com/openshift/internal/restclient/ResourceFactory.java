/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient;

import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.*;
import com.openshift.restclient.IApiTypeMapper.IVersionedApiResource;
import com.openshift.restclient.model.IResource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.dmr.ModelNode;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ResourceFactory creates a list of resources from a json string
 *
 * @author Jeff Cantrill
 */
public class ResourceFactory implements IResourceFactory {

    private static final String KIND = "kind";
    private static final String APIVERSION = "apiVersion";

    private IClient client;
    private final ResourceKindRegistry resourceKindRegistry;

    public ResourceFactory(final IClient client) {
        this(client, null);
    }

    public ResourceFactory(final IClient client, final ResourceKindRegistry resourceKindRegistry) {
        this.client = client;
        this.resourceKindRegistry = Optional.ofNullable(resourceKindRegistry).orElseGet(DefaultResourceKindRegistry::new);
    }

    @Override
    public List<IResource> createList(String json, String kind) {
        ModelNode data = ModelNode.fromJSONString(json);
        final String dataKind = data.get(KIND).asString();
        if (!(kind.toString() + "List").equals(dataKind)) {
            throw new RuntimeException(String.format("Unexpected container type '%s' for desired kind: %s", dataKind, kind));
        }

        try {
            final String version = data.get(APIVERSION).asString();
            return buildList(version, data.get("items").asList(), kind);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<IResource> buildList(final String version, List<ModelNode> items, String kind) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
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
    public Object createInstanceFrom(String response) {
        return create(response);
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
                Constructor<? extends IResource> constructor = klass.getConstructor(ModelNode.class, IClient.class, Map.class);
                return constructor.newInstance(node, client, properyKeyMap);
            }
            return new KubernetesResource(node, client, properyKeyMap);
        } catch (UnsupportedVersionException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceFactoryException(e, "Unable to create %s resource kind %s from %s", version, kind, node);
        }
    }

    @SuppressWarnings("unchecked")
    // Note: It would be nice if you kept that method protected, since this way people could extend this factory, thank you.
    private Class<? extends IResource> getResourceClass(String version, String kind) {
        return resourceKindRegistry.find(kind) // First try to get the resource kind from that registration map
                .flatMap(ResourceKind::getImplementationClass) // Use the implementation class if present
                .orElseGet(() -> this.getResourceClassViaMapper(version, kind)); // Fall back to that mapper
    }

    private Class<? extends IResource> getResourceClassViaMapper(final String version, final String kind) {
        IApiTypeMapper mapper = this.client.adapt(IApiTypeMapper.class);
        if (mapper != null) {
            IVersionedApiResource endpoint = mapper.getEndpointFor(version, kind);
            String extension = "";
            switch (endpoint.getPrefix()) {
                case IApiTypeMapper.KUBE_API:
                case IApiTypeMapper.OS_API:
                    break;
                default:
                    String extPlusVersion = endpoint.getApiGroupName();
                    extension = StringUtils.split(extPlusVersion, IApiTypeMapper.FWD_SLASH)[0];
            }
            try {
                String classname = String.format("com.openshift.internal.restclient.%s%s.models.%s", endpoint.getPrefix(), extension, endpoint.getKind());
                return (Class<? extends IResource>) Class.forName(classname);
            } catch (ClassNotFoundException e) {
                //class doesnt exist in the exp location.
                //fallback to an explicit registration
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IResource> T stub(String kind, String name, String namespace) {
        //TODO get k8e or os
        String version = client.getOpenShiftAPIVersion();
        KubernetesResource resource = (KubernetesResource) create(version, kind);
        resource.setName(name);
        resource.setNamespace(namespace);
        if (StringUtils.isNotEmpty(namespace)) {
            resource.setNamespace(namespace);
        }
        return (T) resource;
    }

    @Override
    public Object stubKind(String kind, Optional<String> name, Optional<String> namespace) {
        return stub(kind, name.get(), namespace.get());
    }

    @Override
    public <T extends IResource> T stub(String kind, String name) {
        return stub(kind, name, null);
    }

    @Override
    public void setClient(IClient client) {
        this.client = client;
    }

    @Override
    public ResourceKindRegistry getResourceKindRegistry() {
        return this.resourceKindRegistry;
    }

}
