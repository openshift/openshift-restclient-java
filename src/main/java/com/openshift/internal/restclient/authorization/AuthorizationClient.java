/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.authorization;

import static com.openshift.internal.util.URIUtils.splitFragment;

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
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.restclient.ISSLCertificateCallback;
import com.openshift.restclient.NoopSSLCertificateCallback;
import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.authorization.IAuthorizationClient;
import com.openshift.restclient.authorization.IAuthorizationContext;

/**
 * @author Jeff Cantrill
 */
public class AuthorizationClient implements IAuthorizationClient {
	private static final Logger LOG = LoggerFactory.getLogger(IAuthorizationClient.class);
	
	public static final String ACCESS_TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private SSLContext sslContext;
	private X509HostnameVerifier hostnameVerifier = new AllowAllHostnameVerifier();

	public AuthorizationClient() {
		setSSLCertificateCallback(new NoopSSLCertificateCallback());
	}

	@Override
	public IAuthorizationContext getContext(final String baseURL, final String username, final String password) {
		CloseableHttpResponse response = null;
		CloseableHttpClient client = null;
		try {
			OpenShiftAuthorizationRedirectStrategy redirectStrategy = new OpenShiftAuthorizationRedirectStrategy();
			client = HttpClients.custom()
					.setRedirectStrategy(redirectStrategy)
					.setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault()))
					.setDefaultCredentialsProvider(buildCredentialsProvider(username, password))
					.setHostnameVerifier(hostnameVerifier)
					.setSslcontext(sslContext)
					.build();
			HttpGet request =
					new HttpGet(
							new URIBuilder(String.format("%s/oauth/authorize", baseURL))
									.addParameter("response_type", "token")
									.addParameter("client_id", "openshift-challenging-client")
									.build());
			response = client.execute(request);
			return createAuthorizationConext(response, redirectStrategy.isAuthorized());
		} catch (URISyntaxException e) {
			throw new OpenShiftException(e, String.format("Could not authorize user %s on server at %s", username, baseURL));
		} catch (ClientProtocolException e) {
			throw new OpenShiftException(e, String.format("Could not authorize user %s on server at %s", username, baseURL));
		} catch (IOException e) {
			throw new OpenShiftException(e, String.format("Could not authorize user %s on server at %s", username, baseURL));
		} finally {
			close(response);
			close(client);
		}
	}

	private IAuthorizationContext createAuthorizationConext(CloseableHttpResponse response, boolean authorized) {
		if (!authorized) {
			return new AuthorizationContext(IAuthorizationContext.AuthorizationType.Basic);
		}
		Header header = response.getFirstHeader("Location");
		Map<String, String> fragment = splitFragment(header.getValue());
		return new AuthorizationContext(fragment.get(ACCESS_TOKEN), fragment.get(EXPIRES));
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

	private CredentialsProvider buildCredentialsProvider(final String username, final String password) {
		CredentialsProvider provider = new BasicCredentialsProvider();
		provider.setCredentials(
				// TODO: limit scope on host?
				new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
				new UsernamePasswordCredentials(username, password));
		return provider;
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
