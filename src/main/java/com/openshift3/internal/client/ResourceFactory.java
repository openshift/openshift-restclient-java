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
import com.openshift3.internal.client.model.KubernetesResource;
import com.openshift3.internal.client.model.Pod;
import com.openshift3.internal.client.model.Project;
import com.openshift3.internal.client.model.ReplicationController;
import com.openshift3.internal.client.model.Service;
import com.openshift3.internal.client.model.Status;

/**
 * ResourceFactory creates a list of resources from a json string 
 */
public class ResourceFactory implements IResourceFactory{
	
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
		String dataKind = data.get("kind").asString();
		if(ResourceKind.Project.toString().equals(dataKind)){
			return buildProjectListForSingleProject(data);
		}
		if(!(kind.toString() + "List").equals(dataKind)){
			throw new RuntimeException(String.format("Unexpected container type '%s' for desired kind: %s", dataKind, kind));
		}
		
		try{
			return buildList(data.get("items").asList(), IMPL_MAP.get(kind));
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * Project is apparently special as query for project with namespace returns a singular
	 * project
	 */
	private List<IResource> buildProjectListForSingleProject(ModelNode data) {
		ArrayList<IResource> projects = new ArrayList<IResource>(1);
		projects.add(new Project(data, client));
		return projects;
	}

	private <T extends IResource> List<IResource> buildList(List<ModelNode> items, Class<T> kind) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor<T> constructor = kind.getConstructor(ModelNode.class, IClient.class);
		List<IResource> resources = new ArrayList<IResource>(items.size());
		for (ModelNode item : items) {
			resources.add(constructor.newInstance(item, client));
		}
		return resources;
	}

	@SuppressWarnings("unchecked")
	public <T extends IResource> T create(String response) {
		KubernetesResource resource = new KubernetesResource(response);
		try {
			Constructor<? extends IResource> constructor = IMPL_MAP.get(resource.getKind()).getConstructor(ModelNode.class, IClient.class);
			return (T) constructor.newInstance(resource.getNode(), client);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
