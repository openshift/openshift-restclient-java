/*******************************************************************************
 * Copyright (c) 2015-2020 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient;

import static com.openshift.internal.restclient.capability.CapabilityInitializer.initializeClientCapabilities;
import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.dmr.ModelNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.authorization.AuthorizationContext;
import com.openshift.internal.restclient.okhttp.OpenShiftRequestBuilder;
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
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * @author Jeff Cantrill
 */
public class DefaultClient implements IClient, IHttpConstants {


    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultClient.class);

    public static final String PATH_KUBERNETES_VERSION = "version";
    public static final String PATH_OPENSHIFT_VERSION = "version/openshift";
    public static final String PATH_HEALTH_CHECK = "healthz";
    public static final String PATH_DEFAULT_OAUTH_TOKEN = "oauth/token";
    public static final String PATH_DEFAULT_OAUTH_AUTHORIZE = "oauth/authorize";

    public static final String SYSTEM_PROP_K8E_API_VERSION = "osjc.k8e.apiversion";
    public static final String SYSTEM_PROP_OPENSHIFT_API_VERSION = "osjc.openshift.apiversion";

    private static final String OS_API_ENDPOINT = "oapi";

    private URL baseUrl;
    private OkHttpClient client;
    private IResourceFactory factory;
    private Map<Class<? extends ICapability>, ICapability> capabilities = new HashMap<>();
    private boolean capabilitiesInitialized = false;

    private final AuthorizationContext authContext;
    private final IApiTypeMapper typeMapper;
    private final ClusterVersion kubernetesVersion;
    private final ClusterVersion openShiftVersion;
    private final AuthorizationEndpoints authorizationEndpoints;
    private OpenShiftMajorVersion openShiftMajorVersion;

    public DefaultClient(URL baseUrl, OkHttpClient client, IResourceFactory factory, IApiTypeMapper typeMapper,
            AuthorizationContext authContext) {
        this.baseUrl = baseUrl;
        this.client = client;
        this.factory = factory;
        if (this.factory != null) {
            this.factory.setClient(this);
        }
        this.typeMapper = typeMapper != null ? typeMapper : new ApiTypeMapper(baseUrl.toString(), client, authContext);
        this.authContext = authContext;
        this.kubernetesVersion = new ClusterVersion(baseUrl.toExternalForm() + "/" + PATH_KUBERNETES_VERSION, "Kubernetes Version", client);
        this.openShiftVersion = new ClusterVersion(baseUrl.toExternalForm() + "/" + PATH_OPENSHIFT_VERSION, "OpenShift Version", client);
        this.authorizationEndpoints = new AuthorizationEndpoints(baseUrl.toExternalForm(), client);
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
    }

    @Override
    public IWatcher watch(String namespace, IOpenShiftWatchListener listener, String... kinds) {
        WatchClient watcher = new WatchClient(this, this.typeMapper, this.client);
        return watcher.watch(Arrays.asList(kinds), namespace, listener);
    }

    @Override
    public IWatcher watch(IOpenShiftWatchListener listener, String... kinds) {
        return this.watch("", listener, kinds);
    }

    @Override
    public String getResourceURI(IResource resource) {
        return new URLBuilder(getBaseURL(), typeMapper, resource).build().toString();
    }

    @Override
    public <T extends IResource> List<T> list(String kind) {
        return list(kind, "");
    }

    @Override
    public <T extends IResource> List<T> list(String kind, Map<String, String> labels) {
        return list(kind, "", labels);
    }

    @Override
    public <T extends IResource> List<T> list(String kind, String namespace) {
        return list(kind, namespace, new HashMap<>());
    }

    @Override
    public <T extends IResource> List<T> list(String kind, String namespace, Map<String, String> labels) {
        String labelQuery = "";
        if (labels != null && !labels.isEmpty()) {
            labelQuery = labels.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(joining(","));
        }
        return list(kind, namespace, labelQuery);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IResource> List<T> list(String kind, String namespace, String labelQuery) {
        Map<String, String> params = new HashMap<>();
        if (labelQuery != null && !labelQuery.isEmpty()) {
            params.put("labelSelector", labelQuery);
        }

        IList resources = execute(HttpMethod.GET.toString(), kind, namespace, null, null, null, params);
        List<T> items = new ArrayList<>();
        items.addAll((Collection<? extends T>) resources.getItems());
        return items;
    }

    @Override
    public Collection<IResource> create(IList list, String namespace) {
        List<IResource> results = new ArrayList<>(list.getItems().size());
        for (IResource resource : list.getItems()) {
            try {
                results.add(create(resource, namespace));
            } catch (OpenShiftException e) {
                if (e.getStatus() != null) {
                    results.add(e.getStatus());
                } else {
                    throw e;
                }
            }
        }
        return results;
    }

    @Override
    public <T extends IResource> T create(T resource) {
        return create(resource, resource.getNamespaceName());
    }

    @Override
    public <T extends IResource> T create(T resource, String namespace) {
        return execute(HttpMethod.POST, resource.getKind(), namespace, null, null, resource);
    }

    @Override
    public <T extends IResource> T create(String kind, String namespace, String name, String subresource,
            IResource payload) {
        return execute(HttpMethod.POST, kind, namespace, name, subresource, payload);
    }

    public <T extends IResource> T create(String kind, String version, String namespace, String name, String subresource,
            InputStream payload) {
        return create(kind, version, namespace, name, subresource, payload, Collections.emptyMap());
    }

    public <T extends IResource> T create(String kind, String version, String namespace, String name, String subresource,
            InputStream payload, Map<String, String> parameters) {
        return execute(HttpMethod.POST, kind, version, namespace, name, subresource, payload, parameters);
    }

    enum HttpMethod {
        GET, PUT, POST, DELETE, HEAD
    }

    private <T extends IResource> T execute(HttpMethod method, String kind, String namespace, String name,
            String subresource, IResource payload) {
        return execute(method.toString(), kind, namespace, name, subresource, payload);
    }

    private <T extends IResource> T execute(HttpMethod method, String kind, String version, String namespace, String name,
            String subresource, InputStream payload, Map<String, String> parameters) {
        return execute(method.toString(), kind, version, namespace, name, subresource, payload, parameters);
    }

    public <T extends IResource> T execute(String method, String kind, String namespace, String name,
            String subresource, IResource payload, String subContext) {
        return execute(this.factory, method, kind, namespace, name, subresource, subContext, payload,
                Collections.emptyMap());
    }

    @Override
    public <T extends IResource> T execute(String method, String kind, String namespace, String name,
            String subresource, IResource payload) {
        return execute(this.factory, method, kind, namespace, name, subresource, null, payload,
                Collections.emptyMap());
    }

    @Override
    public <T extends IResource> T execute(String method, String kind, String version, String namespace, String name,
            String subresource, InputStream payload) {
        return execute(this.factory, method, kind, version, namespace, name, subresource, null, payload,
                Collections.emptyMap());
    }

    @Override
    public <T extends IResource> T execute(String method, String kind, String version, String namespace, String name,
            String subresource, InputStream payload, Map<String, String> parameters) {
        return execute(this.factory, method, kind, version, namespace, name, subresource, null, payload,
                parameters);
    }

    @Override
    public <T extends IResource> T execute(String method, String kind, String namespace, String name,
            String subresource, IResource payload, Map<String, String> params) {
        return execute(this.factory, method, kind, namespace, name, subresource, null, payload, params);
    }

    public <T> T execute(ITypeFactory factory, String method, String kind, String version, String namespace, String name,
            String subresource, String subContext, InputStream payload, Map<String, String> params) {
        return execute(factory, method, kind, version, namespace, name, subresource, subContext, 
                getPayload(payload, method), params);
    }

    public <T> T execute(ITypeFactory factory, String method, String kind, String namespace, String name,
            String subresource, String subContext, JSONSerializeable payload, Map<String, String> params) {
        return execute(factory, method, kind, getApiVersion(payload), namespace, name, subresource, subContext,
                getPayload(payload, method), params);
    }

    @SuppressWarnings("unchecked")
    private <T> T execute(ITypeFactory factory, String method, String kind, String version, String namespace,
            String name, String subresource, String subContext, RequestBody requestBody, Map<String, String> params) {
        if (factory == null) {
            throw new OpenShiftException(ITypeFactory.class.getSimpleName() + " is null while trying to call IClient#execute");
        }

        if (params == null) {
            params = Collections.emptyMap();
        }

        if (ResourceKind.LIST.equals(kind)) {
            throw new UnsupportedOperationException("Generic create operation not supported for resource type 'List'");
        }

        final URL endpoint = new URLBuilder(this.baseUrl, typeMapper)
                .apiVersion(version)
                .kind(kind)
                .name(name)
                .namespace(namespace)
                .subresource(subresource)
                .subContext(subContext)
                .addParameters(params)
                .build();
        Request request = newRequestBuilder()
            .url(endpoint)
            .method(method, requestBody)
            .acceptJson()
            .authorization(authContext)
            .build();
        LOGGER.debug("About to make {} request: {}", request.method(), request);
        try {
            String body = request(request);
            return (T) factory.createInstanceFrom(body);
        } catch (IOException e) {
            throw new OpenShiftException(e, "Unable to execute request to %s", endpoint);
        }
    }

    private String request(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string() ;
            LOGGER.debug("Response: {}", body);
            return body;
        }
    }
    
    private String getApiVersion(JSONSerializeable payload) {
        String apiVersion = null;
        if (payload instanceof IResource) {
            apiVersion = ((IResource) payload).getApiVersion();
        }
        return apiVersion;
    }

    private RequestBody getPayload(JSONSerializeable payload, String method) {
        if(isPayloadlessMethod(method)) {
            return null;
        }
        String json = payload == null ? "" : payload.toJson(true);
        LOGGER.debug("About to send payload: {}", json);
        return RequestBody.create(json, MediaType.parse(MEDIATYPE_APPLICATION_JSON));
    }

    RequestBody getPayload(InputStream payload, String method) {
        if(isPayloadlessMethod(method)) {
            return null;
        }
        InputStream input = payload == null ? IOUtils.toInputStream("") : payload;
        LOGGER.debug("About to send binary payload");
        return new RequestBody() {
            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source = Okio.source(input);
                sink.writeAll(source);
            }

            @Override
            public MediaType contentType() {
                return MediaType.parse(MEDIATYPE_APPLICATION_OCTET_STREAM);
            }
        };
    }

    private boolean isPayloadlessMethod(String method) {
        String uppercaseMethod = StringUtils.upperCase(method);
        return HttpMethod.GET.name().equals(uppercaseMethod) 
                || HttpMethod.HEAD.name().equals(uppercaseMethod);
    }

    @Override
    public String getServerReadyStatus() {
        try {
            Request request = new Request.Builder()
                    .url(new URL(this.baseUrl, PATH_HEALTH_CHECK))
                    .header(PROPERTY_ACCEPT, "*/*")
                    .build();
            return request(request);
        } catch (IOException e) {
            throw new OpenShiftException(e,
                    "Exception while trying to determine the health/ready response of the server");
        }
    }

    /* for debugging purposes */
    protected OpenShiftRequestBuilder newRequestBuilder() {
        return new OpenShiftRequestBuilder();
    }

    @Override
    public <T extends IResource> T update(T resource) {
        return execute(HttpMethod.PUT, resource.getKind(), resource.getNamespaceName(), resource.getName(), null,
                resource);
    }

    @Override
    public <T extends IResource> void delete(T resource) {
        delete(resource.getKind(), resource.getNamespaceName(), resource.getName());
    }

    @Override
    public void delete(String resourceKind, String namespaceName, String name) {
        execute(HttpMethod.DELETE, resourceKind, namespaceName, name, null, null);
    }

    @Override
    public IList get(String kind, String namespace) {
        return execute(HttpMethod.GET, kind, namespace, null, null, (IResource)null);
    }

    @Override
    public <T extends IResource> T get(String kind, String name, String namespace) {
        return execute(HttpMethod.GET, kind, namespace, name, null, (IResource)null);
    }

    public synchronized void initializeCapabilities() {
        if (capabilitiesInitialized) {
            return;
        }
        initializeClientCapabilities(capabilities, this);
        capabilitiesInitialized = true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ICapability> T getCapability(Class<T> capability) {
        return (T) capabilities.get(capability);
    }

    @Override
    public boolean supports(Class<? extends ICapability> capability) {
        if (!capabilitiesInitialized) {
            initializeCapabilities();
        }
        return capabilities.containsKey(capability);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ICapability, R> R accept(CapabilityVisitor<T, R> visitor, R unsupportedCapabililityValue) {
        if (!capabilitiesInitialized) {
            initializeCapabilities();
        }
        if (capabilities.containsKey(visitor.getCapabilityType())) {
            T capability = (T) capabilities.get(visitor.getCapabilityType());
            return visitor.visit(capability);
        }
        return unsupportedCapabililityValue;
    }

    @Override
    public String getOpenShiftAPIVersion() {
        return typeMapper.getPreferedVersionFor(OS_API_ENDPOINT);
    }

    @Override
    public String getOpenshiftMasterVersion() {
        return openShiftVersion.get();
    }

    @Override
    public String getKubernetesMasterVersion() {
        return kubernetesVersion.get();
    }

    @Override
    public URL getBaseURL() {
        return this.baseUrl;
    }

    @Override
    public URL getAuthorizationEndpoint() {
        URL url = authorizationEndpoints.getAuthorizationEndpoint();
        if (url != null) {
            return url;
        }
        return getDefaultAuthorizationEndpoint();
                
    }
    
    protected URL getDefaultAuthorizationEndpoint() {
        try {
            return new URL(getBaseURL(), PATH_DEFAULT_OAUTH_AUTHORIZE);
        } catch (MalformedURLException e) {
            throw new OpenShiftException(e, e.getLocalizedMessage());
        }
    }

    @Override
    public URL getTokenEndpoint() {
        URL url = authorizationEndpoints.getTokenEndpoint();
        if (url != null) {
            return url;
        }
        return getDefaultTokenEndpoint();
    }
    
    protected URL getDefaultTokenEndpoint() {
        try {
            return new URL(getBaseURL(), PATH_DEFAULT_OAUTH_TOKEN);
        } catch (MalformedURLException e) {
            throw new OpenShiftException(e, e.getLocalizedMessage());
        }
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
    public int getOpenShiftMajorVersion() {
        if (openShiftMajorVersion == null) {
            this.openShiftMajorVersion = new OpenShiftMajorVersion(getOpenShiftAPIVersion(), getKubernetesMasterVersion());
        }
        return openShiftMajorVersion.get();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((baseUrl == null) ? 0 : baseUrl.toString().hashCode());
        result = prime * result + ((kubernetesVersion == null) ? 0 : kubernetesVersion.hashCode());
        result = prime * result + ((openShiftVersion == null) ? 0 : openShiftVersion.hashCode());
        result = prime * result
                + ((authContext == null || authContext.getToken() == null) ? 0 : authContext.getToken().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DefaultClient)) {
            return false;
        }
        DefaultClient other = (DefaultClient) obj;
        if (baseUrl == null) {
            if (other.baseUrl != null) {
                return false;
            }
        } else if (!baseUrl.toString().equals(other.baseUrl.toString())) {
            return false;
        }
        if (authContext == null) {
            return other.authContext == null;
        } else {
            if (other.authContext == null) {
                return false;
            }
            return Objects.equals(authContext.getUserName(), other.authContext.getUserName());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T adapt(Class<T> klass) {
        if (DefaultClient.class.equals(klass)) {
            return (T) this;
        }
        if (OkHttpClient.class.equals(klass)) {
            return (T) this.client;
        }
        if (IApiTypeMapper.class.equals(klass)) {
            return (T) this.typeMapper;
        }
        if (ICapability.class.isAssignableFrom(klass) && this.supports((Class<? extends ICapability>) klass)) {
            return (T) getCapability((Class<? extends ICapability>) klass);
        }
        if (IResourceFactory.class.equals(klass)) {
            return (T) this.factory;
        }
        return null;
    }

    private class ClusterVersion extends RequestingSupplier<String> {

        protected ClusterVersion(String url, String description, OkHttpClient client) {
            super(url, description, client);
        }

        @Override
        protected String extractValue(String response) {
            try {
                return ModelNode.fromJSONString(response).get("gitVersion").asString();
            } catch (IllegalArgumentException e) {
                LOGGER.error("Could not retrieve {}: Invalid JSON.", description);
                return null;
            }
        }

        @Override
        protected String getDefaultValue() {
            return "";
        }
    }
}
