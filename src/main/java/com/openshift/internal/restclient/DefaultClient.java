/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient;

import static com.openshift.internal.restclient.capability.CapabilityInitializer.initializeClientCapabilities;

import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.http.HttpClientException;
import com.openshift.internal.restclient.http.UnauthorizedException;
import com.openshift.internal.restclient.http.UrlConnectionHttpClientBuilder;
import com.openshift.internal.restclient.model.Status;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.ISSLCertificateCallback;
import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.UnsupportedOperationException;
import com.openshift.restclient.authorization.AuthorizationClientFactory;
import com.openshift.restclient.authorization.IAuthorizationClient;
import com.openshift.restclient.authorization.IAuthorizationContext;
import com.openshift.restclient.authorization.IAuthorizationDetails;
import com.openshift.restclient.authorization.IAuthorizationStrategy;
import com.openshift.restclient.authorization.ResourceForbiddenException;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.http.IHttpClient;
import com.openshift.restclient.http.IHttpStatusCodes;
import com.openshift.restclient.model.IList;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.user.IUser;

/**
 * @author Jeff Cantrill
 */
public class DefaultClient implements IClient, IHttpStatusCodes{
	
	public static final String SYSTEM_PROP_K8E_API_VERSION = "osjc.k8e.apiversion"; 
	public static final String SYSTEM_PROP_OPENSHIFT_API_VERSION = "osjc.openshift.apiversion"; 
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultClient.class);
	private URL baseUrl;
	private IHttpClient client;
	private IResourceFactory factory;
	private Map<Class<? extends ICapability>, ICapability> capabilities = new HashMap<Class<? extends ICapability>, ICapability>();
	private boolean capabilitiesInitialized = false;
	
	private static final String apiEndpoint = "api";
	private static final String osApiEndpoint = "osapi";
	
	private final Map<String, String> typeMappings = new HashMap<String, String>();
	private String openShiftVersion;
	private String kubernetesVersion;
	private IAuthorizationStrategy strategy;
	private IAuthorizationClient authClient;
	
	public DefaultClient(URL baseUrl, ISSLCertificateCallback sslCertCallback){
		this(baseUrl, null, sslCertCallback);
	}
	
	/*
	 * Testing constructor
	 */
	DefaultClient(URL baseUrl,  IHttpClient httpClient,  ISSLCertificateCallback sslCertCallback){
		this.baseUrl = baseUrl;
		client = httpClient != null ? httpClient : newIHttpClient(sslCertCallback);
		factory = new ResourceFactory(this);
		openShiftVersion = System.getProperty(SYSTEM_PROP_OPENSHIFT_API_VERSION, null);
		kubernetesVersion = System.getProperty(SYSTEM_PROP_K8E_API_VERSION, null);
		authClient = new AuthorizationClientFactory().create(this);
	}
	
	/*
	 * Factory method for testing
	 */
	private IHttpClient newIHttpClient(ISSLCertificateCallback sslCertCallback){
		return  new UrlConnectionHttpClientBuilder()
		.setAcceptMediaType("application/json")
		.setSSLCertificateCallback(sslCertCallback)
		.client();
	}
	
	@Override
	public IResourceFactory getResourceFactory() {
		return factory;
	};
	
	@Override
	public <T extends IResource> List<T> list(String kind) {
		return list(kind,""); //assumes namespace=default
	}
	
	@Override
	public <T extends IResource> List<T> list(String kind, String namespace) {
		return list(kind, namespace, new HashMap<String, String>());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends IResource> List<T> list(String kind, String namespace, Map<String, String> labels) {
		try {
			if(!getTypeMappings().containsKey(kind))
				// TODO: replace with specific runtime exception
				throw new RuntimeException("No OpenShift resource endpoint for type: " + kind);
			URLBuilder builder = new URLBuilder(this.baseUrl, getTypeMappings())
				.kind(kind)
				.namespace(namespace);
			final URL endpoint = builder.build();
			String response = client.get(endpoint,  IHttpClient.DEFAULT_READ_TIMEOUT);
			LOGGER.debug(String.format("List Response: %s:", response));
			List<T> items = (List<T>) factory.createList(response, kind);
			return filterItems(items, labels); //client filter until we can figure out how to restrict with a server call
		} catch (HttpClientException e){
			throw handleHttpClientException("Exception listing the resources", e);
		} catch (SocketTimeoutException e) {
			throw new OpenShiftException(e, "SocketTimeout listing resources");
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
	public Collection<IResource> create(IList list, String namespace){
		List<IResource> results = new ArrayList<IResource>(list.getItems().size());
		for (IResource resource : list.getItems()) {
			try{
				results.add(create(resource, namespace));
			}catch(OpenShiftException e){
				if(e.getStatus() != null){
					results.add(e.getStatus());
				}else{
					throw e;
				}
			}
		}
		return results;
	}

	@Override
	public <T extends IResource> T create(T resource) {
		return create(resource, resource.getNamespace());
	}
	
	@Override
	public <T extends IResource> T create(T resource, String namespace) {
		if(ResourceKind.LIST.equals(resource.getKind())) throw new UnsupportedOperationException("Generic create operation not supported for resource type 'List'");
		try {
			namespace = ResourceKind.PROJECT.equals(resource.getKind()) ? "" : namespace;
			final URL endpoint = new URLBuilder(this.baseUrl, getTypeMappings())
				.kind(resource.getKind())
				.namespace(namespace)
				.build();
			String response = client.post(endpoint,  IHttpClient.DEFAULT_READ_TIMEOUT, resource);
			LOGGER.debug(response);
			return factory.create(response);
		} catch (HttpClientException e){
			throw handleHttpClientException("Exception creating the resource", e);
		} catch (SocketTimeoutException e) {
			throw new OpenShiftException(e, "SocketTimeout creating resource %", resource.getName());
		}
	}
	
	@Override
	public <T extends IResource> T update(T resource) {
		if(ResourceKind.LIST.equals(resource.getKind())) throw new UnsupportedOperationException("Update operation not supported for resource type 'List'");
		try {
			final URL endpoint = new URLBuilder(getBaseURL(), getTypeMappings())
				.resource(resource)
				.namespace(resource.getNamespace())
				.build();
			String response = client.put(endpoint, IHttpClient.DEFAULT_READ_TIMEOUT, resource);
			LOGGER.debug(response);
			return factory.create(response);
		} catch (HttpClientException e){
			throw handleHttpClientException("Exception updating the resource", e);
		} catch (SocketTimeoutException e) {
			throw new OpenShiftException(e, "SocketTimeout updating resource %", resource.getName());
		}
	}

	@Override
	public <T extends IResource> void delete(T resource) {
		if(ResourceKind.LIST.equals(resource.getKind())) throw new UnsupportedOperationException("Delete operation not supported for resource type 'List'");
		try {
			String namespace = ResourceKind.PROJECT.equals(resource.getKind()) ? "" : resource.getNamespace();
			final URL endpoint = new URLBuilder(this.baseUrl, getTypeMappings())
				.resource(resource)
				.namespace(namespace)
				.build();
			LOGGER.debug(String.format("Deleting resource: %s", endpoint));
			String response = client.delete(endpoint,  IHttpClient.DEFAULT_READ_TIMEOUT);
			LOGGER.debug(response);
			//TODO return response object here
		} catch (HttpClientException e){
			throw handleHttpClientException("Exception deleting the resource", e);
		} catch (SocketTimeoutException e) {
			throw new OpenShiftException(e, "SocketTimeout deleting resource %", resource.getName());
		} 
	}

	@Override
	public <T extends IResource> T get(String kind, String name, String namespace) {
		try {
			namespace = ResourceKind.PROJECT.equals(kind) ? "" : namespace;
			final URL endpoint = new URLBuilder(this.baseUrl, getTypeMappings())
				.kind(kind)
				.name(name)
				.namespace(namespace)
				.build();
			String response = client.get(endpoint, IHttpClient.DEFAULT_READ_TIMEOUT);
			LOGGER.debug(response);
			return factory.create(response);
		} catch (HttpClientException e){
			throw handleHttpClientException("Exception getting the resource", e);
		} catch (SocketTimeoutException e) {
			throw new OpenShiftException(e, "SocketTimeout getting resource %", name);
		} 
	}

	public synchronized void initializeCapabilities(){
		if(capabilitiesInitialized) return;
		initializeClientCapabilities(capabilities, this);
		capabilitiesInitialized = true;
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
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ICapability, R> R accept(CapabilityVisitor<T, R> visitor, R unsupportedCapabililityValue){
		if(!capabilitiesInitialized) initializeCapabilities();
		if(capabilities.containsKey(visitor.getCapabilityType())){
			T capability = (T) capabilities.get(visitor.getCapabilityType());
			return (R) visitor.visit(capability);
		}
		return unsupportedCapabililityValue;
	}

	public List<KubernetesAPIVersion> getKubernetesVersions() {
		return getVersion(KubernetesAPIVersion.class, apiEndpoint);
	}

	public List<OpenShiftAPIVersion> getOpenShiftVersions() {
		return getVersion(OpenShiftAPIVersion.class, osApiEndpoint);
	}

	public String getKubernetesVersion() {
		if(kubernetesVersion == null){
			List<KubernetesAPIVersion> versions = getKubernetesVersions();
			kubernetesVersion = ResourcePropertiesRegistry.getInstance().getMaxSupportedKubernetesVersion(versions).toString();
		}
		return kubernetesVersion; 
	}

	@Override
	public String getOpenShiftAPIVersion() {
		if(openShiftVersion == null){
			List<OpenShiftAPIVersion> versions = getOpenShiftVersions();
			openShiftVersion = ResourcePropertiesRegistry.getInstance().getMaxSupportedOpenShiftVersion(versions).toString();
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
		} catch (MalformedURLException e) {
			LOGGER.error("Exception", e);
			throw new OpenShiftException(e,"");
		} catch (SocketTimeoutException e) {
			LOGGER.error("Exception", e);
			throw new OpenShiftException(e,"");
		}
	}

	private Map<String, String> getTypeMappings(){
		if(typeMappings.isEmpty()){
			//OpenShift endpoints
			final String osEndpoint = String.format("%s/%s", osApiEndpoint, getOpenShiftAPIVersion());
			typeMappings.put(ResourceKind.BUILD, osEndpoint);
			typeMappings.put(ResourceKind.BUILD_CONFIG, osEndpoint);
			typeMappings.put(ResourceKind.DEPLOYMENT_CONFIG, osEndpoint);
			typeMappings.put(ResourceKind.IMAGE_STREAM, osEndpoint);
			typeMappings.put(ResourceKind.OAUTH_ACCESS_TOKEN, osEndpoint);
			typeMappings.put(ResourceKind.OAUTH_AUTHORIZE_TOKEN, osEndpoint);
			typeMappings.put(ResourceKind.OAUTH_CLIENT, osEndpoint);
			typeMappings.put(ResourceKind.OAUTH_CLIENT_AUTHORIZATION, osEndpoint);
			typeMappings.put(ResourceKind.POLICY, osEndpoint);
			typeMappings.put(ResourceKind.POLICY_BINDING, osEndpoint);
			typeMappings.put(ResourceKind.PROJECT, osEndpoint);
			typeMappings.put(ResourceKind.PROJECT_REQUEST, osEndpoint);
			typeMappings.put(ResourceKind.ROLE, osEndpoint);
			typeMappings.put(ResourceKind.ROLE_BINDING, osEndpoint);
			typeMappings.put(ResourceKind.ROUTE, osEndpoint);
			typeMappings.put(ResourceKind.TEMPLATE, osEndpoint);
			typeMappings.put(ResourceKind.USER, osEndpoint);
			//not real kinds
			typeMappings.put(ResourceKind.TEMPLATE_CONFIG, osEndpoint);
			typeMappings.put(ResourceKind.PROCESSED_TEMPLATES, osEndpoint);
			
			//Kubernetes endpoints
			final String k8eEndpoint = String.format("%s/%s", apiEndpoint, getKubernetesVersion());
			typeMappings.put(ResourceKind.EVENT, k8eEndpoint);
			typeMappings.put(ResourceKind.POD, k8eEndpoint);
			typeMappings.put(ResourceKind.LIMIT_RANGE, k8eEndpoint);
			typeMappings.put(ResourceKind.REPLICATION_CONTROLLER, k8eEndpoint);
			typeMappings.put(ResourceKind.RESOURCE_QUOTA, k8eEndpoint);
			typeMappings.put(ResourceKind.SERVICE, k8eEndpoint);
			typeMappings.put(ResourceKind.SECRET, k8eEndpoint);
		}
		return typeMappings;
	}

	@Override
	public URL getBaseURL() {
		return this.baseUrl;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setAuthorizationStrategy(IAuthorizationStrategy strategy) {
		this.strategy = strategy;
		this.client.setAuthorizationStrategy(strategy);
	}
	
	@Override
	public IAuthorizationStrategy getAuthorizationStrategy() {
		return this.strategy;
	}

	private OpenShiftException handleHttpClientException(String message, HttpClientException e) {
		LOGGER.debug(message, e);
		if (e.getMessage().startsWith("{")) {
			Status status = factory.create(e.getMessage());
			if(status.getCode() == STATUS_FORBIDDEN) {
				return new ResourceForbiddenException(status.getMessage(), e);
			}
			return new OpenShiftException(e, status, message);
		} else {
			if(e instanceof UnauthorizedException) {
				return new com.openshift.restclient.authorization.UnauthorizedException(authClient.getAuthorizationDetails(this.baseUrl.toString()));
			}
			return new OpenShiftException(e, message);
		}
	}

	@Override
	public IUser getCurrentUser() {
		return get(ResourceKind.USER, "~", "");
	}

	@Override
	public IAuthorizationContext getContext(String baseURL) {
		return this.authClient.getContext(baseURL);
	}

	@Override
	public IAuthorizationDetails getAuthorizationDetails(String baseURL) {
		return this.authClient.getAuthorizationDetails(baseURL);
	}

	@Override
	public void setSSLCertificateCallback(ISSLCertificateCallback callback) {
		this.authClient.setSSLCertificateCallback(callback);
	}
	
	
}
