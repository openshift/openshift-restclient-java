/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.client.HttpMethod;
import com.openshift.client.IHttpClient;
import com.openshift.client.utils.Base64Coder;
import com.openshift.internal.client.RequestParameter;
import com.openshift.internal.client.utils.StreamUtils;
import com.openshift.internal.client.utils.StringUtils;

/**
 * @author Andre Dietisheim
 * @author Nicolas Spano
 */
public class UrlConnectionHttpClient implements IHttpClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(UrlConnectionHttpClient.class);

	private static final String SYSPROP_OPENSHIFT_CONNECT_TIMEOUT = "com.openshift.httpclient.timeout";
	private static final String SYSPROP_DEFAULT_CONNECT_TIMEOUT = "sun.net.client.defaultConnectTimeout";
	private static final String SYSPROP_DEFAULT_READ_TIMEOUT = "sun.net.client.defaultReadTimeout";

	private static final String USERAGENT_FOR_KEYAUTH = "OpenShift";

	private String userAgent;
	private boolean sslChecks;
	private String username;
	private String password;
	private String authKey;
	private String authIV;
	private IMediaType requestMediaType;
	private String acceptedMediaType;
	private String acceptVersion;

	public UrlConnectionHttpClient(String username, String password, String userAgent, boolean sslChecks,
			IMediaType requestMediaType, String acceptedMediaType, String version) {
		this(username, password, userAgent, sslChecks, requestMediaType, acceptedMediaType, version, null, null);
	}

	public UrlConnectionHttpClient(String username, String password, String userAgent, boolean sslChecks,
			IMediaType requestMediaType, String acceptedMediaType, String version, String authKey, String authIV) {
		this.username = username;
		this.password = password;
		this.userAgent = setupUserAgent(authKey, authIV, userAgent);
		this.sslChecks = sslChecks;
		this.requestMediaType = requestMediaType;
		this.acceptedMediaType = acceptedMediaType;
		this.authKey = authKey;
		this.authIV = authIV;
		this.acceptVersion = version;
	}

	/** TODO: unify with #setUserAgent **/
	private String setupUserAgent(String authKey, String authIV, String userAgent) {
		if (!StringUtils.isEmpty(authKey)) {
			if (userAgent == null) {
				userAgent = "OpenShift";
			} else if (!userAgent.startsWith("OpenShift")) {
				userAgent = "OpenShift-" + userAgent;
			}
		}
		return userAgent;
	}

	@Override
	public void setAcceptedMediaType(String acceptedMediaType) {
		this.acceptedMediaType = acceptedMediaType;
	}

	@Override
	public String getAcceptedMediaType() {
		return acceptedMediaType;
	}

	@Override
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	@Override
	public String getUserAgent() {
		return userAgent;
	}

	@Override
	public void setAcceptVersion(String version) {
		this.acceptVersion = version;
	}

	@Override
	public String getAcceptVersion() {
		return acceptVersion;
	}

	@Override
	public String get(URL url) throws HttpClientException, SocketTimeoutException {
		return get(url, NO_TIMEOUT);
	}

	@Override
	public String get(URL url, int timeout) throws HttpClientException, SocketTimeoutException {
		return request(HttpMethod.GET, url, timeout);
	}

	@Override
	public String put(URL url, RequestParameter... parameters)
			throws SocketTimeoutException, UnsupportedEncodingException, HttpClientException {
		return put(url, NO_TIMEOUT, parameters);
	}

	@Override
	public String put(URL url, int timeout, RequestParameter... parameters)
			throws HttpClientException, SocketTimeoutException, UnsupportedEncodingException {
		return request(HttpMethod.PUT, url, timeout, parameters);
	}

	protected String put(String data, URL url, RequestParameter... parameters) throws HttpClientException,
			SocketTimeoutException {
		return request(HttpMethod.PUT, url, NO_TIMEOUT, parameters);
	}

	@Override
	public String post(URL url, RequestParameter... parameters)
			throws SocketTimeoutException, UnsupportedEncodingException, HttpClientException {
		return request(HttpMethod.POST, url, NO_TIMEOUT, parameters);
	}

	@Override
	public String post(URL url, int timeout, RequestParameter... parameters)
			throws HttpClientException, SocketTimeoutException, UnsupportedEncodingException {
		return request(HttpMethod.POST, url, timeout, parameters);
	}

	@Override
	public String delete(URL url, RequestParameter... parameters) throws HttpClientException, SocketTimeoutException {
		return request(HttpMethod.DELETE, url, NO_TIMEOUT, parameters);
	}

	@Override
	public String delete(URL url, int timeout, RequestParameter... parameters)
			throws HttpClientException, SocketTimeoutException, UnsupportedEncodingException {
		return request(HttpMethod.DELETE, url, timeout, parameters);
	}

	@Override
	public String delete(URL url)
			throws HttpClientException, SocketTimeoutException, UnsupportedEncodingException {
		return delete(url, NO_TIMEOUT);
	}

	protected String request(HttpMethod httpMethod, URL url, int timeout, RequestParameter... parameters)
			throws SocketTimeoutException, HttpClientException {
		HttpURLConnection connection = null;
		try {
			connection = createConnection(username, password, authKey, authIV, userAgent, url, timeout);
			connection.setRequestMethod(httpMethod.toString());
			if (parameters != null
					&& parameters.length > 0) {
				connection.setDoOutput(true);
				requestMediaType.write(parameters, connection.getOutputStream());
			}
			return StreamUtils.readToString(connection.getInputStream());
		} catch (SocketTimeoutException e) {
			throw e;
		} catch (IOException e) {
			throw createException(e, connection);
		} finally {
			disconnect(connection);
		}
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
		String errorMessage = StreamUtils.readToString(connection.getErrorStream());
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

	/**
	 * Sets a trust manager that will always trust.
	 * <p>
	 * TODO: dont swallog exceptions and setup things so that they dont disturb
	 * other components.
	 */
	private void setPermissiveSSLSocketFactory(HttpsURLConnection connection) {
		try {
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(
					new KeyManager[0], new TrustManager[] { new PermissiveTrustManager() }, new SecureRandom());
			SSLSocketFactory socketFactory = sslContext.getSocketFactory();
			((HttpsURLConnection) connection).setSSLSocketFactory(socketFactory);
		} catch (KeyManagementException e) {
			// ignore
		} catch (NoSuchAlgorithmException e) {
			// ignore
		}
	}

	protected HttpURLConnection createConnection(String username, String password, String userAgent, URL url)
			throws IOException {
		return createConnection(username, password, null, null, userAgent, url, NO_TIMEOUT);
	}

	protected HttpURLConnection createConnection(String username, String password, String authKey, String authIV,
			String userAgent, URL url, int timeout) throws IOException {
		LOGGER.trace(
				"creating connection to {} using username \"{}\" and password \"{}\"", new Object[] { url, username,
						password });
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		setSSLChecks(url, connection);
		setAuthorisation(username, password, authKey, authIV, connection);
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setAllowUserInteraction(false);
		setConnectTimeout(connection);
		setReadTimeout(timeout, connection);
		// wont work when switching http->https
		// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4620571
		connection.setInstanceFollowRedirects(true);
		setAcceptHeader(connection);
		setUserAgent(connection);

		connection.setRequestProperty(PROPERTY_CONTENT_TYPE, requestMediaType.getType());

		return connection;
	}

	private void setUserAgent(HttpURLConnection connection) {
		String userAgent = this.userAgent;
		if (!StringUtils.isEmpty(authKey)) {
			userAgent = USERAGENT_FOR_KEYAUTH;
		}

		if (userAgent != null) {
			connection.setRequestProperty(PROPERTY_USER_AGENT, userAgent);
		}
	}

	private void setAcceptHeader(HttpURLConnection connection) {
		StringBuilder builder =
				new StringBuilder(acceptedMediaType);
		if (acceptVersion != null) {
			builder.append(SEMICOLON).append(SPACE)
					.append(VERSION).append(EQUALS).append(acceptVersion);
		}

		connection.setRequestProperty(PROPERTY_ACCEPT, builder.toString());
	}

	private void setAuthorisation(String username, String password, String authKey, String authIV,
			HttpURLConnection connection) {
		if (username == null || username.trim().length() == 0
				|| password == null || password.trim().length() == 0) {
			if (authKey != null && authIV != null) {
				connection.setRequestProperty(PROPERTY_AUTHKEY, authKey);
				connection.setRequestProperty(PROPERTY_AUTHIV, authIV);
			}
		} else {
			String credentials = Base64Coder.encode(
					new StringBuilder().append(username).append(COLON).append(password).toString().getBytes());
			connection.setRequestProperty(PROPERTY_AUTHORIZATION,
					new StringBuilder().append(AUTHORIZATION_BASIC).append(SPACE).append(credentials).toString());
		}
	}

	private void setSSLChecks(URL url, HttpURLConnection connection) {
		if (isHttps(url)
				&& !sslChecks) {
			HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
			httpsConnection.setHostnameVerifier(new NoopHostnameVerifier());
			setPermissiveSSLSocketFactory(httpsConnection);
		}
	}

	private void setConnectTimeout(URLConnection connection) {
		int timeout = getTimeout(
				getSystemPropertyInteger(SYSPROP_OPENSHIFT_CONNECT_TIMEOUT),
				getSystemPropertyInteger(SYSPROP_DEFAULT_CONNECT_TIMEOUT),
				DEFAULT_CONNECT_TIMEOUT);
		connection.setConnectTimeout(timeout);
	}

	private void setReadTimeout(int timeout, URLConnection connection) {
		timeout = getTimeout(timeout, getSystemPropertyInteger(SYSPROP_DEFAULT_READ_TIMEOUT), DEFAULT_READ_TIMEOUT);
		connection.setReadTimeout(timeout);
	}

	private int getTimeout(int timeout, int systemPropertyTimeout, int defaultTimeout) {
		if (timeout == NO_TIMEOUT) {
			timeout = systemPropertyTimeout;
			if (timeout == NO_TIMEOUT) {
				timeout = defaultTimeout;
			}
		}
		return timeout;
	}

	protected IMediaType getMediaType() {
		return requestMediaType;
	}
	
	private int getSystemPropertyInteger(String key) {
		try {
			return Integer.parseInt(System.getProperty(key));
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	private class PermissiveTrustManager implements X509TrustManager {

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkServerTrusted(X509Certificate[] chain,
				String authType) throws CertificateException {
		}

		public void checkClientTrusted(X509Certificate[] chain,
				String authType) throws CertificateException {
		}
	}

	private class NoopHostnameVerifier implements HostnameVerifier {

		public boolean verify(String hostname, SSLSession sslSession) {
			return true;
		}
	}
}
