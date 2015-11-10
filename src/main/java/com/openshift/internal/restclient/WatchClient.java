/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient;

import java.net.URI;
import java.net.URL;
import java.util.Map;

import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.UpgradeException;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IOpenShiftWatchListener;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.IWatcher;
import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.authorization.ResourceForbiddenException;
import com.openshift.restclient.http.IHttpStatusCodes;
import com.openshift.restclient.model.IList;

/**
  * Encapsulation of the logic to handle watching resources.
  * @author Jeff Cantrill
  *
  */
public class WatchClient implements IHttpStatusCodes, IWatcher{
	private static final Logger LOGGER = LoggerFactory.getLogger(WatchClient.class);
	private URL baseUrl;
	private Map<String, String> typeMappings;
	private IResourceFactory factory;
	private IClient client;
	private WebSocketClient wsClient;
	
	public WatchClient(URL baseUrl, Map<String, String> typeMappings, IClient client) {
		this.baseUrl = baseUrl;
		this.typeMappings = typeMappings;
		this.factory = client.getResourceFactory();
		this.client = client;
		wsClient = newWebSocketClient();
	}
	
	private class WatchEndpoint extends WebSocketAdapter{
		private IOpenShiftWatchListener listener;

		public WatchEndpoint(IOpenShiftWatchListener listener) {
			this.listener = listener;
		}

		@Override
		public void onWebSocketClose(int statusCode, String reason) {
			LOGGER.debug("WatchSocket closed");
			super.onWebSocketClose(statusCode, reason);
			listener.disconnected();
		}

		@Override
		public void onWebSocketConnect(Session session) {
			LOGGER.debug("WatchSocket connected");
			super.onWebSocketConnect(session);
			listener.connected();
		}

		@Override
		public void onWebSocketError(Throwable err) {
			LOGGER.error("WatchSocket Error", err);
			listener.error(createOpenShiftException("WatchSocket Error", err));
		}

		@Override
		public void onWebSocketText(String message) {
			KubernetesResource payload = factory.create(message);
			IOpenShiftWatchListener.ChangeType event = IOpenShiftWatchListener.ChangeType.valueOf(payload.getNode().get("type").asString());
			listener.received(factory.create(payload.getNode().get("object").toJSONString(true)), event);
		}
	}
	
	public IWatcher watch(String kind, String namespace, IOpenShiftWatchListener listener) {
		try {
			final String resourceVersion = getResourceVersion(kind, namespace);
			wsClient.start();
			ClientUpgradeRequest request = newRequest(this.client.getAuthorizationStrategy().getToken());	
			
			WatchEndpoint socket = new WatchEndpoint(listener);
			final String endpoint = new URLBuilder(baseUrl, typeMappings)
					.kind(kind)
					.namespace(namespace)
					.watch()
					.addParmeter("resourceVersion", resourceVersion)
					.websocket();
			wsClient.connect(socket, new URI(endpoint), request).get();
		} catch (Exception e) {
			throw createOpenShiftException(String.format("Could not watch %s resource in namespace %s: %s", kind, namespace, e.getMessage()), e);
		}
		return this;
	}
	
	@Override
	public void stop(){
		try {
			wsClient.stop();
		} catch (Exception e) {
			LOGGER.error("Unable to stop the watch client",e);
		}
	}


	
	private ClientUpgradeRequest newRequest(final String token) {
		ClientUpgradeRequest request = new ClientUpgradeRequest();
		request.setHeader("Origin", baseUrl.toString());
		request.setHeader("User-Agent", "openshift-restclient-java");
		request.setHeader("Authorization", "Bearer " + token);	
		return request;
	}
	
	private WebSocketClient newWebSocketClient() {
		SslContextFactory factory = new SslContextFactory();
		factory.setTrustAll(true);
		WebSocketClient client = new WebSocketClient(factory);
		return client;
	}
	
	private String getResourceVersion(String kind, String namespace) throws Exception{
		IList list = client.get(kind, namespace);
		return list.getMetadata().get("resourceVersion");
	}
	
	private OpenShiftException createOpenShiftException(String message, Throwable e) {
		LOGGER.debug(message, e);
		int responseCode = 0;
		if(e instanceof UpgradeException) {
			UpgradeException ex = (UpgradeException)e;
			responseCode = ex.getResponseStatusCode();
		}
		switch(responseCode) {
		case STATUS_FORBIDDEN:
			return new ResourceForbiddenException("Resource Forbidden", e);
		case STATUS_UNAUTHORIZED:
			return new com.openshift.restclient.authorization.UnauthorizedException(client.getAuthorizationDetails(this.baseUrl.toString()));
		default:
			return new OpenShiftException(e, message);
		}
	}
	
}
