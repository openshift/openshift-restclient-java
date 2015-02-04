/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift3.client.IClient;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.model.IResource;
import com.openshift3.internal.client.model.BuildConfig;
import com.openshift3.internal.client.model.DeploymentConfig;
import com.openshift3.internal.client.model.ImageRepository;
import com.openshift3.internal.client.model.Pod;
import com.openshift3.internal.client.model.Project;
import com.openshift3.internal.client.model.ReplicationController;
import com.openshift3.internal.client.model.ResourcePropertiesRegistry;
import com.openshift3.internal.client.model.Service;
import com.openshift3.internal.client.model.Status;

/**
 * ResourceFactory creates a list of resources from a json string 
 */
public class ResourceFactory implements IResourceFactory{
	
	private static final String KIND = "kind";
	private static final String APIVERSION = "apiVersion";
	private static final Map<ResourceKind, Class<? extends IResource>> IMPL_MAP = new HashMap<ResourceKind, Class<? extends IResource>>();
	static {
		IMPL_MAP.put(ResourceKind.BuildConfig, BuildConfig.class);
		IMPL_MAP.put(ResourceKind.DeploymentConfig, DeploymentConfig.class);
		IMPL_MAP.put(ResourceKind.ImageRepository, ImageRepository.class);
		IMPL_MAP.put(ResourceKind.Project, Project.class);
		IMPL_MAP.put(ResourceKind.Pod, Pod.class);
		IMPL_MAP.put(ResourceKind.ReplicationController, ReplicationController.class);
		IMPL_MAP.put(ResourceKind.Status, Status.class);
		IMPL_MAP.put(ResourceKind.Service, Service.class);
	}
	private IClient client;
	
	public ResourceFactory(IClient client) {
		this.client = client;
	}

	public List<IResource> createList(String json, ResourceKind kind){
		ModelNode data = ModelNode.fromJSONString(json);
		final String dataKind = data.get(KIND).asString();
		if(ResourceKind.Project.toString().equals(dataKind)){
			return buildProjectListForSingleProject(json);
		}
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
	
	/*
	 * Project is apparently special as query for project with namespace returns a singular
	 * project
	 */
	private List<IResource> buildProjectListForSingleProject(String data) {
		ArrayList<IResource> projects = new ArrayList<IResource>(1);
		projects.add(create(data));
		return projects;
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
		ModelNode node = new ModelNode();
		node.get(APIVERSION).set(version);
		node.get(KIND).set(kind.toString());
		return create(node, version, kind);
	}

	@SuppressWarnings("unchecked")
	private  <T extends IResource> T create(ModelNode node, String version, ResourceKind kind) {
		try {
			Map<String, String[]> properyKeyMap = ResourcePropertiesRegistry.getInstance().get(version, kind);
			Constructor<? extends IResource> constructor =  IMPL_MAP.get(kind).getConstructor(ModelNode.class, IClient.class, Map.class);
			return (T) constructor.newInstance(node, client, properyKeyMap);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
