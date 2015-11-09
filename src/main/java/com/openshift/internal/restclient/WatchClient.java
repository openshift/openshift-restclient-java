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

import java.io.IOException;
import java.net.URL;
import java.nio.CharBuffer;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncCharConsumer;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.http.UnauthorizedException;
import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.internal.restclient.model.Status;
import com.openshift.restclient.IOpenShiftWatchListener;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.ISSLCertificateCallback;
import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.authorization.IAuthorizationClient;
import com.openshift.restclient.authorization.IAuthorizationStrategy;
import com.openshift.restclient.authorization.ResourceForbiddenException;
import com.openshift.restclient.http.IHttpStatusCodes;
import com.openshift.restclient.model.IResource;

/**
  * Encapsulation of the logic to handle watching resources.
  * @author Jeff Cantrill
  *
  */
public class WatchClient implements IHttpStatusCodes{
	private static final Logger LOGGER = LoggerFactory.getLogger(WatchClient.class);
	private URL baseUrl;
	private Map<String, String> typeMappings;
	private IResourceFactory factory;
	private IAuthorizationStrategy strategy;
	private IAuthorizationClient authClient;
	private SSLContext sslContext;
	private SSLCertificateCallbackAdapter sslCertAdapter;
	
	public WatchClient(URL baseUrl, Map<String, String> typeMappings, IResourceFactory factory, IAuthorizationStrategy strategy, IAuthorizationClient authClient, ISSLCertificateCallback certCallback) {
		this.baseUrl = baseUrl;
		this.typeMappings = typeMappings;
		this.factory = factory;
		this.strategy = strategy;
		this.authClient = authClient;
		sslCertAdapter = new SSLCertificateCallbackAdapter(certCallback);
		try {
			sslContext = SSLContexts.custom()
					.loadTrustMaterial(null, sslCertAdapter).build();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			throw createOpenShiftException("Unable to initialize watch:",e);
		}
	}
	
	public void watch(String kind, String namespace, IOpenShiftWatchListener listener) {
		try {
			final String token = strategy.getToken();
			final String resourceVersion = getResourceVersion(kind, namespace, token);
			try(CloseableHttpAsyncClient httpClient = HttpAsyncClients
					.custom()
					.setSSLContext(sslContext)
					.setHostnameVerifier(sslCertAdapter).build()){
				final String endpoint = new URLBuilder(baseUrl, typeMappings)
						.kind(kind)
						.namespace(namespace)
						.watch()
						.addParmeter("resourceVersion", resourceVersion)
						.build().toString();
				HttpUriRequest get = buildRequest(endpoint.toString(), token);
				
				final CountDownLatch latch = new CountDownLatch(1);
				HttpAsyncRequestProducer producer = HttpAsyncMethods.create(get);
				AsyncCharConsumer<HttpResponse> consumer = new AsyncCharConsumer<HttpResponse>() {
					HttpResponse response;
					@Override
					protected void onCharReceived(CharBuffer buf, IOControl ioctrl) throws IOException {
						KubernetesResource payload = factory.create(buf.toString());
						IOpenShiftWatchListener.ChangeType event = IOpenShiftWatchListener.ChangeType.valueOf(payload.getNode().get("type").asString());
						listener.received(factory.create(payload.getNode().get("object").toJSONString(true)), event);
					}

					@Override
					protected void onResponseReceived(HttpResponse response) throws HttpException, IOException {
						this.response = response;
					}

					@Override
					protected HttpResponse buildResult(HttpContext context) throws Exception {
						return response;
					}
					
				};
				httpClient.start();
				httpClient.execute(producer, consumer, new FutureCallback<HttpResponse>() {

			        public void completed(final HttpResponse response3) {
			        	LOGGER.debug(get.getRequestLine() + " " + response3.getStatusLine());
			        	latch.countDown();
			        }

			        public void failed(final Exception ex) {
			        	LOGGER.error(get.getRequestLine().toString(), ex);
			        	latch.countDown();
			        }

			        public void cancelled() {
			        	LOGGER.debug(get.getRequestLine() + " cancelled");
			        	latch.countDown();
			        }

			    });
				listener.started();
				latch.await();
				listener.stopped();
			}
				
		} catch (Exception e) {
			throw createOpenShiftException(String.format("Could not watch %s resource in namespace %s: %s", kind, namespace, e.getMessage()), e);
		}
	}
	
	private String getResourceVersion(String kind, String namespace, String token) throws Exception{
		HttpClientBuilder builder = HttpClients.custom().setSslcontext(sslContext).setHostnameVerifier(sslCertAdapter);
		try(CloseableHttpClient client = builder.build()){
			final URL endpoint = new URLBuilder(baseUrl, typeMappings)
					.kind(kind)
					.namespace(namespace)
					.build();
			HttpUriRequest get = buildRequest(endpoint.toString(), token);
			String response = client.execute(get, new BasicResponseHandler());
			LOGGER.debug(String.format("List Response: %s:", response));
			IResource list = factory.create(response);
			return list.getMetadata().get("resourceVersion");
		}
	}
	
	private HttpUriRequest buildRequest(String endpoint, String token) {
		HttpGet request = new HttpGet(endpoint);
		request.addHeader("User-Agent", "openshift-restclient-java");
		request.addHeader("Authorization", "Bearer " + token);	
		return request;
	}
	
	private OpenShiftException createOpenShiftException(String message, Exception e) {
		LOGGER.debug(message, e);
		final String token = strategy != null ? strategy.getToken() : "";
		if (e.getMessage() != null
				&& e.getMessage().startsWith("{")) {
			Status status = factory.create(e.getMessage());
			if(status.getCode() == STATUS_FORBIDDEN) {
				if(StringUtils.isNotBlank(token)) { //truly forbidden
					return new ResourceForbiddenException(status.getMessage(), e);
				}else {
					return new com.openshift.restclient.authorization.UnauthorizedException(authClient.getAuthorizationDetails(this.baseUrl.toString()));
				}
			}
			return new OpenShiftException(e, status, message);
		} else {
			if(e instanceof UnauthorizedException) {
				return new com.openshift.restclient.authorization.UnauthorizedException(authClient.getAuthorizationDetails(this.baseUrl.toString()));
			}
			return new OpenShiftException(e, message);
		}
	}

}
