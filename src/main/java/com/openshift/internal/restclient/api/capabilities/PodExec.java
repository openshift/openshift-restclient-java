/*******************************************************************************
 * Copyright (c) 2015-2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.internal.restclient.api.capabilities;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.DefaultClient;
import com.openshift.internal.restclient.URLBuilder;
import com.openshift.internal.restclient.capability.AbstractCapability;
import com.openshift.internal.restclient.okhttp.OpenShiftRequestBuilder;
import com.openshift.internal.restclient.okhttp.ResponseCodeInterceptor;
import com.openshift.restclient.IApiTypeMapper;
import com.openshift.restclient.IClient;
import com.openshift.restclient.api.capabilities.IPodExec;
import com.openshift.restclient.capability.IStoppable;
import com.openshift.restclient.http.IHttpConstants;
import com.openshift.restclient.model.IPod;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class PodExec extends AbstractCapability implements IPodExec {

    private static final Logger LOG = LoggerFactory.getLogger(IPodExec.class);
    private static final String CAPABILITY = "exec";

    private static final String COMMAND = "command";

    private static final String K8S_PROTOCOL_HEADER = "X-Stream-Protocol-Version";

    private static final String K8S_PROTOCOL = "channel.k8s.io";

    public static final int CHANNEL_STDOUT = 1;
    public static final int CHANNEL_STDERR = 2;
    public static final int CHANNEL_EXECERR = 3;

    private final IPod pod;
    private final DefaultClient client;
    private final IApiTypeMapper mapper;

    public PodExec(IPod pod, IClient client) {
        super(pod, client, CAPABILITY);
        this.pod = pod;
        this.client = client.adapt(DefaultClient.class);
        this.mapper = client.adapt(IApiTypeMapper.class);
    }

    @Override
    public String getName() {
        return PodExec.class.getSimpleName();
    }

    @Override
    public IStoppable start(IPodExecOutputListener listener, Options options, String... commands) {

        if (options == null) {
            options = new Options();
        }

        /*
         * with 3.7 per https://github.com/openshift/origin/issues/15330, 3.6 was
         * evidently broke in allowing stdErr/stdOut to not be set; need to set
         * stdout/stderr to true
         */
        options.stdErr(true);
        options.stdOut(true);

        Map<String, String> parameters = options.getMap();

        OkHttpClient okClient = client.adapt(OkHttpClient.class);

        URLBuilder urlBuilder = new URLBuilder(client.getBaseURL(), mapper).resource(pod).subresource(CAPABILITY)
                .addParameters(parameters);

        // The main command and all arguments are specified as 'command' parameters
        for (String command : commands) {
            urlBuilder.addParmeter(COMMAND, command);
        }

        final String endpoint = urlBuilder.websocket();

        Request request = new OpenShiftRequestBuilder()
                .url(endpoint)
                .method("GET", null)
                .accept(IHttpConstants.MEDIATYPE_ANY)
                .authorization(client.getAuthorizationContext())
                .addHeader(K8S_PROTOCOL_HEADER, K8S_PROTOCOL)
                // Unless we mark this as ignored, exceptions triggered by interceptor would be
                // lost in dispatcher thread
                .tag(new ResponseCodeInterceptor.Ignore() {})
                .build();

        ExecOutputListenerAdapter adapter = new ExecOutputListenerAdapter(listener);
        okClient.newWebSocket(request, adapter);
        return adapter;
    }

    static class ExecOutputListenerAdapter extends WebSocketListener implements IStoppable {

        private final IPodExecOutputListener listener;
        private WebSocket call;
        private AtomicBoolean open = new AtomicBoolean(false);
        private boolean shouldStop = false;

        public ExecOutputListenerAdapter(IPodExecOutputListener listener) {
            this.listener = listener;
        }

        @Override
        public void stop() {
            if (call != null) {
                call.cancel();
            } else {
                shouldStop = true;
            }
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            if (open.compareAndSet(false, true)) {
                this.call = webSocket;
                listener.onOpen();
                if (shouldStop) {
                    webSocket.cancel();
                }
            }
        }

        @Override
        public void onClosing(WebSocket socket, int code, String reason) {
            if (open.compareAndSet(true, false)) {
                listener.onClose(code, reason);
            }
        }

        @Override
        public void onFailure(WebSocket socket, Throwable e, Response response) {
            listener.onFailure(e);
        }

        public void deliver(int channel, String msg) {
            switch (channel) {
            case CHANNEL_STDOUT:
                listener.onStdOut(msg);
                break;
            case CHANNEL_STDERR:
                listener.onStdErr(msg);
                break;
            case CHANNEL_EXECERR:
                listener.onExecErr(msg);
                break;
            default:
                LOG.warn("Unable to deliver exec message of type [%d]: %s", channel, msg);
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            throw new IllegalStateException("Should not receive text message on pod exec sockets");
        }

        @Override
        public void onMessage(WebSocket socket, ByteString message) {

            /**
             * https://godoc.org/k8s.io/kubernetes/pkg/util/wsstream The Websocket
             * subprotocol "channel.k8s.io" prepends each binary message with a byte
             * indicating the channel number (zero indexed) the message was sent on.
             * Messages in both directions should prefix their messages with this channel
             * byte. When used for remote execution, the channel numbers are by convention
             * defined to match the POSIX file-descriptors assigned to STDIN, STDOUT, and
             * STDERR (0, 1, and 2). No other conversion is performed on the raw subprotocol
             * - writes are sent as they are received by the server.
             */

            int channel = message.getByte(0);
            String msg = message.substring(1).utf8();
            deliver(channel, msg);
        }

    }

}
