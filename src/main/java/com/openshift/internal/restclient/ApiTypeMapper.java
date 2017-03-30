/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.restclient;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.jboss.dmr.ModelNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.model.properties.ResourcePropertyKeys;
import com.openshift.internal.util.JBossDmrExtentions;
import com.openshift.restclient.IApiTypeMapper;
import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.UnsupportedEndpointException;
import com.openshift.restclient.model.IResource;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Typemapper to determine the endpoints for
 * various openshift resources
 * @author jeff.cantrill
 *
 */
public class ApiTypeMapper implements IApiTypeMapper, ResourcePropertyKeys{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ApiTypeMapper.class);
	private final String baseUrl;
	private final OkHttpClient client;
	private List<VersionedApiResource> resourceEndpoints;
	private final Map<String, String> preferedVersion = new HashMap<>(2);
	private boolean initialized = false;
	
	public ApiTypeMapper(String baseUrl, OkHttpClient client) {
		this.baseUrl = baseUrl;
		this.client = client;
		preferedVersion.put(KUBE_API, KubernetesAPIVersion.v1.toString());
		preferedVersion.put(OS_API, OpenShiftAPIVersion.v1.toString());
	}

	@Override
	public String getPreferedVersionFor(String endpoint) {
		return preferedVersion.get(endpoint);
	}

	@Override
	public boolean isSupported(IResource resource) {
		return isSupported(resource.getApiVersion(), resource.getKind());
	}
	
	@Override
	public IVersionedApiResource getEndpointFor(String apiVersion, String kind) {
		init();
		IVersionedApiResource apiresource = endpointFor(apiVersion, kind);
		if(apiresource == null) {
			throw new UnsupportedEndpointException("No endpoint found for %s, version %s", kind, apiVersion);
		}
		return apiresource;
	}
	
	private IVersionedApiResource endpointFor(String version, String kind) {
		String[] split = StringUtils.isBlank(version) ? new String [] {} : version.split(FWD_SLASH);
		Optional<IVersionedApiResource> result = null;
		if(split.length <=1) {
			result = Stream.of(KUBE_API, OS_API)
					.map(api->formatEndpointFor(api, (split.length == 0 ? preferedVersion.get(api) : split[0]), kind))
					.filter(e->resourceEndpoints.contains(e))
					.findFirst();
		}else{
			result = Optional.of(formatEndpointFor(API_GROUPS_API, version, ResourceKind.pluralize(kind, true, true)));
		}
		if(result.isPresent()) {
			int index = resourceEndpoints.indexOf(result.get());
			if(index > -1) {
				return resourceEndpoints.get(index);
			}
		}
		return null;
	}

	@Override
	public boolean isSupported(String kind) {
		return isSupported(null, kind);
	}

	@Override
	public boolean isSupported(String version, String kind) {
		init();
		return endpointFor(version, kind) != null;
	}
	
	private IVersionedApiResource formatEndpointFor(String prefix, String version, String kind) {
		return new VersionedApiResource(prefix, version, ResourceKind.pluralize(kind, true, true));
	}
	
	private synchronized void init() {
		if(!this.initialized) {
			List<VersionedApiResource> resourceEndpoints = new ArrayList<>();
			Collection<ApiGroup> groups = getLegacyGroups();
			groups.addAll(getApiGroups());
			groups.forEach(g->{
				Collection<String> versions = g.getVersions();
				versions.forEach(v->{
					Collection<ModelNode> resources = getResources(g, v);
					addEndpoints(resourceEndpoints, g.getPrefix(), g.getName(), v, resources);
				});
			});
			this.resourceEndpoints = resourceEndpoints;
			this.initialized = true;
		}
	}
	
	private void addEndpoints(List<VersionedApiResource> endpoints, final String prefix, final String apiGroupName, final String version, final Collection<ModelNode> nodes) {
		for (ModelNode node : nodes) {
			String name = node.get(NAME).asString();
			String capability = null;
			if(name.contains(FWD_SLASH)) {
				int first = name.indexOf(FWD_SLASH);
				capability = name.substring(first+1);
				name = name.substring(0, first);
			}
			boolean namespaced = node.get("namespaced").asBoolean();
			VersionedApiResource resource = new VersionedApiResource(prefix, apiGroupName, version, name, node.get(KIND).asString(), namespaced);
			if(!endpoints.contains(resource)) {
				endpoints.add(resource);
			}
			if(capability != null) {
				int index = endpoints.indexOf(resource);
				endpoints.get(index).addCapability(capability);
			}
		}
	}

	private Collection<ApiGroup> getApiGroups(){
		String json = readEndpoint(API_GROUPS_API);
		return ModelNode.fromJSONString(json)
				.get("groups")
				.asList()
				.stream()
				.map(n->new ApiGroup(API_GROUPS_API, n))
				.collect(Collectors.toList());
	}
	
	private Collection<ModelNode> getResources(IApiGroup group, String version){
		String json = readEndpoint(group.pathFor(version));
		if(StringUtils.isBlank(json)) {
			return new ArrayList<>();
		}
		ModelNode node = ModelNode.fromJSONString(json);
		return node.get("resources").asList();
	}
	
	private Collection<ApiGroup> getLegacyGroups(){
		Collection<ApiGroup> groups = new ArrayList<>();
		for(String e: Arrays.asList(KUBE_API,OS_API)) {
			String json = readEndpoint(e);
			ModelNode n = ModelNode.fromJSONString(json);
			groups.add(new LegacyApiGroup(e,n));
		}
		return groups;
	}
	
	private String readEndpoint(final String endpoint) {
		try {
			final URL url = new URL(new URL(this.baseUrl), endpoint);
			LOGGER.debug(url.toString());
			Request request = new Request.Builder()
					.url(url)
					.build();
			Response response = client.newCall(request).execute();
			return response.body().string();
		} catch (IOException e) {
			throw new OpenShiftException(e,"Unable to read endpoint %s/%s", this.baseUrl, endpoint);
		} 
	}
	
	static class ApiGroup implements IApiGroup{
		private final ModelNode node;
		private final String prefix;
		private final String path;
		
		ApiGroup(String prefix, ModelNode node) {
			this.prefix = prefix;
			this.node = node;
			StringBuilder builder = new StringBuilder(prefix);
			if(getName() != null) { //null name for k8e or openshift
				builder
				.append(FWD_SLASH)
				.append(getName());
			}
			path = builder.toString();
		}
		
		protected ModelNode getNode() {
			return node;
		}
		
		@Override
		public String getPrefix() {
			return prefix;
		}

		@Override
		public String getName() {
			return JBossDmrExtentions.asString(node, new HashMap<>(), NAME);
		}
		
		@Override
		public Collection<String> getVersions() {
			return JBossDmrExtentions.get(node, new HashMap<>(), "versions").asList()
				.stream().map(n->n.get("version").asString())
				.collect(Collectors.toList());
		}
		
		@Override
		public String getPreferedVersion() {
			return JBossDmrExtentions.asString(node, new HashMap<>(), "preferedVersion.version");
		}
		
		@Override
		public String pathFor(String version) {
			//add check for supported version?
			return String.format("%s/%s", path, version);
		}
	}
	
	static class LegacyApiGroup extends ApiGroup{

		LegacyApiGroup(String prefix, ModelNode node) {
			super(prefix, node);
		}
		
		@Override
		public String getName() {
			return null;
		}

		@Override
		public Collection<String> getVersions() {
			return JBossDmrExtentions.get(getNode(), new HashMap<>(), "versions").asList()
				.stream().map(n->n.asString())
				.collect(Collectors.toList());
		}

		@Override
		public String getPreferedVersion() {
			return OpenShiftAPIVersion.v1.toString();
		}
		
		
	}
	
	static class VersionedApiResource implements IVersionedApiResource {
		
		private final String prefix;
		private final String name;
		private final boolean namespaced;
		private final Collection<String> capabilities = new ArrayList<>();
		private final String version;
		private String apiGroupName;
		private String kind;
		
		VersionedApiResource(String prefix, String version, String name){
			if(version == null) throw new IllegalArgumentException("version can not be null when creating a VersionedApiResource ");
			if(version.contains(FWD_SLASH)) {
				int last = version.lastIndexOf(FWD_SLASH);
				this.apiGroupName = version.substring(0, last);
				version = version.substring(last + 1);
			}
			this.prefix = prefix;
			this.name = name;
			this.version = version;
			this.namespaced = false;
		}
		
		VersionedApiResource(String prefix, String apiGroupName, String version, String name, String kind, boolean namespaced){
			this.prefix = prefix;
			this.name = name;
			this.namespaced = namespaced;
			this.version = version;
			this.apiGroupName = apiGroupName;
			this.kind = kind;
		}
		
		public void addCapability(String capability) {
			capabilities.add(capability);
		}
		
		@Override
		public String getApiGroupName() {
			return apiGroupName;
		}

		@Override
		public String getVersion() {
			return this.version;
		}

		@Override
		public String getPrefix() {
			return prefix;
		}

		@Override
		public String getName() {
			return name;
		}
		

		@Override
		public String getKind() {
			return kind;
		}

		@Override
		public boolean isNamespaced() {
			return namespaced;
		}

		@Override
		public boolean isSupported(String capability) {
			return capabilities.contains(capability);
		}
		
		@Override
		public String toString() {
		    if(apiGroupName == null) {
		        return String.format("%s/%s/%s", prefix, version, name);
		    }
			return String.format("%s/%s/%s/%s/%s", prefix, apiGroupName, version, name);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((apiGroupName == null) ? 0 : apiGroupName.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
			result = prime * result + ((version == null) ? 0 : version.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			VersionedApiResource other = (VersionedApiResource) obj;
			if (apiGroupName == null) {
				if (other.apiGroupName != null)
					return false;
			} else if (!apiGroupName.equals(other.apiGroupName))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (prefix == null) {
				if (other.prefix != null)
					return false;
			} else if (!prefix.equals(other.prefix))
				return false;
			if (version == null) {
				if (other.version != null)
					return false;
			} else if (!version.equals(other.version))
				return false;
			return true;
		}
		
	}
}
