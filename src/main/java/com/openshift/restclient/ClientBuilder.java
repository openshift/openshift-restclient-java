/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.restclient;

import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.openshift.internal.restclient.DefaultClient;
import com.openshift.internal.restclient.ResourceFactory;
import com.openshift.internal.restclient.authorization.AuthorizationContext;
import com.openshift.internal.restclient.okhttp.OpenShiftAuthenticator;
import com.openshift.internal.restclient.okhttp.ResponseCodeInterceptor;
import com.openshift.restclient.http.IHttpConstants;
import com.openshift.restclient.utils.SSLUtils;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

/**
 * Builder to create IClient instances.
 * @author jeff.cantrill
 *
 */
public class ClientBuilder {
	
	private String baseUrl;
	private ISSLCertificateCallback sslCertificateCallback = new NoopSSLCertificateCallback();
	private X509Certificate certificate;
	private String certificateAlias;
	private IResourceFactory resourceFactory;
	private String userName;
	private String token;
	private String password;
	
	private int maxRequests = 64;
	private int maxRequestsPerHost = 10;
	
	private int readTimeout = IHttpConstants.DEFAULT_READ_TIMEOUT;
	private TimeUnit readTimeoutUnit = TimeUnit.MILLISECONDS;
	private int connectTimeout = IHttpConstants.DEFAULT_READ_TIMEOUT;
	private TimeUnit connectTimeoutUnit = TimeUnit.MILLISECONDS;
	private int writeTimeout = IHttpConstants.DEFAULT_READ_TIMEOUT;
	private TimeUnit writeTimeoutUnit = TimeUnit.MILLISECONDS;

	public ClientBuilder() {
		this(null);
	}

	public ClientBuilder(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public ClientBuilder sslCertificateCallback(ISSLCertificateCallback callback) {
		this.sslCertificateCallback = callback == null ? new NoopSSLCertificateCallback() : callback;
		return this;
	}
	
	public ClientBuilder sslCertificate(String alias, X509Certificate cert) {
		this.certificateAlias = alias;
		this.certificate = cert;
		return this;
	}
	
	public ClientBuilder resourceFactory(IResourceFactory factory) {
		this.resourceFactory = factory;
		return this;
	}

	public ClientBuilder toCluster(String baseUrl) {
		this.baseUrl = baseUrl;
		return this;
	}

	public ClientBuilder withUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public ClientBuilder withPassword(String password) {
		this.password = password;
		return this;
	}
	
	public ClientBuilder usingToken(String token) {
		this.token = token;
		return this;
	}

	public ClientBuilder withConnectTimeout(int timeout, TimeUnit unit) {
		this.connectTimeout = timeout;
		this.connectTimeoutUnit = unit;
		return this;
	}
	public ClientBuilder withReadTimeout(int timeout, TimeUnit unit) {
		this.readTimeout = timeout;
		this.readTimeoutUnit = unit;
		return this;
	}
	public ClientBuilder withWriteTimeout(int timeout, TimeUnit unit) {
		this.writeTimeout = timeout;
		this.writeTimeoutUnit = unit;
		return this;
	}
	
	/**
	 * The connect timeout parameter used for establishing
	 * the connection to a remote server
	 * @param connectInMillis  A value in milliseconds
	 * @return
	 */
	public ClientBuilder withConnectTimeout(int connectInMillis) {
		this.connectTimeout = connectInMillis;
		return this;
	}
	
	/**
	 * Build a client using the config loading rules defined http://janetkuo.github.io/kubernetes/v1.0/docs/user-guide/kubeconfig-file.html.  Brief summary
	 * of loading order:
	 * 
	 * 1. use explicit values set in builder
	 *   a. username/token
	 *   b. authStrategy
	 * 2. currentContext of config file located at $KUBECONFIG
	 * 3. currentContext of config file located at ~/.kube/config 
	 * 
	 * @return
	 * @throws KeyManagementException 
	 */
	public IClient build() {
		try {
			TrustManagerFactory trustManagerFactory = initTrustManagerFactory(certificateAlias, certificate);
			X509TrustManager trustManager  = getCurrentTrustManager(trustManagerFactory);
			SSLContext sslContext = SSLUtils.getSSLContext(trustManager);

			ResponseCodeInterceptor responseCodeInterceptor = new ResponseCodeInterceptor();
			OpenShiftAuthenticator authenticator = new OpenShiftAuthenticator();
			Dispatcher dispatcher = new Dispatcher();
			
			//hiding these for now to since not certain
			//if we need to really expose them.
			dispatcher.setMaxRequests(maxRequests);
			dispatcher.setMaxRequestsPerHost(maxRequestsPerHost);

			OkHttpClient.Builder builder = new OkHttpClient.Builder()
				.addInterceptor(responseCodeInterceptor)
				.authenticator(authenticator)
				.dispatcher(dispatcher)
				.readTimeout(readTimeout, readTimeoutUnit)
				.writeTimeout(writeTimeout, writeTimeoutUnit)
				.connectTimeout(connectTimeout, connectTimeoutUnit)
				.hostnameVerifier(this.sslCertificateCallback)
				.sslSocketFactory(sslContext.getSocketFactory(), trustManager);
			OkHttpClient okClient = builder.build();
			
			IResourceFactory factory = defaultIfNull(resourceFactory, new ResourceFactory(null));
			AuthorizationContext authContext = new AuthorizationContext(token, userName, password);
			DefaultClient client = new DefaultClient(new URL(this.baseUrl), okClient, factory, null, authContext);
			
			authContext.setClient(client);
			responseCodeInterceptor.setClient(client);
			authenticator.setClient(client);
			authenticator.setOkClient(okClient);
			
			return client;
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | CertificateException | IOException e) {
			throw new OpenShiftException(e, "Unable to initialize client");
		}
	}
	
	private <T> T defaultIfNull(T value, T aDefault) {
		if(value != null)
			return value;
		return aDefault;
	}
	
	private X509TrustManager getCurrentTrustManager(TrustManagerFactory trustManagerFactory) throws NoSuchAlgorithmException, KeyStoreException {
		for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
			if (trustManager instanceof X509TrustManager) {
				X509TrustManager x509TrustManager = (X509TrustManager) trustManager;
				return new CallbackTrustManager(x509TrustManager, this.sslCertificateCallback);
			}
		}
		return null;
		
	}
	
	private TrustManagerFactory initTrustManagerFactory(String alias, X509Certificate cert) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			if (alias != null && cert != null) {
				KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
				// need this load to initialize the key store, and allow for the subsequent set certificate entry
				ks.load(null, null);
				cert.checkValidity();
				ks.setCertificateEntry(alias, cert);
				// testing has proven that you can only call init() once for a TrustManagerFactory wrt loading certs
				// from the KeyStore ... subsequent KeyStore.setCertificateEntry / TrustManagerFactory.init calls are 
				// ignored.
				// So if a specific cert is required to validate this connection's communication with the server, add it up front
				// in the ctor.
				trustManagerFactory.init(ks);
			} else {
				trustManagerFactory.init((KeyStore)null);
			}
			return trustManagerFactory;
	}
	
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
