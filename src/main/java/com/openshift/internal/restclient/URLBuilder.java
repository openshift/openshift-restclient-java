/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient;

import com.openshift.restclient.*;
import com.openshift.restclient.IApiTypeMapper.IVersionedApiResource;
import com.openshift.restclient.http.IHttpConstants;
import com.openshift.restclient.model.IResource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

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
    private ArrayList<AbstractMap.SimpleEntry<String, String>> params = new ArrayList<>();
    private final IApiTypeMapper typeMappings;
    private final ResourceKindRegistry resourceKindRegistry;

    private String apiVersion;
    private String namespace;
    private String subResource;
    private String subContext;


    /**
     * @param baseUrl
     * @param typeMappings the map of kinds to endpoint
     * @param resource
     */
    URLBuilder(URL baseUrl, IApiTypeMapper typeMappings, IResource resource, ResourceKindRegistry resourceKindRegistry) {
        this(baseUrl, typeMappings, resourceKindRegistry);
        resource(resource);
    }

    /**
     * @param baseUrl
     * @param typeMappings the map of kinds to endpoint
     */
    public URLBuilder(URL baseUrl, IApiTypeMapper typeMappings, ResourceKindRegistry resourceKindRegistry) {
        this.baseUrl = baseUrl.toString().replaceAll("/*$", "");
        this.typeMappings = typeMappings;
        this.resourceKindRegistry = resourceKindRegistry;
    }

    public URLBuilder apiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
        return this;
    }

    public URLBuilder namespace(String namespace) {
        if (StringUtils.isBlank(namespace)) return this;
        this.namespace = namespace;
        return this;
    }

    public URLBuilder name(String name) {
        this.name = name;
        return this;
    }

    public URLBuilder kind(String kind) {
        if (!resourceKindRegistry.find(kind).isPresent()) {
            LOG.warn(String.format("There kind '%s' is not recognized by this client; this operation may fail.", kind));
        }
        this.kind = kind;
        return this;
    }

    public URLBuilder resource(IResource resource) {
        if (resource == null) return this;
        this.name = resource.getName();
        kind(resource.getKind());
        namespace(resource.getNamespaceName());
        return this;
    }

    public URLBuilder addParmeter(String key, String value) {
        params.add(new AbstractMap.SimpleEntry<>(key, value));
        return this;
    }


    public URLBuilder subresource(String value) {
        this.subResource = value;
        return this;
    }

    public URLBuilder subContext(String value) {
        this.subContext = value;
        return this;
    }

    /**
     * Builds a URL based on the information provided. Either  a resource or
     * a resource kind must be provided
     *
     * @return
     */
    public URL build() {
        StringBuilder url = new StringBuilder(baseUrl);
        if (kind == null)
            throw new RuntimeException(
                    "Unable to build a URL because the ResourceKind is unknown");
        buildWithNamespaceInPath(url);

        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Built url: %s", url.toString()));
            }
            return new URL(url.toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private void buildWithNamespaceInPath(StringBuilder url) {
        if (!typeMappings.isSupported(apiVersion, kind)) {
            throw new UnsupportedEndpointException("Unable to determine the api endpoint for kind '%s'", kind);
        }
        url.append("/");
        IVersionedApiResource apiResource = typeMappings.getEndpointFor(apiVersion, kind);
        url.append(apiResource.getPrefix()).append("/").append(apiResource.getVersion());
        if (namespace == null && apiResource.isNamespaced()) {
            LOG.debug("The api endpoint for kind '{}' requires a namespace but none was provided. Will only work for priviledged user.", kind);
        }
        if (!PredefinedResourceKind.PROJECT.getIdentifier().equals(kind) && namespace != null) {
            url.append("/namespaces/")
                    .append(namespace);
        }
        url.append("/").append(apiResource.getName());
        if (name != null) {
            url.append("/").append(name);
        }
        if (StringUtils.isNotBlank(subResource) && !apiResource.isSupported(subResource)) {
            throw new OpenShiftException("The api endpoint for kind '%s' && subresource '%s' is not supported by the cluster", kind, subResource);
        }
        if (StringUtils.isNotBlank(subResource)) {
            url.append("/").append(subResource);
        }
        if (StringUtils.isNotBlank(subContext)) {
            url.append("/").append(subContext);
        }
        url = appendParameters(url);
    }

    private StringBuilder appendParameters(StringBuilder url) {
        if (!params.isEmpty()) {
            url.append(IHttpConstants.QUESTION_MARK);
            for (Iterator<AbstractMap.SimpleEntry<String, String>> iterator = params.iterator(); iterator.hasNext(); ) {
                AbstractMap.SimpleEntry<String, String> entry = iterator.next();
                try {
                    if (StringUtils.isNotBlank(entry.getValue())) {
                        url.append(entry.getKey())
                                .append(IHttpConstants.EQUALS)
                                .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                    } else {
                        LOG.error("Unable to append parameter: {} since it is blank", entry.getKey());
                    }
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
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Built url: %s", url));
        }
        return url;
    }

    public URLBuilder addParameters(Map<String, String> params) {
        params.forEach((k, v) -> addParmeter(k, v));
        return this;
    }

}
