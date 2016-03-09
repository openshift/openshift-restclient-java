/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.capability.resources;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.jboss.dmr.ModelNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.model.ModelNodeBuilder;
import com.openshift.internal.restclient.model.image.ImageStreamImport;
import com.openshift.internal.restclient.model.properties.ResourcePropertyKeys;
import com.openshift.internal.util.JBossDmrExtentions;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.resources.IImageStreamImportCapability;
import com.openshift.restclient.http.IHttpConstants;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.image.IImageStreamImport;

/**
 * Retrieve metadata directly from docker.
 * @author jeff.cantrill
 *
 */
public class DockerRegistryImageStreamImportCapability implements IImageStreamImportCapability, IHttpConstants, ResourcePropertyKeys {
	
	private static final String TOKEN = "token";
	private static final String STATUS_STATUS = "status.status";
	private static final int TIMEOUT = 10 * 1000; //10 seconds
	private static final String ID = "id";
	private static final String PARENT = "parent";
	private static final String REALM = "realm";
	private static final Logger LOG = LoggerFactory.getLogger(IImageStreamImportCapability.class);
	private static final String DEFAULT_DOCKER_REGISTRY = "https://registry-1.docker.io/v2/";
	private IResourceFactory factory;
	private IProject project;

	public DockerRegistryImageStreamImportCapability(IProject project, IResourceFactory factory) {
		this.factory = factory;
		this.project = project;
	}
	
	@Override
	public boolean isSupported() {
		return true;
	}

	@Override
	public String getName() {
		return DockerRegistryImageStreamImportCapability.class.getSimpleName();
	}
	
	private boolean registryExists(HttpClient client) throws Exception{
		ContentResponse response = client.newRequest(DEFAULT_DOCKER_REGISTRY).send();
		if(response == null) return false;
		return (response.getStatus() == STATUS_UNAUTHORIZED || response.getStatus() == STATUS_OK);
	}
	
	/**
	 * @return the token required to pull docker metadata
	 */
	private String retrieveAuthToken(HttpClient client, String details) throws Exception {
		if(StringUtils.isNotBlank(details)) {
			Map<String, String> auth = parseAuthDetails(details);
			if(auth.containsKey(REALM)) {
				Request request = createAuthRequest(client, auth);
				ContentResponse response = request.send();
				LOG.debug("Auth response: " + response.toString());
				if(response.getStatus() == STATUS_OK && response.getHeaders().contains(PROPERTY_CONTENT_TYPE, MEDIATYPE_APPLICATION_JSON)) {
						ModelNode tokenNode = ModelNode.fromJSONString(response.getContentAsString());
						if(tokenNode.hasDefined(TOKEN)) {
							return tokenNode.get(TOKEN).asString();
						}else {
							LOG.debug("No auth token was found on auth response: " + tokenNode.toJSONString(false));
						}
				} else {
					LOG.info("Unable to retrieve authentication token as response was not OK and/or unexpected content type");
				}	
			}else {
				LOG.info("Unable to retrieve authentication token - 'realm' was not found in the authenticate header: " + auth.toString());
			}
		}
		return null;
	}
	
	private Request createAuthRequest(HttpClient client, Map<String, String> authParams) {
		Request request = client.newRequest(StringUtils.strip(authParams.get(REALM),"\""));
		for (Entry<String, String> e: authParams.entrySet()) {
			if(!REALM.equals(e.getKey())) {
				request.param(StringUtils.strip(e.getKey(),"\""), StringUtils.strip(e.getValue(),"\""));
			}
		}
		LOG.debug("Auth request uri: " + request.getURI());
		return request;
	}
	
	private Map<String, String> parseAuthDetails(String auth){
		LOG.debug("Auth details header: " + auth);
		Map<String, String> map = new HashMap<>();
		String [] authAndValues = auth.split(" ");
		if(authAndValues.length == 2 && AUTHORIZATION_BEARER.equals(authAndValues[0])) {
			String [] params = authAndValues[1].split(",");
			for (String p : params) {
				String[] knv = p.split("=");
				if(knv.length >= 2 ) {
					map.put(knv[0], knv[1]);
				}
			}
		}
		return map;
	}
	
	private DockerResponse retrieveMetaData(HttpClient client, String token, DockerImageURI uri) throws Exception {
		String regUri = String.format("%s/%s/%s/manifests/%s", 
					DEFAULT_DOCKER_REGISTRY,
					StringUtils.defaultIfBlank(uri.getUserName(),"library"),
					uri.getName(),
					uri.getTag());
		Request request = client.newRequest(regUri);
		if(token != null) {
			request.header(PROPERTY_AUTHORIZATION, String.format("%s %s", AUTHORIZATION_BEARER, token));
			
		}
		ContentResponse response = request.send();
		switch(response.getStatus()) {
		case STATUS_OK:
			return new DockerResponse(DockerResponse.DATA, response.getContentAsString());
		case STATUS_UNAUTHORIZED:
			return new DockerResponse(DockerResponse.AUTH, response.getHeaders().get(HttpHeader.WWW_AUTHENTICATE));
		}
		LOG.info("Unable to retrieve docker meta data: " + response.toString());
		return null;
	}
	
