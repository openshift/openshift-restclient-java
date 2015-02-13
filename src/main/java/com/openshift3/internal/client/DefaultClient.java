/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client;

import static com.openshift3.client.capability.CapabilityInitializer.initializeCapability;

import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLSession;

import org.jboss.dmr.ModelNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.client.IHttpClient;
import com.openshift.client.IHttpClient.ISSLCertificateCallback;
import com.openshift.internal.client.httpclient.HttpClientException;
import com.openshift.internal.client.httpclient.UrlConnectionHttpClientBuilder;
import com.openshift3.client.IClient;
import com.openshift3.client.OpenShiftException;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.UnsupportedVersionException;
import com.openshift3.client.capability.ICapability;
import com.openshift3.client.capability.server.IImageRegistryHosting;
import com.openshift3.client.model.IResource;
import com.openshift3.internal.client.capability.server.DefaultImageRegistryHosting;
import com.openshift3.internal.client.model.ResourcePropertiesRegistry;
import com.openshift3.internal.client.model.Status;

public class DefaultClient implements IClient{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultClient.class);
	private URL baseUrl;
	private IHttpClient client;
	private IResourceFactory factory;
	private Map<Class<? extends ICapability>, ICapability> capabilities = new HashMap<Class<? extends ICapability>, ICapability>();
	private boolean capabilitiesInitialized = false;
	
	private static final String apiEndpoint = "api";
	private static final String osApiEndpoint = "osapi";
	
	private final Map<ResourceKind, String> typeMappings = new HashMap<ResourceKind, String>();
	private OpenShiftAPIVersion openShiftVersion;
	private KubernetesAPIVersion kubernetesVersion;
	
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
		.setSSLCertificateCallback(new ISSLCertificateCallback() {
			
			@Override
			public boolean allowHostname(String hostname, SSLSession session) {
				return true;
			}
			
			@Override
			public boolean allowCertificate(X509Certificate[] chain) {
				return true;
			}
		})
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
		if(!getTypeMappings().containsKey(kind))
			throw new RuntimeException("No OpenShift resource endpoint for type: " + kind);
		try {
			URLBuilder builder = new URLBuilder(this.baseUrl, getTypeMappings())
				.kind(kind)
				.namespace(namespace);
			final URL endpoint = builder.build();
			String response = client.get(endpoint,  IHttpClient.DEFAULT_READ_TIMEOUT);
			LOGGER.debug(String.format("List Response: %s:", response));
			List<T> items = (List<T>) factory.createList(response, kind);
			return filterItems(items, labels); //client filter until we can figure out how to restrict with a server call
		} catch (HttpClientException e){
			LOGGER.debug("Exception while trying to list resource.", e);
			//TODO handle case where there is no status response
			throw new OpenShiftException("Exception listing the resources", e, factory.<Status>create(e.getMessage()));
		} catch (SocketTimeoutException e) {
			throw new com.openshift.client.OpenShiftException(e, "SocketTimeout listing resources");
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
			final URL endpoint = new URLBuilder(this.baseUrl, getTypeMappings())
				.kind(resource.getKind())
				.addParmeter("namespace", resource.getNamespace())
				.build();
			String response = client.post(endpoint,  IHttpClient.DEFAULT_READ_TIMEOUT, resource);
			LOGGER.debug(response);
			return factory.create(response);
		} catch (HttpClientException e){
			throw new OpenShiftException("Exception creating the resource", e, factory.<Status>create(e.getMessage()));
		} catch (SocketTimeoutException e) {
			throw new com.openshift.client.OpenShiftException(e, "SocketTimeout creating resource %", resource.getName());
		}
	}

	@Override
	public <T extends IResource> void delete(T resource) {
		try {
			final URL endpoint = new URLBuilder(this.baseUrl, getTypeMappings())
				.resource(resource)
				.addParmeter("namespace", resource.getNamespace())
				.build();
			LOGGER.debug(String.format("Deleting resource: %s", endpoint));
			String response = client.delete(endpoint,  IHttpClient.DEFAULT_READ_TIMEOUT);
			LOGGER.debug(response);
			//TODO return response object here
		} catch (HttpClientException e){
			throw new OpenShiftException("Exception deleting the resource", e, factory.<Status>create(e.getMessage()));
		} catch (SocketTimeoutException e) {
			throw new com.openshift.client.OpenShiftException(e, "SocketTimeout deleting resource %", resource.getName());
		} 
	}

	@Override
	public <T extends IResource> T get(ResourceKind kind, String name, String namespace) {
		try {
			final URL endpoint = new URLBuilder(this.baseUrl, getTypeMappings())
				.kind(kind)
				.name(name)
				.addParmeter("namespace", namespace)
				.build();
			String response = client.get(endpoint, IHttpClient.DEFAULT_READ_TIMEOUT);
			LOGGER.debug(response);
			return factory.create(response);
		} catch (HttpClientException e){
			throw new OpenShiftException("Exception getting the resource", e, factory.<Status>create(e.getMessage()));
		} catch (SocketTimeoutException e) {
			throw new com.openshift.client.OpenShiftException(e, "SocketTimeout getting resource %", name);
		} 
	}

	public synchronized void initializeCapabilities(){
		if(capabilitiesInitialized) return;
		initializeCapability(capabilities, IImageRegistryHosting.class, new DefaultImageRegistryHosting(this));
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
	public <T extends ICapability> T getCapability(Class<T> capability) {
		return  (T) capabilities.get(capability);
	}

	@Override
	public  boolean supports(Class<? extends ICapability> capability) {
		if(!capabilitiesInitialized ){
			initializeCapabilities();
		}
		return capabilities.containsKey(capability);
	}

	public List<KubernetesAPIVersion> getKubernetesVersions() {
		return getVersion(KubernetesAPIVersion.class, apiEndpoint);
	}

	public List<OpenShiftAPIVersion> getOpenShiftVersions() {
		return getVersion(OpenShiftAPIVersion.class, osApiEndpoint);
	}

	public KubernetesAPIVersion getKubernetesVersion() {
		if(kubernetesVersion == null){
			List<KubernetesAPIVersion> versions = getKubernetesVersions();
			kubernetesVersion = ResourcePropertiesRegistry.getInstance().getMaxSupportedKubernetesVersion();
			if(!versions.contains(kubernetesVersion)){
				throw new RuntimeException(String.format("Kubernetes API version '%s' is not supported by this client"));
			}
		}
		return kubernetesVersion; 
	}

	@Override
	public String getOpenShiftAPIVersion() throws UnsupportedVersionException{
		return getOpenShiftVersion().toString();
	}
	
	public OpenShiftAPIVersion getOpenShiftVersion() {
		if(openShiftVersion == null){
			List<OpenShiftAPIVersion> versions = getOpenShiftVersions();
			openShiftVersion = ResourcePropertiesRegistry.getInstance().getMaxSupportedOpenShiftVersion();
			if(!versions.contains(openShiftVersion)){
				throw new UnsupportedVersionException(openShiftVersion.toString());
			}
		}
		return openShiftVersion; 
	}
	
	private <T extends Enum<T>> List<T> getVersion(Class<T> klass, String endpoint) {
		try {
			final URL url = new URL(this.baseUrl, endpoint);
			LOGGER.debug(url.toString());
			String response = client.get(url, IHttpClient.DEFAULT_READ_TIMEOUT);
			LOGGER.debug(response);
			ModelNode json = ModelNode.fromJSONString(response);
			List<ModelNode> versionNodes = json.get("versions").asList();
			List<T> versions = new ArrayList<T>(versionNodes.size());
			for (ModelNode node : versionNodes) {
				try{
					versions.add(Enum.valueOf(klass, node.asString()));
				}catch(IllegalArgumentException e){
					LOGGER.warn(String.format("Unsupported server version '%s' for '%s'",  node.asString(), klass.getSimpleName()));
				}
			}
			return versions;
		} catch (Exception e) {
			LOGGER.error("Exception", e);
			throw new RuntimeException(e);
		}
	}

	private Map<ResourceKind, String> getTypeMappings(){
		if(typeMappings.isEmpty()){
			//OpenShift endpoints
			final String osEndpoint = String.format("%s/%s", osApiEndpoint, getOpenShiftVersion());
			typeMappings.put(ResourceKind.Build, osEndpoint);
			typeMappings.put(ResourceKind.BuildConfig, osEndpoint);
			typeMappings.put(ResourceKind.DeploymentConfig, osEndpoint);
			typeMappings.put(ResourceKind.ImageRepository, osEndpoint);
			typeMappings.put(ResourceKind.Project, osEndpoint);
			
			//Kubernetes endpoints
			final String k8eEndpoint = String.format("%s/%s", apiEndpoint, getKubernetesVersion());
			typeMappings.put(ResourceKind.Pod, k8eEndpoint);
			typeMappings.put(ResourceKind.Service, k8eEndpoint);
			typeMappings.put(ResourceKind.ReplicationController, k8eEndpoint);
		}
		return typeMappings;
	}

	@Override
	public URL getBaseURL() {
		return this.baseUrl;
	}


}
