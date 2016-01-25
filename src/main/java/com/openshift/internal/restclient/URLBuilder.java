/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.http.IHttpConstants;
import com.openshift.restclient.model.IResource;

/**
 * Helper class to build the URL connection string in the proper
 * format
 * 
 * @author Jeff Cantrill
 */
public class URLBuilder {
	
	private static final Logger LOG = LoggerFactory.getLogger(URLBuilder.class);
	
	private String baseUrl;
	private String kind;
	private String name;
	private Map<String, String> params = new HashMap<String, String>();
	private final Map<String, String> typeMappings;

	private String namespace;
	private String subResource;

	/**
	 * 
	 * @param baseUrl
	 * @param typeMappings the map of kinds to endpoint
	 * @param resource
	 */
	URLBuilder(URL baseUrl, Map<String, String> typeMappings, IResource resource) {
		this(baseUrl, typeMappings);
		resource(resource);
	}
	
	/**
	 * 
	 * @param baseUrl
	 * @param typeMappings the map of kinds to endpoint
	 */
	URLBuilder(URL baseUrl, Map<String, String> typeMappings) {
		this.baseUrl = baseUrl.toString().replaceAll("/*$", "");
		this.typeMappings = typeMappings;
	}
	
	URLBuilder namespace(String namespace){
		if(StringUtils.isBlank(namespace)) return this;
		this.namespace = namespace;
		return this;
	}
	
	URLBuilder name(String name) {
		this.name = name;
		return this;
	}

	URLBuilder kind(String kind) {
		if(!ResourceKind.values().contains(kind)) {
			LOG.warn(String.format("There kind '%s' is not recognized by this client; this operation may fail.", kind));
		}
		this.kind = kind;
		return this;
	}

	URLBuilder resource(IResource resource) {
		if (resource == null) return this;
		this.name = resource.getName();
		kind(resource.getKind());
		namespace(resource.getNamespace());
		return this;
	}

	URLBuilder addParmeter(String key, String value) {
		params.put(key, value);
		return this;
	}
	

	URLBuilder subresource(String value) {
		this.subResource = value;
		return this;
	}

	/**
	 * Builds a URL based on the information provided. Either  a resource or
	 * a resource kind must be provided
	 * @return
	 */
	URL build() {
		StringBuilder url = new StringBuilder(baseUrl);
		if (kind == null)
			throw new RuntimeException(
					"Unable to build a URL because the ResourceKind is unknown");
		buildWithNamespaceInPath(url);

		try {
			if(LOG.isDebugEnabled()) {
				LOG.debug(String.format("Built url: %s", url.toString()));
			}
			return new URL(url.toString());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void buildWithNamespaceInPath(StringBuilder url) {
		url.append("/");
		if(typeMappings.containsKey(kind))
			url.append(typeMappings.get(kind));
		else {
			throw new OpenShiftException("Unable to determine the api endpoint for kind '%s'", kind);
		}
		if(namespace != null && !ResourceKind.PROJECT.equals(kind)) {
			url.append("/namespaces/")
				.append(namespace);
		}
		url.append("/").append(ResourceKind.pluralize(kind));
		if (name != null) {
			url.append("/").append(name);
		}
		if(StringUtils.isNotBlank(subResource)) {
			url.append("/").append(subResource);
		}
		url = appendParameters(url);
	}

	private StringBuilder appendParameters(StringBuilder url) {
		if (!params.isEmpty()) {
			url.append(IHttpConstants.QUESTION_MARK);
			for (Iterator<Entry<String, String>> iterator = params.entrySet()
					.iterator(); iterator.hasNext();) {
				Entry<String, String> entry = (Entry<String, String>) iterator
						.next();
				try {
					url.append(entry.getKey())
							.append(IHttpConstants.EQUALS)
							.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
				if (iterator.hasNext()) {
					url.append(IHttpConstants.AMPERSAND);
				}
			}
		}
		return url;
	}

	public URLBuilder watch() {
		addParmeter("watch", "true");
		return this;
	}

	public String websocket() {
		String url = build().toString();
		url = "wss" + url.substring(url.indexOf(":"));
		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("Built url: %s", url));
		}
		return url;
	}

}
