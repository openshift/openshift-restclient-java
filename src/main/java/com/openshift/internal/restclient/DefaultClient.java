/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient;

import static java.util.stream.Collectors.joining;

import static com.openshift.internal.restclient.capability.CapabilityInitializer.initializeClientCapabilities;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.authorization.AuthorizationContext;
import com.openshift.internal.restclient.okhttp.WatchClient;
import com.openshift.restclient.IApiTypeMapper;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IOpenShiftWatchListener;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.IWatcher;
import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.UnsupportedOperationException;
import com.openshift.restclient.api.ITypeFactory;
import com.openshift.restclient.authorization.IAuthorizationContext;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.http.IHttpConstants;
import com.openshift.restclient.model.IList;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.JSONSerializeable;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author Jeff Cantrill
 */
public class DefaultClient implements IClient, IHttpConstants{
	
	public static final String SYSTEM_PROP_K8E_API_VERSION = "osjc.k8e.apiversion"; 
	public static final String SYSTEM_PROP_OPENSHIFT_API_VERSION = "osjc.openshift.apiversion"; 
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultClient.class);
	URL baseUrl;
	protected OkHttpClient client;
	protected IResourceFactory factory;
	protected Map<Class<? extends ICapability>, ICapability> capabilities = new HashMap<Class<? extends ICapability>, ICapability>();
	private boolean capabilitiesInitialized = false;

	private static final String OS_API_ENDPOINT = "oapi";

	private String openShiftVersion;
	private String kubernetesVersion;
	private AuthorizationContext authContext;
	IApiTypeMapper typeMapper;
	
	public DefaultClient(URL baseUrl,  OkHttpClient client, IResourceFactory factory, IApiTypeMapper typeMapper, AuthorizationContext authContext){
		this.baseUrl = baseUrl;
		this.client = client; 
		this.factory = factory;
		if(this.factory != null) {
			this.factory.setClient(this);
		}
		openShiftVersion = System.getProperty(SYSTEM_PROP_OPENSHIFT_API_VERSION, null);
		kubernetesVersion = System.getProperty(SYSTEM_PROP_K8E_API_VERSION, null);
		this.typeMapper = typeMapper != null ? typeMapper :  new ApiTypeMapper(baseUrl.toString(), client);
		this.authContext = authContext;
	}
	
	
	@Override
	public IClient clone() {
		AuthorizationContext context = authContext.clone();
		DefaultClient clone = new DefaultClient(baseUrl, client, factory, typeMapper, context);
		context.setClient(clone);
		return clone;
	}


	@Override
	public IResourceFactory getResourceFactory() {
		return factory;
	};
	
	@Override
	public IWatcher watch(String namespace, IOpenShiftWatchListener listener, String...kinds) {
		WatchClient watcher = new WatchClient(this, this.typeMapper, this.client);
		return watcher.watch(Arrays.asList(kinds), namespace, listener);
	}

	@Override
	public IWatcher watch(IOpenShiftWatchListener listener, String...kinds) {
		return this.watch("", listener, kinds);
	}

	@Override
	public String getResourceURI(IResource resource) {
		return new URLBuilder(getBaseURL(), typeMapper, resource).build().toString();
	}

	@Override
	public <T extends IResource> List<T> list(String kind) {
		return list(kind,"");
	}

	@Override
	public <T extends IResource> List<T> list(String kind,  Map<String, String> labels) {
		return list(kind,"", labels);
	}


	@Override
	public <T extends IResource> List<T> list(String kind, String namespace) {
		return list(kind, namespace, new HashMap<>());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends IResource> List<T> list(String kind, String namespace, Map<String, String> labels) {

		String labelQuery="";
		if(labels != null && !labels.isEmpty()){
			 labelQuery = labels.entrySet().stream()
					.map(e -> e.getKey() + "=" + e.getValue())
					.collect(joining(","));
		}
		return list(kind, namespace, labelQuery);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends IResource> List<T> list(String kind, String namespace, String labelQuery) {

		Map<String, String> params = new HashMap<>();
		if(labelQuery != null && !labelQuery.isEmpty()){
			params.put("labelSelector", labelQuery);
		}

		IList resources = execute(HttpMethod.GET.toString(), kind, namespace, null, null, null, params);
		List<T> items = new ArrayList<>();
		items.addAll((Collection<? extends T>) resources.getItems());
		return items;
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
		return execute(HttpMethod.POST, resource.getKind(), namespace, null, null, resource);
	}

	@Override
	public <T extends IResource> T create(String kind, String namespace, String name, String subresource, IResource payload) {
		return execute(HttpMethod.POST, kind, namespace, name, subresource, payload);
	}
	
	enum HttpMethod{
		GET,
		PUT,
		POST,
		DELETE
	}
	
	private <T extends IResource> T execute(HttpMethod method, String kind, String namespace, String name, String subresource, IResource payload) {
		return execute(method.toString(), kind, namespace, name, subresource, payload);
	}

	@SuppressWarnings("unchecked")
	public <T extends IResource> T execute(String method, String kind, String namespace, String name, String subresource, IResource payload, String subContext) {
		return (T) execute(this.factory, method, kind, namespace, name, subresource, subContext, payload,
				Collections.emptyMap());
	}	
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends IResource> T execute(String method, String kind, String namespace, String name, String subresource, IResource payload) {
		return (T) execute(this.factory, method, kind, namespace, name, subresource, null, payload,
				Collections.emptyMap());
	}

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IResource> T execute(String method, String kind, String namespace, String name, String subresource, IResource payload, Map<String, String> params) {
		return (T) execute(this.factory, method, kind, namespace, name, subresource, null, payload,params);
    }
	@SuppressWarnings("unchecked")
	public <T> T execute(ITypeFactory factory, String method, String kind, String namespace, String name,
        String subresource, String subContext, JSONSerializeable payload, Map<String, String> params) {
		if(factory == null) {
			throw new OpenShiftException("ITypeFactory is null while trying to call IClient#execute");
		}

		if(params == null){
			params = Collections.emptyMap();
		}

		if(ResourceKind.LIST.equals(kind)) 
			throw new UnsupportedOperationException("Generic create operation not supported for resource type 'List'");
		final URL endpoint = new URLBuilder(this.baseUrl, typeMapper)
				.kind(kind)
				.name(name)
				.namespace(namespace)
				.subresource(subresource)
				.subContext(subContext)
 				.addParameters(params)
				.build();
			
		try {
			Request request = newRequestBuilderTo(endpoint.toString())
					.method(method, getPayload(method, payload))
					.build();
			LOGGER.debug("About to make {} request: {}", request.method(), request);
			try(Response result = client.newCall(request).execute()){
				String response =  result.body().string();
				LOGGER.debug("Response: {}", response);
				return (T) factory.createInstanceFrom(response);
			}
		} catch (IOException e){
			throw new OpenShiftException(e, "Unable to execute request to %s", endpoint);
		}
	}
	
	RequestBody getPayload(String method, JSONSerializeable payload) {
		switch(method.toUpperCase()){
			case "GET":
			case "DELETE":
				return null;
			default:
				String json = payload == null ? "" : payload.toJson(true);
				LOGGER.debug("About to send payload: {}", json);
				return RequestBody.create(MediaType.parse(MEDIATYPE_APPLICATION_JSON), json);
		}
	}
	

	@Override
	public String getServerReadyStatus() {
		try {
			Request request = new Request.Builder()
					.url(new URL(this.baseUrl, "healthz/ready"))
					.header(PROPERTY_ACCEPT, "*/*")
					.build();
			try(Response response = client.newCall(request).execute()){
				return response.body().string();
			}
		} catch (IOException e) {
			throw new OpenShiftException(e, "Exception while trying to determine the health/ready response of the server");
		}
	}

	public Request.Builder newRequestBuilderTo(String endpoint){
		return newRequestBuilderTo(endpoint, MEDIATYPE_APPLICATION_JSON);
	}

	public Request.Builder newRequestBuilderTo(String endpoint,String acceptMediaType){
		Request.Builder builder = new Request.Builder()
				.url(endpoint.toString())
				.header(PROPERTY_ACCEPT, acceptMediaType);

		String token =  null;
		if(this.authContext != null &&  StringUtils.isNotBlank(this.authContext.getToken())){
			token = this.authContext.getToken();
		}
		builder.header(IHttpConstants.PROPERTY_AUTHORIZATION, String.format("%s %s", IHttpConstants.AUTHORIZATION_BEARER, token));
		return builder;
	}


	@Override
	public <T extends IResource> T update(T resource) {
		return execute(HttpMethod.PUT, resource.getKind(), resource.getNamespace(), resource.getName(), null, resource);
	}

	@Override
	public <T extends IResource> void delete(T resource) {
		execute(HttpMethod.DELETE, resource.getKind(), resource.getNamespace(), resource.getName(), null, resource);
	}


	@Override
	public IList get(String kind, String namespace) {
		return execute(HttpMethod.GET, kind, namespace, null, null, null);
	}


	@Override
	public <T extends IResource> T get(String kind, String name, String namespace) {
		return execute(HttpMethod.GET, kind, namespace, name, null, null);
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

	@Override
	public String getOpenShiftAPIVersion() {
		return typeMapper.getPreferedVersionFor(OS_API_ENDPOINT);
	}
	
	@Override
	public URL getBaseURL() {
		return this.baseUrl;
	}

	@Override
	public IAuthorizationContext getAuthorizationContext() {
		return this.authContext;
	}
	
	public void setToken(String token) {
		this.authContext.setToken(token);
	}
	
	public String getToken() {
		return getAuthorizationContext().getToken();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((baseUrl == null) ? 0 : baseUrl.toString().hashCode());
		result = prime * result + ((kubernetesVersion == null) ? 0 : kubernetesVersion.hashCode());
		result = prime * result + ((openShiftVersion == null) ? 0 : openShiftVersion.hashCode());
		result = prime * result + ((authContext == null || authContext.getToken() == null) ? 0 : authContext.getToken().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DefaultClient))
			return false;
		DefaultClient other = (DefaultClient) obj;
		if (baseUrl == null) {
			if (other.baseUrl != null)
				return false;
		} else if (!baseUrl.toString().equals(other.baseUrl.toString()))
			return false;
		if (kubernetesVersion == null) {
			if (other.kubernetesVersion != null)
				return false;
		} else if (!kubernetesVersion.equals(other.kubernetesVersion))
			return false;
		if (openShiftVersion == null) {
			if (other.openShiftVersion != null)
				return false;
		} else if (!openShiftVersion.equals(other.openShiftVersion)) {
			return false;
		}
		if (authContext == null) {
			return other.authContext == null;
		} else {
			if (other.authContext == null) {
				return false;
			}
			return ObjectUtils.equals(authContext.getUserName(), other.authContext.getUserName());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T adapt(Class<T> klass) {
		if(DefaultClient.class.equals(klass)){
			return (T) this;
		}
		if(OkHttpClient.class.equals(klass)) {
			return (T) this.client;
		}
		if(IApiTypeMapper.class.equals(klass)) {
			return (T)this.typeMapper;
		}
		if(ICapability.class.isAssignableFrom(klass) && this.supports((Class<? extends ICapability>) klass)) {
			return (T) getCapability((Class<? extends ICapability>)klass);
		}
		return null;
	}
	
	
	
}
