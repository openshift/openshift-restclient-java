/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.model.Build;
import com.openshift.internal.restclient.model.BuildConfig;
import com.openshift.internal.restclient.model.Config;
import com.openshift.internal.restclient.model.DeploymentConfig;
import com.openshift.internal.restclient.model.ImageRepository;
import com.openshift.internal.restclient.model.Pod;
import com.openshift.internal.restclient.model.Project;
import com.openshift.internal.restclient.model.ReplicationController;
import com.openshift.internal.restclient.model.Route;
import com.openshift.internal.restclient.model.Service;
import com.openshift.internal.restclient.model.Status;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.internal.restclient.model.template.Template;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IResource;

/**
 * ResourceFactory creates a list of resources from a json string 
 * 
 * @author Jeff Cantrill
 */
public class ResourceFactory implements IResourceFactory{
	
	private static final String KIND = "kind";
	private static final String APIVERSION = "apiVersion";
	private static final Map<ResourceKind, Class<? extends IResource>> IMPL_MAP = new HashMap<ResourceKind, Class<? extends IResource>>();
	static {
		IMPL_MAP.put(ResourceKind.Build, Build.class);
		IMPL_MAP.put(ResourceKind.BuildConfig, BuildConfig.class);
		IMPL_MAP.put(ResourceKind.Config, Config.class);
		IMPL_MAP.put(ResourceKind.DeploymentConfig, DeploymentConfig.class);
		IMPL_MAP.put(ResourceKind.ImageRepository, ImageRepository.class);
		IMPL_MAP.put(ResourceKind.Project, Project.class);
		IMPL_MAP.put(ResourceKind.Pod, Pod.class);
		IMPL_MAP.put(ResourceKind.ReplicationController, ReplicationController.class);
		IMPL_MAP.put(ResourceKind.Route, Route.class);
		IMPL_MAP.put(ResourceKind.Status, Status.class);
		IMPL_MAP.put(ResourceKind.Service, Service.class);
		IMPL_MAP.put(ResourceKind.Template, Template.class);
	}
	private IClient client;
	
	public ResourceFactory(IClient client) {
		this.client = client;
	}

	public List<IResource> createList(String json, ResourceKind kind){
		ModelNode data = ModelNode.fromJSONString(json);
		final String dataKind = data.get(KIND).asString();
		if(!(kind.toString() + "List").equals(dataKind)){
			throw new RuntimeException(String.format("Unexpected container type '%s' for desired kind: %s", dataKind, kind));
		}
		
		try{
			final String version = data.get(APIVERSION).asString();
			return buildList(version, data.get("items").asList(), kind);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	private List<IResource> buildList(final String version, List<ModelNode> items, ResourceKind kind) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		List<IResource> resources = new ArrayList<IResource>(items.size());
		for (ModelNode item : items) {
			resources.add(create(item, version, kind));
		}
		return resources;
	}

	public <T extends IResource> T create(String response) {
		ModelNode node = ModelNode.fromJSONString(response);
		String version = node.get(APIVERSION).asString();
		ResourceKind kind = ResourceKind.valueOf(node.get(KIND).asString());
		return create(node, version, kind);
	}

	public <T extends IResource> T create(String version, ResourceKind kind) {
		return create(new ModelNode(), version, kind);
	}

	@SuppressWarnings("unchecked")
	private  <T extends IResource> T create(ModelNode node, String version, ResourceKind kind) {
		try {
			node.get(APIVERSION).set(version);
			node.get(KIND).set(kind.toString());
			Map<String, String[]> properyKeyMap = ResourcePropertiesRegistry.getInstance().get(version, kind);
			Constructor<? extends IResource> constructor =  IMPL_MAP.get(kind).getConstructor(ModelNode.class, IClient.class, Map.class);
			return (T) constructor.newInstance(node, client, properyKeyMap);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
