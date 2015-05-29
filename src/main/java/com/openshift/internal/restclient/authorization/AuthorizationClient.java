/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.authorization;

import java.io.Closeable;
import java.io.IOException;
import java.net.ProxySelector;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.restclient.IClient;
import com.openshift.restclient.ISSLCertificateCallback;
import com.openshift.restclient.NoopSSLCertificateCallback;
import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.authorization.IAuthorizationClient;
import com.openshift.restclient.authorization.IAuthorizationContext;
import com.openshift.restclient.authorization.IAuthorizationDetails;
import com.openshift.restclient.authorization.IAuthorizationStrategy;
import com.openshift.restclient.authorization.ResourceForbiddenException;
import com.openshift.restclient.authorization.TokenAuthorizationStrategy;
import com.openshift.restclient.authorization.UnauthorizedException;
import com.openshift.restclient.http.IHttpClient;

/**
 * @author Jeff Cantrill
 */
public class AuthorizationClient implements IAuthorizationClient {
	private static final Logger LOG = LoggerFactory.getLogger(IAuthorizationClient.class);
	
	private SSLContext sslContext;
	private X509HostnameVerifier hostnameVerifier = new AllowAllHostnameVerifier();
	private IClient openshiftClient;


	public AuthorizationClient(IClient client) {
		this.openshiftClient = client;
		setSSLCertificateCallback(new NoopSSLCertificateCallback());
	}
	
	
	@Override
	public IAuthorizationDetails getAuthorizationDetails(final String baseURL) {
		try {
			getContextUsingCredentials(baseURL, null);
			return new AuthorizationDetails(String.format("%s/oauth/token/request", baseURL));
		}catch(UnauthorizedException e) {
			return e.getAuthorizationDetails();
		}
	}
	
	@Override
	public IAuthorizationContext getContext(final String baseURL) {
		OpenShiftCredentialsProvider credentialsProvider = new OpenShiftCredentialsProvider();
		openshiftClient.getAuthorizationStrategy().accept(credentialsProvider);
		final IAuthorizationStrategy configuredAuthStrategy = openshiftClient.getAuthorizationStrategy();
		try {
			final String token = credentialsProvider.getToken();
			openshiftClient.setAuthorizationStrategy(new TokenAuthorizationStrategy(token));
			return new AuthorizationContext(token, null, openshiftClient.getCurrentUser(), credentialsProvider.getScheme());
		}catch(ResourceForbiddenException e) {
			//the response if token is invalid because we tried to
			//get the current user
		}catch(UnauthorizedException e) {
			openshiftClient.setAuthorizationStrategy(configuredAuthStrategy);
			return getContextUsingCredentials(baseURL, credentialsProvider);
		}finally{
			openshiftClient.setAuthorizationStrategy(configuredAuthStrategy);
		}
		return getContextUsingCredentials(baseURL, credentialsProvider);
		
	}
	
	private IAuthorizationContext getContextUsingCredentials(final String baseURL, CredentialsProvider credentialsProvider) {
		
		CloseableHttpResponse response = null;
		CloseableHttpClient client = null;
		try {
			OpenShiftAuthorizationRedirectStrategy redirectStrategy = new OpenShiftAuthorizationRedirectStrategy(openshiftClient);
			client = HttpClients.custom()
					.setRedirectStrategy(redirectStrategy)
					.setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault()))
					.setHostnameVerifier(hostnameVerifier)
					.setDefaultCredentialsProvider(credentialsProvider)
					.setSslcontext(sslContext)
					.build();
			HttpGet request =
					new HttpGet(
							new URIBuilder(String.format("%s/oauth/authorize", baseURL))
							.addParameter("response_type", "token")
							.addParameter("client_id", "openshift-challenging-client")
							.build());
			request.addHeader("X-CSRF-Token", "1");
			response = client.execute(request);
			return redirectStrategy.getAuthorizationContext();
		} catch (URISyntaxException e) {
			throw new OpenShiftException(e, String.format("Unvalid URI while trying to get an authorization context for server %s", baseURL));
		} catch (ClientProtocolException e) {
			throw new OpenShiftException(e, String.format("Client protocol exception while trying to get authorization context for server %s", baseURL));
		} catch (IOException e) {
			throw new OpenShiftException(e, String.format("%s while trying to get an authorization context for server %s", e.getClass().getName(), baseURL));
		} finally {
			close(response);
			close(client);
		}
		
	}

	private void close(Closeable closer) {
		if (closer == null)
			return;
		try {
			closer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setSSLCertificateCallback(ISSLCertificateCallback callback) {
		X509TrustManager trustManager = null;
		if (callback != null) {
			trustManager = createCallbackTrustManager(callback);
		}
		try {
			this.sslContext = SSLContext.getInstance("TLS");
			this.sslContext.init(null, new TrustManager[] { trustManager }, null);
		} catch (NoSuchAlgorithmException e) {
			LOG.warn("Could not install trust manager callback", e);
			this.sslContext = null;
		} catch (KeyManagementException e) {
			LOG.warn("Could not install trust manager callback", e);
			this.sslContext = null;
		}
	}

	// TODO REPLACE me with osjc impl
	private X509TrustManager createCallbackTrustManager(ISSLCertificateCallback sslAuthorizationCallback) {
		X509TrustManager trustManager = null;
		try {
			trustManager = getCurrentTrustManager();
			if (trustManager == null) {
				LOG.warn("Could not install trust manager callback, no trustmanager was found.");
			} else {
				trustManager = new CallbackTrustManager(trustManager, sslAuthorizationCallback);
			}
		} catch (GeneralSecurityException e) {
			LOG.warn("Could not install trust manager callback.", e);
		}
		return trustManager;
	}

	// TODO replace me with OSJC implementation
	private X509TrustManager getCurrentTrustManager() throws NoSuchAlgorithmException, KeyStoreException {
		TrustManagerFactory trustManagerFactory =
				TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init((KeyStore) null);

		X509TrustManager x509TrustManager = null;
		for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
			if (trustManager instanceof X509TrustManager) {
				x509TrustManager = (X509TrustManager) trustManager;
				break;
			}
		}
		return x509TrustManager;
	}

	// TODO - Replace me with instance in OSJC
	private static class CallbackTrustManager implements X509TrustManager {

		private X509TrustManager trustManager;
		private ISSLCertificateCallback callback;

		private CallbackTrustManager(X509TrustManager currentTrustManager, ISSLCertificateCallback callback)
				throws NoSuchAlgorithmException, KeyStoreException {
			this.trustManager = currentTrustManager;
			this.callback = callback;
		}

		public X509Certificate[] getAcceptedIssuers() {
			return trustManager.getAcceptedIssuers();
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			try {
				trustManager.checkServerTrusted(chain, authType);
			} catch (CertificateException e) {
				if (!callback.allowCertificate(chain)) {
					throw e;
				}
			}
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			trustManager.checkServerTrusted(chain, authType);
		}
	}
}
