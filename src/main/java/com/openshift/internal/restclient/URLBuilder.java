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

import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.http.IHttpClient;
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
	private ResourceKind kind;
	private String name;
	private Map<String, String> params = new HashMap<String, String>();
	private final Map<ResourceKind, String> typeMappings;

	private String namespace;

	URLBuilder(URL baseUrl, Map<ResourceKind, String> typeMappings, IResource resource) {
		this(baseUrl, typeMappings);
		resource(resource);
	}
	
	URLBuilder(URL baseUrl, Map<ResourceKind, String> typeMappings) {
		this.baseUrl = baseUrl.toString();
		this.typeMappings = typeMappings;
	}
	
	URLBuilder namespace(String namespace){
		if(StringUtils.isBlank(namespace)) return this;
		if(typeMappingIsForV1Beta1()) {
			addParmeter("namespace", namespace);
		}else {
			this.namespace = namespace;
		}
		return this;
	}
	
	URLBuilder name(String name) {
		this.name = name;
		return this;
	}

	URLBuilder kind(ResourceKind kind) {
		this.kind = kind;
		return this;
	}

	URLBuilder resource(IResource resource) {
		if (resource == null) return this;
		this.name = resource.getName();
		this.kind = resource.getKind();
		return this;
	}

	URLBuilder addParmeter(String key, String value) {
		params.put(key, value);
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
		if(typeMappingIsForV1Beta1()) {
			buildWithNamespaceAsQueryParam(url);
		}else {
			buildWithNamespaceInPath(url);
		}

		try {
			LOG.debug(String.format("Built url: %s", url.toString()));
			return new URL(url.toString());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	

	private boolean typeMappingIsForV1Beta1() {
		String mapping = typeMappings.get(kind);
		return mapping.contains("v1beta1");
	}

	private void buildWithNamespaceInPath(StringBuilder url) {
		url.append("/")
			.append(typeMappings.get(kind));
		if(namespace != null) {
			url.append("/namespaces/")
				.append(namespace);
		}
		url.append("/").append(kind.pluralize());
		if (name != null) {
			url.append("/").append(name);
		}
		url = appendParameters(url);
	}

	private URL buildWithNamespaceAsQueryParam(StringBuilder url) {
		url.append("/")
			.append(typeMappings.get(kind)).append("/")
			.append(kind.pluralize());
		if (name != null) {
			url.append("/").append(name);
		}
		url = appendParameters(url);
		try {
			LOG.debug(String.format("Built url: %s", url.toString()));
			return new URL(url.toString());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private StringBuilder appendParameters(StringBuilder url) {
		if (!params.isEmpty()) {
			url.append(IHttpClient.QUESTION_MARK);
			for (Iterator<Entry<String, String>> iterator = params.entrySet()
					.iterator(); iterator.hasNext();) {
				Entry<String, String> entry = (Entry<String, String>) iterator
						.next();
				try {
					url.append(entry.getKey())
							.append(IHttpClient.EQUALS)
							.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
				if (iterator.hasNext()) {
					url.append(url.append(IHttpClient.AMPERSAND));
				}
			}
		}
		return url;
	}
}
