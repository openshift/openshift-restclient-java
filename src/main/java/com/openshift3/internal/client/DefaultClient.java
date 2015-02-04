/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client;

import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.client.IHttpClient;
import com.openshift.internal.client.httpclient.HttpClientException;
import com.openshift.internal.client.httpclient.UrlConnectionHttpClientBuilder;
import com.openshift3.client.IClient;
import com.openshift3.client.OpenShiftException;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.capability.Capability;
import com.openshift3.client.capability.CapabilityInitializer;
import com.openshift3.client.model.IResource;
import com.openshift3.internal.client.model.Status;

public class DefaultClient implements IClient{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultClient.class);
	private URL baseUrl;
	private IHttpClient client;
	private IResourceFactory factory;
	private Map<Class<? extends Capability>, Capability> capabilities = new HashMap<Class<? extends Capability>, Capability>();
	private boolean capabilitiesInitialized = false;
	
	private static final String apiEndpoint = "api/v1beta1";
	private static final String osApiEndpoint = "osapi/v1beta1";
	
	private static final Map<ResourceKind, String> TYPE_MAPPING = new HashMap<ResourceKind, String>();
	
	static {
		//OpenShift endpoints
		TYPE_MAPPING.put(ResourceKind.BuildConfig, osApiEndpoint);
		TYPE_MAPPING.put(ResourceKind.DeploymentConfig, osApiEndpoint);
		TYPE_MAPPING.put(ResourceKind.ImageRepository, osApiEndpoint);
		TYPE_MAPPING.put(ResourceKind.Project, osApiEndpoint);
		
		//Kubernetes endpoints
		TYPE_MAPPING.put(ResourceKind.Pod, apiEndpoint);
		TYPE_MAPPING.put(ResourceKind.Service, apiEndpoint);
		
	}

	public DefaultClient(URL baseUrl){
		this(baseUrl, null);
	}
	
	/*
	 * Testing constructor
	 */
	DefaultClient(URL baseUrl,  IHttpClient httpClient){
		this.baseUrl = baseUrl;
		client = httpClient != null ? httpClient : newIHttpClient();
		factory = new ResourceFactory(this);
	}
	
	/*
	 * Factory method for testing
	 */
	private IHttpClient newIHttpClient(){
		return  new UrlConnectionHttpClientBuilder()
		.setAcceptMediaType("application/json")
		.client();
	}
	
	@Override
	public <T extends IResource> List<T> list(ResourceKind kind) {
		return list(kind,""); //assumes namespace=default
	}
	
	@Override
	public <T extends IResource> List<T> list(ResourceKind kind, String namespace) {
		return list(kind, namespace, new HashMap<String, String>());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends IResource> List<T> list(ResourceKind kind, String namespace, Map<String, String> labels) {
		if(!TYPE_MAPPING.containsKey(kind))
			throw new RuntimeException("No OpenShift resource endpoint for type: " + kind);
		try {
			URLBuilder builder = new URLBuilder(this.baseUrl, TYPE_MAPPING)
				.kind(kind)
				.namespace(namespace);
			final URL endpoint = builder.build();
			String response = client.get(endpoint,  IHttpClient.DEFAULT_READ_TIMEOUT);
			LOGGER.debug(String.format("List Response: %s:", response));
			List<T> items = (List<T>) factory.createList(response, kind);
			return filterItems(items, labels); //client filter until we can figure out how to restrict with a server call
		} catch (HttpClientException e){
			throw new OpenShiftException("Exception listing the resources", e, factory.<Status>create(e.getMessage()));
		} catch (Exception e) {
			LOGGER.error("Exception", e);
			throw new RuntimeException(e);
		}
	}
	
	private <T extends IResource> List<T> filterItems(List<T> items, Map<String, String> labels){
		if(labels.isEmpty()) return items;
		List<T> filtered = new ArrayList<T>();
		for (T item : items) {
			if( item.getLabels().entrySet().containsAll(labels.entrySet())){
				filtered.add(item);
			}
		}
		return filtered;
	}

	@Override
	public <T extends IResource> T create(T resource) {
		try {
			final URL endpoint = new URLBuilder(this.baseUrl, TYPE_MAPPING)
				.kind(resource.getKind())
				.addParmeter("namespace", resource.getNamespace())
				.build();
			String response = client.post(endpoint,  IHttpClient.DEFAULT_READ_TIMEOUT, resource);
			LOGGER.debug(response);
			return factory.create(response);
		} catch (HttpClientException e){
			throw new OpenShiftException("Exception creating the resource", e, factory.<Status>create(e.getMessage()));
		} catch (Exception e) {
			LOGGER.error("Exception", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T extends IResource> void delete(T resource) {
		try {
			final URL endpoint = new URLBuilder(this.baseUrl, TYPE_MAPPING)
				.resource(resource)
				.addParmeter("namespace", resource.getNamespace())
				.build();
			String response = client.delete(endpoint,  IHttpClient.DEFAULT_READ_TIMEOUT);
			LOGGER.debug(response);
			//TODO return response object here
		} catch (HttpClientException e){
			throw new OpenShiftException("Exception deleting the resource", e, factory.<Status>create(e.getMessage()));
		} catch (Exception e) {
			LOGGER.error("Exception", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T extends IResource> T get(ResourceKind kind, String name, String namespace) {
		try {
			final URL endpoint = new URLBuilder(this.baseUrl, TYPE_MAPPING)
				.kind(kind)
				.name(name)
				.addParmeter("namespace", namespace)
				.build();
			String response = client.get(endpoint, IHttpClient.DEFAULT_READ_TIMEOUT);
			LOGGER.debug(response);
			return factory.create(response);
		} catch (HttpClientException e){
			throw new OpenShiftException("Exception getting the resource", e, factory.<Status>create(e.getMessage()));
		} catch (Exception e) {
			LOGGER.error("Exception", e);
			throw new RuntimeException(e);
		}
	}

	public synchronized void initializeCapabilities(){
		if(capabilitiesInitialized) return;
		new CapabilityInitializer().populate(capabilities, this);
		capabilitiesInitialized = true;
	}
	
	@Override
	public AuthorizationContext authorize() {
		try {
			client.get(this.baseUrl,  IHttpClient.DEFAULT_READ_TIMEOUT);
			return new AuthorizationContext();
		} catch (SocketTimeoutException e) {
			LOGGER.error("Socket timeout trying to connect", e);
		} catch (HttpClientException e) {
			LOGGER.error("HttpClient Exception trying to connect", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Capability> T getCapability(Class<T> capability) {
		return  (T) capabilities.get(capability);
	}

	@Override
	public  boolean isCapableOf(Class<? extends Capability> capability) {
		if(!capabilitiesInitialized ){
			initializeCapabilities();
		}
		return capabilities.containsKey(capability);
	}



}
