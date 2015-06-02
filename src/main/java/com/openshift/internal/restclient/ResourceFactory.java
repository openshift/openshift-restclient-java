/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
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

import org.apache.commons.io.IOUtils;
import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.model.Build;
import com.openshift.internal.restclient.model.BuildConfig;
import com.openshift.internal.restclient.model.Config;
import com.openshift.internal.restclient.model.DeploymentConfig;
import com.openshift.internal.restclient.model.ImageStream;
import com.openshift.internal.restclient.model.KubernetesEvent;
import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.internal.restclient.model.LimitRange;
import com.openshift.internal.restclient.model.Pod;
import com.openshift.internal.restclient.model.Project;
import com.openshift.internal.restclient.model.ReplicationController;
import com.openshift.internal.restclient.model.ResourceQuota;
import com.openshift.internal.restclient.model.Route;
import com.openshift.internal.restclient.model.Service;
import com.openshift.internal.restclient.model.Status;
import com.openshift.internal.restclient.model.authorization.OpenshiftPolicy;
import com.openshift.internal.restclient.model.authorization.OpenshiftRole;
import com.openshift.internal.restclient.model.authorization.PolicyBinding;
import com.openshift.internal.restclient.model.authorization.RoleBinding;
import com.openshift.internal.restclient.model.oauth.OAuthAccessToken;
import com.openshift.internal.restclient.model.oauth.OAuthAuthorizeToken;
import com.openshift.internal.restclient.model.oauth.OAuthClient;
import com.openshift.internal.restclient.model.oauth.OAuthClientAuthorization;
import com.openshift.internal.restclient.model.project.OpenshiftProjectRequest;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.internal.restclient.model.template.Template;
import com.openshift.internal.restclient.model.user.OpenShiftUser;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.ResourceFactoryException;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.ICapability;
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
		//OpenShift kinds
		IMPL_MAP.put(ResourceKind.Build, Build.class);
		IMPL_MAP.put(ResourceKind.BuildConfig, BuildConfig.class);
		IMPL_MAP.put(ResourceKind.Config, Config.class);
		IMPL_MAP.put(ResourceKind.DeploymentConfig, DeploymentConfig.class);
		IMPL_MAP.put(ResourceKind.ImageStream, ImageStream.class);
		IMPL_MAP.put(ResourceKind.List, com.openshift.internal.restclient.model.List.class);
		IMPL_MAP.put(ResourceKind.OAuthAccessToken, OAuthAccessToken.class);
		IMPL_MAP.put(ResourceKind.OAuthAuthorizeToken, OAuthAuthorizeToken.class);
		IMPL_MAP.put(ResourceKind.OAuthClient, OAuthClient.class);
		IMPL_MAP.put(ResourceKind.OAuthClientAuthorization, OAuthClientAuthorization.class);
		IMPL_MAP.put(ResourceKind.Project, Project.class);
		IMPL_MAP.put(ResourceKind.ProjectRequest, OpenshiftProjectRequest.class);
		IMPL_MAP.put(ResourceKind.Policy, OpenshiftPolicy.class);
		IMPL_MAP.put(ResourceKind.PolicyBinding, PolicyBinding.class);
		IMPL_MAP.put(ResourceKind.Role, OpenshiftRole.class);
		IMPL_MAP.put(ResourceKind.RoleBinding, RoleBinding.class);
		IMPL_MAP.put(ResourceKind.Route, Route.class);
		IMPL_MAP.put(ResourceKind.Template, Template.class);
		IMPL_MAP.put(ResourceKind.User, OpenShiftUser.class);
		
		//Kubernetes Kinds
		IMPL_MAP.put(ResourceKind.Event, KubernetesEvent.class);
		IMPL_MAP.put(ResourceKind.LimitRange, LimitRange.class);
		IMPL_MAP.put(ResourceKind.Pod, Pod.class);
		IMPL_MAP.put(ResourceKind.ResourceQuota, ResourceQuota.class);
		IMPL_MAP.put(ResourceKind.ReplicationController, ReplicationController.class);
		IMPL_MAP.put(ResourceKind.Status, Status.class);
		IMPL_MAP.put(ResourceKind.Service, Service.class);
	}

	private IClient client;
	private final Map<ResourceKind, List<Class<? extends ICapability>>> resourceCapabillities;
	
	public ResourceFactory(IClient client) {
		this.client = client;
		this.resourceCapabillities = new HashMap<ResourceKind, List<Class<? extends ICapability>>>();
	}
	
	public void registerCapability(ResourceKind kind, Class<? extends ICapability>... capabilities) {
		if(kind == null) {
			throw new IllegalArgumentException("Kind must be specified");
		}
		if(capabilities == null || capabilities.length == 0) {
			return;
		}
		List<Class<? extends ICapability>> registeredCap = resourceCapabillities.get(kind);
		if(registeredCap == null) {
			registeredCap = new ArrayList<Class<? extends ICapability>>();
		}
		Collections.addAll(registeredCap, capabilities);
		resourceCapabillities.put(kind, registeredCap);
	}

	public static Map<ResourceKind, Class<? extends IResource>> getImplMap(){
		return Collections.unmodifiableMap(IMPL_MAP);
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

	@Override
	public <T extends IResource> T create(InputStream input) {
		try {
			String resource = IOUtils.toString(input, "UTF-8");
			return create(resource);
		} catch (IOException e) {
			throw new ResourceFactoryException(e, "There was an exception creating the resource from the InputStream");
		}
	}

	public <T extends IResource> T create(String response) {
		try {
			ModelNode node = ModelNode.fromJSONString(response);
			String version = node.get(APIVERSION).asString();
			ResourceKind kind = ResourceKind.valueOf(node.get(KIND).asString());
			return create(node, version, kind);
		}catch(Exception e) {
			throw new ResourceFactoryException(e, "There was an exception creating the resource from: %s", response);
		}
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
			T resource = (T) constructor.newInstance(node, client, properyKeyMap);

			addCapabilities(resource);

			return resource;
		} catch (Exception e) {
			throw new ResourceFactoryException(e,"Unable to create %s resource kind %s from %s", version, kind, node);
		}
	}

	private void addCapabilities(IResource resource) {
		if(resource instanceof KubernetesResource) {
			((KubernetesResource)resource).addCapability(resourceCapabillities.get(resource.getKind()));
		}
	}
	
}
