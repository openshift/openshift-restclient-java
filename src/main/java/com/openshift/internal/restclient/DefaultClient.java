/*******************************************************************************
 * Copyright (c) 2015-2019 Red Hat, Inc. Distributed under license by Red Hat, Inc.
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.dmr.ModelNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.authorization.AuthorizationContext;
import com.openshift.internal.restclient.okhttp.ResponseCodeInterceptor;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * @author Jeff Cantrill
 */
public class DefaultClient implements IClient, IHttpConstants {

    public static final String SYSTEM_PROP_K8E_API_VERSION = "osjc.k8e.apiversion";
    public static final String SYSTEM_PROP_OPENSHIFT_API_VERSION = "osjc.openshift.apiversion";

    private static final String URL_HEALTH_CHECK = "healthz";

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultClient.class);
    private URL baseUrl;
    private CompletableFuture<URL> authorizationEndpoint = new CompletableFuture<>();
    private CompletableFuture<URL> tokenEndpoint = new CompletableFuture<>();
    
    private OkHttpClient client;
    private IResourceFactory factory;
    private Map<Class<? extends ICapability>, ICapability> capabilities = new HashMap<>();
    private boolean capabilitiesInitialized = false;

    private static final String OS_API_ENDPOINT = "oapi";

    private String openShiftVersion;
    private String kubernetesVersion;
    private AuthorizationContext authContext;
    private IApiTypeMapper typeMapper;

    public DefaultClient(URL baseUrl, OkHttpClient client, IResourceFactory factory, IApiTypeMapper typeMapper,
            AuthorizationContext authContext) {
        this.baseUrl = baseUrl;
        this.client = client;
        this.factory = factory;
        if (this.factory != null) {
            this.factory.setClient(this);
        }
        initMasterVersion("version/openshift", new VersionCallback("OpenShift", version -> this.openShiftVersion = version));
        initMasterVersion("version", new VersionCallback("Kubernetes", version -> this.kubernetesVersion = version));
        initMasterVersion(".well-known/oauth-authorization-server", new AuthorizationCallback());
        this.typeMapper = typeMapper != null ? typeMapper : new ApiTypeMapper(baseUrl.toString(), client);
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

        final URL endpoint = new URLBuilder(this.baseUrl, typeMapper).apiVersion(version).kind(kind).name(name)
                .namespace(namespace).subresource(subresource).subContext(subContext).addParameters(params).build();

        try {
            Request request = newRequestBuilderTo(endpoint.toString())
                    .method(method, requestBody)
                    .build();
            LOGGER.debug("About to make {} request: {}", request.method(), request);
            try (Response result = client.newCall(request).execute()) {
                String response = result.body().string();
                LOGGER.debug("Response: {}", response);
                return (T) factory.createInstanceFrom(response);
            }
        } catch (IOException e) {
            throw new OpenShiftException(e, "Unable to execute request to %s", endpoint);
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
        return RequestBody.create(MediaType.parse(MEDIATYPE_APPLICATION_JSON), json);
    }


    private RequestBody getPayload(InputStream payload, String method) {
        if(isPayloadlessMethod(method)) {
            return null;
        }
        LOGGER.debug("About to send binary payload");
        return new RequestBody() {
            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source = Okio.source(payload);
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
                    .url(new URL(this.baseUrl, URL_HEALTH_CHECK))
                    .header(PROPERTY_ACCEPT, "*/*")
                    .build();
            try (Response response = client.newCall(request).execute()) {
                return response.body().string();
            }
        } catch (IOException e) {
            throw new OpenShiftException(e,
                    "Exception while trying to determine the health/ready response of the server");
        }
    }

    public Builder newRequestBuilderTo(String endpoint) {
        return newRequestBuilderTo(endpoint, MEDIATYPE_APPLICATION_JSON);
    }

    public Builder newRequestBuilderTo(String endpoint, String acceptMediaType) {
        Builder builder = new Builder()
                .url(endpoint)
                .header(PROPERTY_ACCEPT, acceptMediaType);
        addAuthorizationHeader(builder);
        return builder;
    }

    private void addAuthorizationHeader(Builder builder) {
        String token = null;
        if (this.authContext != null && StringUtils.isNotBlank(this.authContext.getToken())) {
            token = this.authContext.getToken();
        }
        builder.header(IHttpConstants.PROPERTY_AUTHORIZATION,
                String.format("%s %s", IHttpConstants.AUTHORIZATION_BEARER, token));
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

    private void initMasterVersion(String versionInfoType, Callback callback) {
        try {
            Request request = new Builder().url(new URL(this.baseUrl, versionInfoType))
                    .header(PROPERTY_ACCEPT, MEDIATYPE_APPLICATION_JSON)
                    .tag(new ResponseCodeInterceptor.Ignore() {})
                    .build();
            client.newCall(request).enqueue(callback);
        } catch (IOException e) {
            LOGGER.warn("Exception while trying to determine master version of openshift and kubernetes", e);
        }
    }

    private class VersionCallback implements Callback {
        String description;
        Consumer<String> versionSetter;

        public VersionCallback(String description, Consumer<String> versionSetter) {
            this.description = description;
            this.versionSetter = versionSetter;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            versionSetter.accept("");
            LOGGER.warn("Exception while trying to determine " + description + " master version", e);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            try {
                if (response.isSuccessful()) {
                    versionSetter.accept(ModelNode.fromJSONString(response.body().string()).get("gitVersion").asString());
                } else {
                    versionSetter.accept("");
                    LOGGER.warn("Failed to determine " + description + " master version: got " + response.code());
                }
            } finally {
                response.close();
            }
        }
    }

    private class AuthorizationCallback implements Callback {

        private void setDefaults() {
            DefaultClient.this.authorizationEndpoint.complete(DefaultClient.this.getDefaultAuthorizationEndpoint());
            DefaultClient.this.tokenEndpoint.complete(DefaultClient.this.getDefaultTokenEndpoint());
        }

        @Override
        public void onFailure(Call call, IOException e) {
            setDefaults();
            LOGGER.warn("Exception while trying to get authorization endpoint", e);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            try {
                if (response.isSuccessful()) {
                    ModelNode node = ModelNode.fromJSONString(response.body().string());
                    DefaultClient.this.authorizationEndpoint.complete(new URL(node.get("authorization_endpoint").asString()));
                    DefaultClient.this.tokenEndpoint.complete(new URL(node.get("token_endpoint").asString()));
                } else {
                    setDefaults();
                    LOGGER.warn("Failed to determine authorization endpoint: got " + response.code());
                }
            } finally {
                response.close();
            }
        }
    }

    @Override
    public String getOpenshiftMasterVersion() {
        return this.openShiftVersion;
    }

    @Override
    public String getKubernetesMasterVersion() {
        return this.kubernetesVersion;
    }

    @Override
    public URL getBaseURL() {
        return this.baseUrl;
    }

    @Override
    public URL getAuthorizationEndpoint() {
        try {
            return authorizationEndpoint.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new OpenShiftException(e, e.getLocalizedMessage());
        }
    }
    
    protected URL getDefaultAuthorizationEndpoint() {
        try {
            return new URL(getBaseURL(), "oauth/authorize");
        } catch (MalformedURLException e) {
            throw new OpenShiftException(e, e.getLocalizedMessage());
        }
    }

    @Override
    public URL getTokenEndpoint() {
        try {
            return tokenEndpoint.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new OpenShiftException(e, e.getLocalizedMessage());
        }
    }
    
    protected URL getDefaultTokenEndpoint() {
        try {
            return new URL(getBaseURL(), "oauth/token");
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
        if (kubernetesVersion == null) {
            if (other.kubernetesVersion != null) {
                return false;
            }
        } else if (!kubernetesVersion.equals(other.kubernetesVersion)) {
            return false;
        }
        if (openShiftVersion == null) {
            if (other.openShiftVersion != null) {
                return false;
            }
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
        return null;
    }

}
