package com.openshift.kube;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.client.IHttpClient;
import com.openshift.internal.client.httpclient.HttpClientException;
import com.openshift.internal.client.httpclient.UrlConnectionHttpClientBuilder;
import com.openshift.internal.kube.Resource;
import com.openshift.internal.kube.ResourceFactory;
import com.openshift.kube.capability.Capability;
import com.openshift.kube.capability.CapabilityInitializer;

public class OpenShiftKubeClient implements Client{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenShiftKubeClient.class);
	private URL baseUrl;
	private IHttpClient client;
	private ResourceFactory factory;
	private Map<Class<? extends Capability>, Capability> capabilities = new HashMap<Class<? extends Capability>, Capability>();
	
	private static final String apiEndpoint = "api/v1beta1";
	private static final String osApiEndpoint = "osapi/v1beta1";
	
	private static final Map<ResourceKind, String> TYPE_MAPPING = new HashMap<ResourceKind, String>();
	
	static {
		//OpenShift endpoints
		TYPE_MAPPING.put(ResourceKind.Build, osApiEndpoint);
		TYPE_MAPPING.put(ResourceKind.BuildConfig, osApiEndpoint);
		TYPE_MAPPING.put(ResourceKind.Deployment, osApiEndpoint);
		TYPE_MAPPING.put(ResourceKind.DeploymentConfig, osApiEndpoint);
		TYPE_MAPPING.put(ResourceKind.ImageRepository, osApiEndpoint);
		TYPE_MAPPING.put(ResourceKind.Project, osApiEndpoint);
		
		//Kubernetes endpoints
		TYPE_MAPPING.put(ResourceKind.Pod, apiEndpoint);
		TYPE_MAPPING.put(ResourceKind.Service, apiEndpoint);
		
	}

	public OpenShiftKubeClient(URL baseUrl){
		this.baseUrl = baseUrl;
		client = new UrlConnectionHttpClientBuilder()
			.setAcceptMediaType("application/json")
			.client();
		factory = new ResourceFactory(this);
	}
	
	@Override
	public <T extends Resource> List<T> list(ResourceKind kind) {
		return list(kind,""); //assumes namespace=default
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Resource> List<T> list(ResourceKind kind, String namespace) {
		if(!TYPE_MAPPING.containsKey(kind))
			throw new RuntimeException("No OpenShift resource endpoint for type: " + kind);
		try {
			UrlBuilder builder = new UrlBuilder(this.baseUrl)
				.kind(kind);
			if(ResourceKind.Project == kind){
				Project p = new Project();
				p.setName(namespace);
				builder.resource(p);
			}else{
				if(namespace.trim().length() > 0){
					builder.addParmeter("namespace", namespace);
				}
			}
			final URL endpoint = builder.build();
			String response = client.get(endpoint,  IHttpClient.DEFAULT_READ_TIMEOUT);
			LOGGER.debug(response);
			return (List<T>) factory.createList(response, kind);
		} catch (HttpClientException e){
			throw new OpenShiftKubeException("Exception listing the resources", e, factory.<Status>create(e.getMessage()));
		} catch (Exception e) {
			LOGGER.error("Exception", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T extends Resource> T create(T resource) {
		try {
			final URL endpoint = new UrlBuilder(this.baseUrl)
				.kind(resource.getKind())
				.addParmeter("namespace", resource.getNamespace())
				.build();
			String response = client.post(endpoint,  IHttpClient.DEFAULT_READ_TIMEOUT, resource);
			LOGGER.debug(response);
			return factory.create(response);
		} catch (HttpClientException e){
			throw new OpenShiftKubeException("Exception creating the resource", e, factory.<Status>create(e.getMessage()));
		} catch (Exception e) {
			LOGGER.error("Exception", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T extends Resource> void delete(T resource) {
		try {
			final URL endpoint = new UrlBuilder(this.baseUrl)
				.resource(resource)
				.addParmeter("namespace", resource.getNamespace())
				.build();
			String response = client.delete(endpoint,  IHttpClient.DEFAULT_READ_TIMEOUT);
			LOGGER.debug(response);
			//TODO return response object here
		} catch (HttpClientException e){
			throw new OpenShiftKubeException("Exception deleting the resource", e, factory.<Status>create(e.getMessage()));
		} catch (Exception e) {
			LOGGER.error("Exception", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T extends Resource> T get(ResourceKind kind, String name, String namespace) {
		try {
			final URL endpoint = new UrlBuilder(this.baseUrl)
				.kind(kind)
				.name(name)
				.addParmeter("namespace", namespace)
				.build();
			String response = client.get(endpoint, IHttpClient.DEFAULT_READ_TIMEOUT);
			LOGGER.debug(response);
			return factory.create(response);
		} catch (HttpClientException e){
			throw new OpenShiftKubeException("Exception getting the resource", e, factory.<Status>create(e.getMessage()));
		} catch (Exception e) {
			LOGGER.error("Exception", e);
			throw new RuntimeException(e);
		}
	}

	public void initializeCapabilities(){
		new CapabilityInitializer().populate(capabilities, this);
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
		return capabilities.containsKey(capability);
	}

	class UrlBuilder {
		
		private String baseUrl;
		private ResourceKind kind;
		private Resource resource;
		private String name;
		private Map<String, String> params = new HashMap<String, String>();

		UrlBuilder(URL baseUrl){
			this.baseUrl = baseUrl.toString();
		}
		
		public UrlBuilder name(String name) {
			this.name = name;
			return this;
		}

		UrlBuilder kind(ResourceKind kind){
			this.kind = kind;
			return this;
		}
		UrlBuilder resource(Resource resource){
			this.resource = resource;
			return this;
		}
		
		UrlBuilder addParmeter(String key, String value){
			params.put(key, value);
			return this;
		}
		
		URL build(){
			StringBuilder url = new StringBuilder(baseUrl);
			this.kind = resource != null ? resource.getKind() : kind;
			if(kind == null) throw new RuntimeException("Unable to build a URL because the ResourceKind is unknown");
			url.append("/")
				.append(TYPE_MAPPING.get(kind))
				.append("/")
				.append(kind.pluralize());
			if(resource != null){
				url.append("/").append(resource.getName());
			}else if(name != null){
				url.append("/").append(name);
			}	
			url = appendParameters(url);
			try {
				LOGGER.debug(String.format("Built url: %s", url.toString()));
				return new URL(url.toString());
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
		
		private StringBuilder appendParameters(StringBuilder url){
			if(!params.isEmpty()){
				url.append(IHttpClient.QUESTION_MARK);
				for (Iterator<Entry<String, String>> iterator = params.entrySet().iterator(); iterator.hasNext();) {
					Entry<String, String> entry = (Entry<String, String>) iterator.next();
					try {
						url.append(entry.getKey())
							.append(IHttpClient.EQUALS)
							.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						throw new RuntimeException(e);
					}
					if(iterator.hasNext()){
						url.append(url.append(IHttpClient.AMPERSAND));
					}
				}
			}
			return url;
		}
	}

}
