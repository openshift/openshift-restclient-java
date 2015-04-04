/******************************************************************************* 
 * Copyright (c) 2013-2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client.httpclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.client.HttpMethod;
import com.openshift.client.IHttpClient;
import com.openshift.client.utils.SSLUtils;
import com.openshift.internal.client.httpclient.request.IMediaType;
import com.openshift.internal.client.httpclient.request.Parameter;
import com.openshift.internal.client.httpclient.request.ParameterValueMap;
import com.openshift.restclient.authorization.IAuthorizationStrategy;
import com.openshift.restclient.authorization.URLConnectionRequest;
import com.openshift.restclient.model.IResource;

/**
 * @author Andre Dietisheim
 * @author Nicolas Spano
 * @author Corey Daley
 * @author Sean Kavanagh
 */
public class UrlConnectionHttpClient implements IHttpClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(UrlConnectionHttpClient.class);

	protected String userAgent;
	protected String acceptedMediaType;
	protected String acceptedVersion;
	protected ISSLCertificateCallback sslAuthorizationCallback;
	protected Integer configTimeout;
	private String excludedSSLCipherRegex;
	private IAuthorizationStrategy authStrategy;

	public UrlConnectionHttpClient(String userAgent, String acceptedMediaType, String version){
		this(userAgent, acceptedMediaType, version, null, null, null);
	}

	public UrlConnectionHttpClient(String userAgent, String acceptedMediaType,
			String version, ISSLCertificateCallback callback, Integer configTimeout, String excludedSSLCipherRegex) {
		this.userAgent = userAgent;
		this.acceptedMediaType = acceptedMediaType;
		this.acceptedVersion = version;
		this.sslAuthorizationCallback = callback;
		this.configTimeout = configTimeout;
		this.excludedSSLCipherRegex = excludedSSLCipherRegex;
	}
	
	@Override
	public void setAuthorizationStrategy(IAuthorizationStrategy strategy) {
		this.authStrategy = strategy;
	}

	@Override
	public String get(URL url, int timeout) throws HttpClientException, SocketTimeoutException {
		return request(HttpMethod.GET, url, null, timeout);
	}

	@Override
	public String head(URL url, int timeout) throws HttpClientException, SocketTimeoutException {
		return request(HttpMethod.HEAD, url, null, timeout);
	}

	public String put(URL url, IMediaType mediaType, int timeout, Parameter... parameters)
			throws HttpClientException, SocketTimeoutException, EncodingException {
		return request(HttpMethod.PUT, url, mediaType, timeout, parameters);
	}

	@Override
	public String put(URL url, int timeout, IResource resource)
			throws HttpClientException, SocketTimeoutException, EncodingException {
		return request(HttpMethod.PUT, url, timeout, resource);
	}
	
	@Override
	public String post(URL url, int timeout, IResource resource) throws HttpClientException, SocketTimeoutException, EncodingException {
		return request(HttpMethod.POST, url, timeout, resource);
	}

	public String delete(URL url, IMediaType mediaType, int timeout, Parameter... parameters)
			throws HttpClientException, SocketTimeoutException, EncodingException {
		return request(HttpMethod.DELETE, url, mediaType, timeout, parameters);
	}
	
	@Override
	public String delete(URL url, int timeout)
			throws HttpClientException, SocketTimeoutException, EncodingException {
		return delete(url, null, timeout);
	}

	protected String request(HttpMethod httpMethod, URL url, IMediaType requestMediaType, int timeout,
			Parameter... parameters)
			throws SocketTimeoutException, HttpClientException {
		return request(httpMethod, url, requestMediaType, timeout, new ParameterValueMap(parameters));
	}

	protected String request(HttpMethod httpMethod, URL url, IMediaType requestMediaType, int timeout,
			ParameterValueMap parameters)
			throws SocketTimeoutException, HttpClientException {
		HttpURLConnection connection = null;
		try {
			connection = createConnection(
					url, userAgent, acceptedVersion, acceptedMediaType, sslAuthorizationCallback, timeout);
			// PATCH not yet supported by JVM
			setRequestMethod(httpMethod, connection);
			if (!parameters.isEmpty()) {
				connection.setDoOutput(true);
				setRequestMediaType(requestMediaType, connection);
				requestMediaType.writeTo(parameters, connection.getOutputStream());
			}
			return IOUtils.toString(connection.getInputStream(), "UTF-8");
		} catch (SocketTimeoutException e) {
			throw e;
		} catch (IOException e) {
			throw createException(e, connection);
		} finally {
			disconnect(connection);
		}
	}
	
	protected String request(HttpMethod httpMethod, URL url, int timeout, IResource resource)
			throws SocketTimeoutException, HttpClientException {
		HttpURLConnection connection = null;
		try {
			connection = createConnection(
					url, userAgent, acceptedVersion, acceptedMediaType, sslAuthorizationCallback, timeout);
			// PATCH not yet supported by JVM
			setRequestMethod(httpMethod, connection);
			if(resource != null){
				connection.setDoOutput(true);
				PrintWriter writer = new PrintWriter(connection.getOutputStream());
				writer.write(resource.toString());
				writer.flush();
			}
			return IOUtils.toString(connection.getInputStream(), "UTF-8");
		} catch (SocketTimeoutException e) {
			throw e;
		} catch (IOException e) {
			throw createException(e, connection);
		} finally {
			disconnect(connection);
		}
	}

	private void setRequestMethod(HttpMethod httpMethod, HttpURLConnection connection) throws ProtocolException {
		if (httpMethod == HttpMethod.PATCH) {
			httpMethod = HttpMethod.POST;
			connection.setRequestProperty("X-Http-Method-Override", "PATCH");
		}
		connection.setRequestMethod(httpMethod.toString());
	}
	
	private void disconnect(HttpURLConnection connection) {
		if (connection != null) {
			connection.disconnect();
		}
	}

	private HttpClientException createException(IOException ioe, HttpURLConnection connection)
			throws SocketTimeoutException {
		try {
			int responseCode = connection.getResponseCode();
			String errorMessage = createErrorMessage(ioe, connection);
			switch (responseCode) {
			case STATUS_INTERNAL_SERVER_ERROR:
				return new InternalServerErrorException(errorMessage, ioe);
			case STATUS_BAD_REQUEST:
				return new BadRequestException(errorMessage, ioe);
			case STATUS_UNAUTHORIZED:
				return new UnauthorizedException(errorMessage, ioe);
			case STATUS_NOT_FOUND:
				return new NotFoundException(errorMessage, ioe);
			default:
				return new HttpClientException(errorMessage, ioe);
			}
		} catch (SocketTimeoutException e) {
			throw e;
		} catch (IOException e) {
			return new HttpClientException(e);
		}
	}

	protected String createErrorMessage(IOException ioe, HttpURLConnection connection) throws IOException {
		String errorMessage = IOUtils.toString(connection.getErrorStream());
		if (!StringUtils.isEmpty(errorMessage)) {
			return errorMessage;
		}
		StringBuilder builder = new StringBuilder("Connection to ")
				.append(connection.getURL());
		String reason = connection.getResponseMessage();
		if (!StringUtils.isEmpty(reason)) {
			builder.append(": ").append(reason);
		}
		return builder.toString();
	}

	private boolean isHttps(URL url) {
		return "https".equals(url.getProtocol());
	}

	protected HttpURLConnection createConnection(URL url, String userAgent, String acceptedVersion, String acceptedMediaType,
			ISSLCertificateCallback callback, int timeout)
			throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		if (isHttps(url)) {
			HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
			SSLContext sslContext = setSSLCallback(sslAuthorizationCallback, url, httpsConnection);
			setFilteredCiphers(excludedSSLCipherRegex, sslContext, httpsConnection);
		}
		setAuthorization(connection);
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setAllowUserInteraction(false);
		setConnectTimeout(NO_TIMEOUT, connection);
		setReadTimeout(timeout, connection);
		// wont work when switching http->https
		// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4620571
		connection.setInstanceFollowRedirects(true);
		setUserAgent(userAgent, connection);
		setAcceptHeader(acceptedVersion, acceptedMediaType, connection);

		return connection;
	}

	private void setUserAgent(String userAgent, HttpURLConnection connection) {
		if (!StringUtils.isEmpty(userAgent)) {
			connection.setRequestProperty(PROPERTY_USER_AGENT, userAgent);
		}
	}

	private void setAcceptHeader(String acceptedVersion, String acceptedMediaType, HttpURLConnection connection) {
		if (StringUtils.isEmpty(acceptedMediaType)) {
			throw new HttpClientException(MessageFormat.format(
					"Accepted media type (ex. {0}) is not defined", MEDIATYPE_APPLICATION_JSON));
		}

		StringBuilder builder = new StringBuilder(acceptedMediaType);
		if (acceptedVersion != null) {
			builder.append(SEMICOLON).append(SPACE)
					.append(VERSION).append(EQUALS).append(acceptedVersion);
		}

		connection.setRequestProperty(PROPERTY_ACCEPT, builder.toString());
	}

	protected final void setAuthorization(HttpURLConnection connection) {
		if(authStrategy != null){
			authStrategy.authorize(new URLConnectionRequest(connection));
			return;
		}
	}

	private SSLContext setSSLCallback(ISSLCertificateCallback sslAuthorizationCallback, URL url, HttpsURLConnection connection) {
		X509TrustManager trustManager = null;
		if (sslAuthorizationCallback != null) {
			connection.setHostnameVerifier(new CallbackHostnameVerifier());
			trustManager = createCallbackTrustManager(sslAuthorizationCallback, connection);
		}

		try {
			SSLContext sslContext = SSLUtils.getSSLContext(trustManager);
			connection.setSSLSocketFactory(sslContext.getSocketFactory());
			return sslContext;
		} catch (GeneralSecurityException e) {
			LOGGER.warn("Could not install trust manager callback", e);;
			return null;
		}
	}

	/**
	 * Returns the callback trustmanager or <code>null</code> if it could not be created.
	 * 
	 * @see ISSLCertificateCallback
	 */
	private X509TrustManager createCallbackTrustManager(ISSLCertificateCallback sslAuthorizationCallback,HttpsURLConnection connection) {
		X509TrustManager trustManager = null;
		try {
			trustManager = getCurrentTrustManager();
			if (trustManager == null) {
				LOGGER.warn("Could not install trust manager callback, no trustmanager was found.", trustManager);
			} else {
				trustManager = new CallbackTrustManager(trustManager, sslAuthorizationCallback);
			}
		} catch (GeneralSecurityException e) {
			LOGGER.warn("Could not install trust manager callback.", e);;
		}
		return trustManager;
	}
		
	/**
	 * Sets a ssl socket factory that sets a filtered list of ciphers based on
	 * the #excludedSSLCipherRegex to the given connection.
	 * 
	 * @param sslContext
	 * 
	 * @param sslContext
	 *            the ssl context that shall be used
	 * @param url
	 *            the url we are connecting to
	 * @param connection
	 *            the connection that the cipher filter shall be applied to
	 */
	protected SSLContext setFilteredCiphers(String excludedSSLCipherRegex, SSLContext sslContext, HttpsURLConnection connection) {
		if (excludedSSLCipherRegex != null) {
			connection.setSSLSocketFactory(
					new EnabledCiphersSSLSocketFactory(
							SSLUtils.filterCiphers(
									excludedSSLCipherRegex, getSupportedCiphers(sslContext)), sslContext
									.getSocketFactory()));
		}
		return sslContext;
	}

	protected String[] getSupportedCiphers(SSLContext sslContext) {
		return sslContext.getSupportedSSLParameters().getCipherSuites();
	}

	private void setConnectTimeout(int timeout, URLConnection connection) {
		if (getTimeout(timeout) != NO_TIMEOUT) {
			connection.setConnectTimeout(getTimeout(timeout));
		}
	}

	private void setReadTimeout(int timeout, URLConnection connection) {
		if (getTimeout(timeout) != NO_TIMEOUT) {
			connection.setReadTimeout(getTimeout(timeout));
		}
	}

	private int getTimeout(int timeout) {
			if (timeout == NO_TIMEOUT) {
				if (configTimeout != null) {
					timeout = this.configTimeout;
				}
			}
		return timeout;
	}

	private void setRequestMediaType(IMediaType mediaType, HttpURLConnection connection) {
		if (mediaType == null
				|| StringUtils.isEmpty(mediaType.getType())) {
			throw new HttpClientException(
					MessageFormat.format("Request media type (ex. {0}) is not defined",
							MEDIATYPE_APPLICATION_FORMURLENCODED));
		}
		connection.setRequestProperty(PROPERTY_CONTENT_TYPE, mediaType.getType());	
	}
	
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
	
	@Override
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	@Override
	public void setAcceptVersion(String version) {
		this.acceptedVersion = version;
	}

	@Override
	public void setAcceptedMediaType(String acceptedMediaType) {
		this.acceptedMediaType = acceptedMediaType;
	}
	
	public class CallbackTrustManager implements X509TrustManager {

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

	private class CallbackHostnameVerifier implements HostnameVerifier {

		@Override
		public boolean verify(String hostname, SSLSession session) {
			return sslAuthorizationCallback.allowHostname(hostname, session);
		}
	}
	
	/**
	 * SSL socket factory that wraps a given socket factory and sets given ciphers
	 * to the socket that the wrapped factory creates.
	 * 
	 * @see http://stackoverflow.com/questions/6851461/java-why-does-ssl-handshake-give-could-not-generate-dh-keypair-exception/16686994#16686994
	 */
	private static class EnabledCiphersSSLSocketFactory extends SSLSocketFactory {
		
		private String[] enabledCiphers;
		private SSLSocketFactory socketFactory;

		EnabledCiphersSSLSocketFactory(String[] enabledCiphers, SSLSocketFactory socketFactory) {
			this.enabledCiphers = enabledCiphers;
			this.socketFactory = socketFactory;
		}

		@Override
		public Socket createSocket(InetAddress host, int port, InetAddress localHost, int localPort) throws IOException {
			return setEnabledCiphers((SSLSocket) socketFactory.createSocket(host, port, localHost, localPort));
		}
		
		@Override
		public Socket createSocket(String host, int port, InetAddress localHost, int localPort) 
				throws IOException, UnknownHostException {
			return setEnabledCiphers((SSLSocket) socketFactory.createSocket(host, port, localHost, localPort));
		}
		
		@Override
		public Socket createSocket(InetAddress host, int port) throws IOException {
			return setEnabledCiphers((SSLSocket) socketFactory.createSocket(host, port));
		}
		
		@Override
		public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
			return setEnabledCiphers((SSLSocket) socketFactory.createSocket(host, port));
		}
		
		@Override
		public String[] getSupportedCipherSuites() {
			if (enabledCiphers == null) {
				return socketFactory.getSupportedCipherSuites();
			} else {
				return enabledCiphers;
			}
		}
		
		@Override
		public String[] getDefaultCipherSuites() {
			return socketFactory.getDefaultCipherSuites();
		}
		
		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
			 return setEnabledCiphers((SSLSocket) socketFactory.createSocket(socket, host, port, autoClose));
		}
		
		private SSLSocket setEnabledCiphers(SSLSocket socket) {
			if (enabledCiphers == null) {
				return socket;
			}
			socket.setEnabledCipherSuites(enabledCiphers);
			return socket;
		}
	}
	
}
