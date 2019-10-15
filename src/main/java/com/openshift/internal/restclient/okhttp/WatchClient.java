/******************************************************************************* 
 * Copyright (c) 2016-2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package com.openshift.internal.restclient.okhttp;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang.StringUtils;
import org.jboss.dmr.ModelNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.DefaultClient;
import com.openshift.internal.restclient.URLBuilder;
import com.openshift.internal.restclient.model.properties.ResourcePropertyKeys;
import com.openshift.restclient.IApiTypeMapper;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IOpenShiftWatchListener;
import com.openshift.restclient.IOpenShiftWatchListener.ChangeType;
import com.openshift.restclient.IWatcher;
import com.openshift.restclient.http.IHttpConstants;
import com.openshift.restclient.model.IList;
import com.openshift.restclient.model.IResource;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WatchClient implements IWatcher, IHttpConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(WatchClient.class);
    private DefaultClient client;
    private OkHttpClient okClient;
    private AtomicReference<Status> status = new AtomicReference<>(Status.Stopped);
    private IApiTypeMapper typeMappings;
    private Map<String, WatchEndpoint> endpointMap = Collections.synchronizedMap(new HashMap<>());

    private enum Status {
        Started, Starting, Stopped, Stopping
    }

    public WatchClient(DefaultClient client, IApiTypeMapper typeMapper, OkHttpClient okClient) {
        this.client = client;
        this.typeMappings = typeMapper;
        this.okClient = okClient;
    }

    @Override
    public void stop() {
        if (status.compareAndSet(Status.Started, Status.Stopping)) {
            Map<String, WatchEndpoint> endpoints = new HashMap<>(endpointMap);
            endpointMap.clear();
            endpoints.values().forEach(w -> w.close());
            status.set(Status.Stopped);
        }
    }

    public IWatcher watch(Collection<String> kinds, String namespace, IOpenShiftWatchListener listener) {

        if (status.compareAndSet(Status.Stopped, Status.Starting)) {
            try {
                for (String kind : kinds) {
                    WatchEndpoint socket = new WatchEndpoint(client, listener, kind);
                    final String resourceVersion = getResourceVersion(kind, namespace, socket);

                    final String endpoint = new URLBuilder(client.getBaseURL(), this.typeMappings)
                            .kind(kind)
                            .namespace(namespace)
                            .watch()
                            .addParmeter(ResourcePropertyKeys.RESOURCE_VERSION, resourceVersion).websocket();
                    Request request = new OpenShiftRequestBuilder()
                            .url(endpoint)
                            .acceptJson()
                            .authorization(client.getAuthorizationContext())
                            .header(PROPERTY_ORIGIN, client.getBaseURL().toString())
                            .header(PROPERTY_USER_AGENT, "openshift-restclient-java")
                            .build();
                    okClient.newWebSocket(request, socket);
                    endpointMap.put(kind, socket);
                }
                status.set(Status.Started);
            } catch (Exception e) {
                endpointMap.clear();
                status.set(Status.Stopped);
                throw ResponseCodeInterceptor.createOpenShiftException(client, 0,
                        String.format("Could not watch resources in namespace %s: %s", namespace, e.getMessage()), null,
                        e);
            }
        }
        return this;
    }

    private String getResourceVersion(String kind, String namespace, WatchEndpoint endpoint) throws Exception {
        IList list = client.get(kind, namespace);
        Collection<IResource> items = list.getItems();
        List<IResource> resources = new ArrayList<>(items.size());
        resources.addAll(items);
        endpoint.setResources(resources);
        return list.getResourceVersion();
    }

    static class WatchEndpoint extends WebSocketListener {

        private IOpenShiftWatchListener listener;
        private List<IResource> resources;
        private final String kind;
        private final IClient client;
        private WebSocket wsClient;

        public WatchEndpoint(IClient client, IOpenShiftWatchListener listener, String kind) {
            this.listener = listener;
            this.kind = kind;
            this.client = client;
        }

        void close() {
            try {
                if (wsClient != null) {
                    wsClient.close(STATUS_NORMAL_STOP, "Client was asked to stop.");
                    wsClient = null;
                }
                listener.disconnected();
            } catch (Exception e) {
                LOGGER.debug("Unable to stop the watch client", e);
            } finally {
                wsClient = null;
            }
        }

        public void setResources(List<IResource> resources) {
            this.resources = resources;
        }

        @Override
        public void onClosing(WebSocket socket, int statusCode, String reason) {
            LOGGER.debug("WatchSocket closed for kind: {}, code: {}, reason: {}",
                    new Object[] { kind, statusCode, reason });
            listener.disconnected();
        }

        @Override
        public void onFailure(WebSocket socket, Throwable err, Response response) {
            LOGGER.debug("WatchSocket Error for kind {}: {}", kind, err);
            try {
                if (response == null) {
                    listener.error(ResponseCodeInterceptor.createOpenShiftException(client, 0, "", "", err));
                } else if (response.code() == IHttpConstants.STATUS_OK && err instanceof ProtocolException) {
                    // Just swallow it. Means the feature isn't supported in this OS server version
                    // yet.
                    // WebSocket creates error "Expected HTTP 101 response but was '200 OK'"
                    // This is described in the web socket specification.
                    LOGGER.debug("The feature isn't supported", err);
                } else {
                    listener.error(ResponseCodeInterceptor.createOpenShiftException(client, response.code(),
                            response.body().string(), response.request().url().toString(), err));
                }
            } catch (IOException e) {
                LOGGER.error("IOException trying to notify listener of specific OpenShiftException", err);
                listener.error(err);
            }
        }

        @Override
        public void onMessage(WebSocket socket, String body) {
            LOGGER.debug(body);
            ModelNode node = ModelNode.fromJSONString(body);
            IOpenShiftWatchListener.ChangeType event = new ChangeType(node.get("type").asString());
            IResource resource = client.getResourceFactory().create(node.get("object").toJSONString(true));
            if (StringUtils.isEmpty(resource.getKind())) {
                LOGGER.error("Unable to determine resource kind from: " + node.get("object").toJSONString(false));
            }
            listener.received(resource, event);
        }

        @Override
        public void onOpen(WebSocket socket, Response response) {
            LOGGER.debug("WatchSocket connected for {}", kind);
            wsClient = socket;
            listener.connected(resources);
        }
    }
}