	private static class DockerResponse {
		public static final String DATA = "data";
		public static final String AUTH = "auth";
		String responseType;
		String data;
		DockerResponse(String responseType, String data){
			this.responseType = responseType;
			this.data = data;
		}
		public Object getResponseType() {
			return responseType;
		}
		public String getData() {
			return data;
		}
	}

	@Override
	public IImageStreamImport importImageMetadata(DockerImageURI uri) {
		
		HttpClient client = null;
		try {
			SslContextFactory sslFactory = new SslContextFactory(true);
			client = new HttpClient(sslFactory);
			client.setConnectTimeout(TIMEOUT);
			client.start();
			if(registryExists(client)) {
				String token = null;
				DockerResponse response = retrieveMetaData(client, token, uri);
				if(DockerResponse.AUTH.equals(response.getResponseType())){
					LOG.debug("Unauthorized.  Trying to retrieve token...");
					token = retrieveAuthToken(client, response.getData());
					response = retrieveMetaData(client, token, uri);
				}
				if(DockerResponse.DATA.equals(response.getResponseType())) {
					String meta = response.getData();
					LOG.debug("Raw Docker image metadata: " + meta);
					return buildResponse(meta, uri);
				}else {
					LOG.info("Unable to retrieve image metadata from docker registry");
					return buildErrorResponse(uri);
				}
			}
		} catch (Exception e) {
			LOG.error("Exception while trying to retrieve image metadata from docker", e);
		}finally {
			try {
				if(client != null) {
					client.stop();
				}
			} catch (Exception e) {
				LOG.warn("Exception while trying to stop http client", e);
			}
		}
		return buildErrorResponse(uri);
	}

	private IImageStreamImport buildErrorResponse(DockerImageURI uri) {
		ModelNodeBuilder builder = new ModelNodeBuilder()
				.set(STATUS_STATUS, "Failure")
				.set("status.message", String.format("you may not have access to the Docker image \"%s\"", uri.getUriWithoutHost()))
				.set("status.reason", "Unauthorized")
				.set("status.code", IHttpConstants.STATUS_UNAUTHORIZED);
		
		return buildImageStreamImport(uri, builder.build());
	}

	private IImageStreamImport buildResponse(String meta, DockerImageURI uri) {
		ModelNode raw = ModelNode.fromJSONString(meta);
		ModelNode last = findNewestHistoryEntry(raw);
		ModelNode containerConfig = last.remove("container_config");
		last.get("ContainerConfig").set(containerConfig);
		
		ModelNodeBuilder builder = new ModelNodeBuilder()
			.set(STATUS_STATUS, "Success")
			.set("tag", uri.getTag())
			.set("image.metadata.name", uri.getName())
			.set(ImageStreamImport.IMAGE_DOCKER_IMAGE_REFERENCE,uri.getUriUserNameAndName())
			.set("image.dockerImageMetadata", last);

		return buildImageStreamImport(uri, builder.build());
	}
	
	private ImageStreamImport buildImageStreamImport(DockerImageURI uri, ModelNode node) {
		ImageStreamImport isImport = (ImageStreamImport) factory.stub(ResourceKind.IMAGE_STREAM_IMPORT, uri.getName(), this.project.getName());
		ModelNode root = isImport.getNode();
		ModelNode images = JBossDmrExtentions.get(root,null, ImageStreamImport.STATUS_IMAGES);
		images.add(node);
		
		return isImport;
	}
	
	private ModelNode findNewestHistoryEntry(ModelNode root) {
		ModelNode history = root.get("history");
		List<ModelNode> entries = history.asList().stream().map(n->ModelNode.fromJSONString(n.get("v1Compatibility").asString())).collect(Collectors.toList());
		entries.sort(new Comparator<ModelNode>() {

			@Override
			public int compare(ModelNode one, ModelNode two) {
				String parent1 = one.has(PARENT ) ? one.get(PARENT).asString() : null;
				String parent2 = two.has(PARENT ) ? one.get(PARENT).asString() : null;
				if(parent1 == null && parent2 != null) {
					return -1;
				}else if(parent1 != null && parent2 == null) {
					return 1;
				}else if(parent1 == null && parent2 == null) {
					return 0; //we should never get here
				}
				String id1 = one.get(ID).asString();
				String id2 = two.get(ID).asString();
				
				if(parent2.equals(id1)) {
					return -1;
				}else if(parent1.equals(id2)) {
					return 1;
				}
				
				return 0; //we should never get here
			}
		});
		
		ModelNode last = entries.get(0);
		LOG.debug("newest history: " + last.toJSONString(false));
		return last;
	}

}
