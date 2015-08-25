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
import org.apache.commons.lang.StringUtils;
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
import com.openshift.internal.restclient.model.Secret;
import com.openshift.internal.restclient.model.Service;
import com.openshift.internal.restclient.model.Status;
import com.openshift.internal.restclient.model.authorization.OpenshiftPolicy;
import com.openshift.internal.restclient.model.authorization.OpenshiftRole;
import com.openshift.internal.restclient.model.authorization.PolicyBinding;
import com.openshift.internal.restclient.model.authorization.RoleBinding;
import com.openshift.internal.restclient.model.build.BuildRequest;
import com.openshift.internal.restclient.model.oauth.OAuthAccessToken;
import com.openshift.internal.restclient.model.oauth.OAuthAuthorizeToken;
import com.openshift.internal.restclient.model.oauth.OAuthClient;
import com.openshift.internal.restclient.model.oauth.OAuthClientAuthorization;
import com.openshift.internal.restclient.model.project.OpenshiftProjectRequest;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.internal.restclient.model.template.Template;
import com.openshift.internal.restclient.model.user.OpenShiftUser;
import com.openshift.internal.util.JBossDmrExtentions;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.ResourceFactoryException;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.UnsupportedVersionException;
import com.openshift.restclient.model.IResource;

/**
 * ResourceFactory creates a list of resources from a json string 
 * 
 * @author Jeff Cantrill
 */
public class ResourceFactory implements IResourceFactory{
	
	private static final String KIND = "kind";
	private static final String APIVERSION = "apiVersion";
	private static final Map<String, Class<? extends IResource>> IMPL_MAP = new HashMap<String, Class<? extends IResource>>();
	static {
		//OpenShift kinds
		IMPL_MAP.put(ResourceKind.BUILD, Build.class);
		IMPL_MAP.put(ResourceKind.BUILD_CONFIG, BuildConfig.class);
		IMPL_MAP.put(ResourceKind.BUILD_REQUEST, BuildRequest.class);
		IMPL_MAP.put(ResourceKind.CONFIG, Config.class);
		IMPL_MAP.put(ResourceKind.DEPLOYMENT_CONFIG, DeploymentConfig.class);
		IMPL_MAP.put(ResourceKind.IMAGE_STREAM, ImageStream.class);
		IMPL_MAP.put(ResourceKind.LIST, com.openshift.internal.restclient.model.List.class);
		IMPL_MAP.put(ResourceKind.OAUTH_ACCESS_TOKEN, OAuthAccessToken.class);
		IMPL_MAP.put(ResourceKind.OAUTH_AUTHORIZE_TOKEN, OAuthAuthorizeToken.class);
		IMPL_MAP.put(ResourceKind.OAUTH_CLIENT, OAuthClient.class);
		IMPL_MAP.put(ResourceKind.OAUTH_CLIENT_AUTHORIZATION, OAuthClientAuthorization.class);
		IMPL_MAP.put(ResourceKind.PROJECT, Project.class);
		IMPL_MAP.put(ResourceKind.PROJECT_REQUEST, OpenshiftProjectRequest.class);
		IMPL_MAP.put(ResourceKind.POLICY, OpenshiftPolicy.class);
		IMPL_MAP.put(ResourceKind.POLICY_BINDING, PolicyBinding.class);
		IMPL_MAP.put(ResourceKind.ROLE, OpenshiftRole.class);
		IMPL_MAP.put(ResourceKind.ROLE_BINDING, RoleBinding.class);
		IMPL_MAP.put(ResourceKind.ROUTE, Route.class);
		IMPL_MAP.put(ResourceKind.TEMPLATE, Template.class);
		IMPL_MAP.put(ResourceKind.USER, OpenShiftUser.class);
		
		//Kubernetes Kinds
		IMPL_MAP.put(ResourceKind.EVENT, KubernetesEvent.class);
		IMPL_MAP.put(ResourceKind.LIMIT_RANGE, LimitRange.class);
		IMPL_MAP.put(ResourceKind.POD, Pod.class);
		IMPL_MAP.put(ResourceKind.RESOURCE_QUOTA, ResourceQuota.class);
		IMPL_MAP.put(ResourceKind.REPLICATION_CONTROLLER, ReplicationController.class);
		IMPL_MAP.put(ResourceKind.STATUS, Status.class);
		IMPL_MAP.put(ResourceKind.SERVICE, Service.class);
		IMPL_MAP.put(ResourceKind.SECRET, Secret.class);
		
		//fallback
		IMPL_MAP.put(ResourceKind.UNRECOGNIZED, KubernetesResource.class);
		
	}
	private IClient client;
	
	protected ResourceFactory(IClient client) {
		this.client = client;
	}
	
	public static Map<String, Class<? extends IResource>> getImplMap(){
		return Collections.unmodifiableMap(IMPL_MAP);
	}

	public List<IResource> createList(String json, String kind){
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

	private List<IResource> buildList(final String version, List<ModelNode> items, String kind) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		List<IResource> resources = new ArrayList<IResource>(items.size());
		for (ModelNode item : items) {
			resources.add(create(item, version, kind, false));
		}
		return resources;
	}
	
	@Override
	public <T extends IResource> T create(InputStream input) {
		return create(input, false);
	}

	@Override
	public <T extends IResource> T create(InputStream input, boolean strict) {
		try {
			String resource = IOUtils.toString(input, "UTF-8");
			return create(resource, strict);
		} catch (IOException e) {
			throw new ResourceFactoryException(e, "There was an exception creating the resource from the InputStream");
		}
	}
	
	
	@Override
	public <T extends IResource> T create(String response) {
		return create(response, false);
	}

	@Override
	public <T extends IResource> T create(String response, boolean strict) {
		try {
			ModelNode node = ModelNode.fromJSONString(response);
			String version = node.get(APIVERSION).asString();
			String kind = node.get(KIND).asString();
			return create(node, version, kind, strict);
		} catch (UnsupportedVersionException e) {
			throw e;
		}catch(Exception e) {
			throw new ResourceFactoryException(e, "There was an exception creating the resource from: %s", response);
		}
	}
	
	
	@Override
	public <T extends IResource> T create(String version, String kind) {
		return create(version, kind, false);
	}

	@Override
	public <T extends IResource> T create(String version, String kind, boolean strict) {
		return create(new ModelNode(), version, kind, strict);
	}

	@SuppressWarnings("unchecked")
	private  <T extends IResource> T create(ModelNode node, String version, String kind, boolean strict) {
		try {
			node.get(APIVERSION).set(version);
			node.get(KIND).set(kind.toString());
			Map<String, String[]> properyKeyMap = ResourcePropertiesRegistry.getInstance().get(version, kind, strict);
			Constructor<? extends IResource> constructor =  IMPL_MAP.get(kind).getConstructor(ModelNode.class, IClient.class, Map.class);
			return (T) constructor.newInstance(node, client, properyKeyMap);
		} catch (UnsupportedVersionException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceFactoryException(e,"Unable to create %s resource kind %s from %s", version, kind, node);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends IResource> T stub(String kind, String name, String namespace) {
		//TODO get k8e or os
		String version = client.getOpenShiftAPIVersion();
		KubernetesResource resource = (KubernetesResource) create(version, kind, true);
		resource.setName(name);
		if(StringUtils.isNotEmpty(namespace)) {
			resource.setNamespace(namespace);
		}
		return (T) resource;
	}

	@Override
	public <T extends IResource> T stub(String kind, String name) {
		return stub(kind, name, null);
	}

	
	
}
