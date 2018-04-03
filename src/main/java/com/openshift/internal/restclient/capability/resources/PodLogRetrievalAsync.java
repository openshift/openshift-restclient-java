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
package com.openshift.internal.restclient.capability.resources;

import com.openshift.internal.restclient.DefaultClient;
import com.openshift.internal.restclient.URLBuilder;
import com.openshift.internal.restclient.okhttp.ResponseCodeInterceptor;
import com.openshift.internal.restclient.okhttp.WebSocketAdapter;
import com.openshift.restclient.IApiTypeMapper;
import com.openshift.restclient.IClient;
import com.openshift.restclient.UnsupportedEndpointException;
import com.openshift.restclient.capability.IStoppable;
import com.openshift.restclient.capability.resources.IPodLogRetrievalAsync;
import com.openshift.restclient.http.IHttpConstants;
import com.openshift.restclient.model.IPod;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.ws.WebSocket;
import okhttp3.ws.WebSocketCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Impl of Pod log retrieval using websocket
 *
 * @author jeff.cantrill
 */
public class PodLogRetrievalAsync implements IPodLogRetrievalAsync {

    private static final Logger LOG = LoggerFactory.getLogger(PodLogRetrievalAsync.class);
    private static final String CAPABILITY = "log";
    private final IPod pod;
    private final DefaultClient client;
    private final IApiTypeMapper mapper;

    public PodLogRetrievalAsync(IPod pod, IClient client) {
        this.pod = pod;
        this.client = client.adapt(DefaultClient.class);
        this.mapper = client.adapt(IApiTypeMapper.class);
    }

    @Override
    public boolean isSupported() {
        if (client != null && mapper != null) {
            try {
                return mapper.getEndpointFor(pod.getApiVersion(), pod.getKind()).isSupported(CAPABILITY);
            } catch (UnsupportedEndpointException e) {
                //endpoint not found for version/kind
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return PodLogRetrievalAsync.class.getSimpleName();
    }

    @Override
    public IStoppable start(IPodLogListener listener) {
        return start(listener, null);
    }

    @Override
    public IStoppable start(IPodLogListener listener, Options options) {
        Map<String, String> parameters = options != null ? options.getMap() : new HashMap<>();
        PodLogListenerAdapter adapter = new PodLogListenerAdapter(listener);

        OkHttpClient okClient = client.adapt(OkHttpClient.class);
        final String endpoint = new URLBuilder(client.getBaseURL(), mapper, client.getResourceFactory().getResourceKindRegistry())
                .kind(pod.getKind())
                .namespace(pod.getNamespaceName())
                .name(pod.getName())
                .subresource(CAPABILITY)
                .addParameters(parameters)
                .websocket();
        Request request = client.newRequestBuilderTo(endpoint)
                .tag(new ResponseCodeInterceptor.Ignore() {
                })
                .build();
        WebSocketCall call = WebSocketCall.create(okClient, request);
        call.enqueue(adapter);

        return adapter;
    }

    static class PodLogListenerAdapter extends WebSocketAdapter implements IStoppable {

        private final IPodLogListener listener;
        private WebSocket wsClient;
        private AtomicBoolean open = new AtomicBoolean(false);

        public PodLogListenerAdapter(IPodLogListener listener) {
            this.listener = listener;
        }

        @Override
        public void stop() {
            try {
                if (open.get()) {
                    wsClient.close(IHttpConstants.STATUS_NORMAL_STOP, "Client asking to stop.");
                }
            } catch (Exception e) {
                LOG.debug("Unable to stop the watch client", e);
            } finally {
                wsClient = null;
            }
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            if (open.compareAndSet(false, true)) {
                wsClient = webSocket;
                listener.onOpen();
            }
        }

        @Override
        public void onClose(int code, String reason) {
            if (open.compareAndSet(true, false)) {
                listener.onClose(code, reason);
            }
        }

        @Override
        public void onMessage(ResponseBody message) throws IOException {
            listener.onMessage(message.string());
        }

        @Override
        public void onFailure(IOException e, Response response) {
            listener.onFailure(e);
        }

    }
}
