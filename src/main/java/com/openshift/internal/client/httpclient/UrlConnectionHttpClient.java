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
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;

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
import com.openshift.internal.client.httpclient.request.IMediaType;
import com.openshift.internal.client.httpclient.request.Parameter;
import com.openshift.internal.client.httpclient.request.ParameterValueMap;
import com.openshift.internal.client.utils.StreamUtils;
import com.openshift.internal.client.utils.StringUtils;

/**
 * @author Andre Dietisheim
 * @author Nicolas Spano
 */
public class UrlConnectionHttpClient implements IHttpClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(UrlConnectionHttpClient.class);

	protected String userAgent;
	protected boolean sslChecks;
	protected String username;
	protected String password;
	protected String authKey;
	protected String authIV;
	protected String acceptedMediaType;
	protected String acceptedVersion;

	public UrlConnectionHttpClient(String username, String password, String userAgent, boolean sslChecks,
			String acceptedMediaType, String version) {
		this(username, password, userAgent, sslChecks, acceptedMediaType, version, null, null);
	}

	public UrlConnectionHttpClient(String username, String password, String userAgent, boolean sslChecks,
			String acceptedMediaType, String version, String authKey, String authIV) {
		this.username = username;
		this.password = password;
		this.userAgent = userAgent;
		this.sslChecks = sslChecks;
		this.acceptedMediaType = acceptedMediaType;
		this.acceptedVersion = version;
		this.authKey = authKey;
		this.authIV = authIV;
	}

	@Override
	public String get(URL url, int timeout) throws HttpClientException, SocketTimeoutException {
		return request(HttpMethod.GET, url, null, timeout);
	}

	@Override
	public String head(URL url, int timeout) throws HttpClientException, SocketTimeoutException {
		return request(HttpMethod.HEAD, url, null, timeout);
	}

	@Override
	public String put(URL url, IMediaType mediaType, int timeout, Parameter... parameters)
			throws HttpClientException, SocketTimeoutException, EncodingException {
		return request(HttpMethod.PUT, url, mediaType, timeout, parameters);
	}

	@Override
	public String post(URL url, IMediaType mediaType, int timeout, Parameter... parameters)
			throws HttpClientException, SocketTimeoutException, EncodingException {
		return request(HttpMethod.POST, url, mediaType, timeout, parameters);
	}

	@Override
	public String patch(URL url, IMediaType mediaType, int timeout, Parameter... parameters)
			throws HttpClientException, SocketTimeoutException, EncodingException {
		return request(HttpMethod.PATCH, url, mediaType, timeout, parameters);
	}

	@Override
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
					url, username, password, authKey, authIV, userAgent, acceptedVersion, acceptedMediaType, timeout);
			// PATCH not yet supported by JVM
			if (httpMethod == HttpMethod.PATCH) {
				httpMethod = HttpMethod.POST;
				connection.setRequestProperty("X-Http-Method-Override", "PATCH");
			}
			connection.setRequestMethod(httpMethod.toString());
			if (!parameters.isEmpty()) {
				connection.setDoOutput(true);
				setRequestMediaType(requestMediaType, connection);
				requestMediaType.writeTo(parameters, connection.getOutputStream());
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

	protected HttpURLConnection createConnection(URL url, String username, String password, String authKey,
			String authIV, String userAgent, String acceptedVersion, String acceptedMediaType, int timeout)
			throws IOException {
		LOGGER.trace(
				"creating connection to {} using username \"{}\" and password \"{}\"",
				new Object[] { url, username, password });
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		setSSLChecks(url, connection);
		setAuthorisation(username, password, authKey, authIV, connection);
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setAllowUserInteraction(false);
		setConnectTimeout(NO_TIMEOUT, connection);
		setReadTimeout(timeout, connection);
		// wont work when switching http->https
		// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4620571
		connection.setInstanceFollowRedirects(true);
		setUserAgent(userAgent, authKey, connection);
		setAcceptHeader(acceptedVersion, acceptedMediaType, connection);

		return connection;
	}

	private void setUserAgent(String userAgent, String authKey, HttpURLConnection connection) {
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

	private void setConnectTimeout(int timeout, URLConnection connection) {
		connection.setConnectTimeout(
				getTimeout(
						timeout,
						getSystemPropertyInteger(SYSPROP_OPENSHIFT_CONNECT_TIMEOUT),
						getSystemPropertyInteger(SYSPROP_DEFAULT_CONNECT_TIMEOUT),
						DEFAULT_CONNECT_TIMEOUT));
	}

	private void setReadTimeout(int timeout, URLConnection connection) {
		connection.setReadTimeout(
				getTimeout(
						timeout,
						getSystemPropertyInteger(SYSPROP_OPENSHIFT_READ_TIMEOUT),
						getSystemPropertyInteger(SYSPROP_DEFAULT_READ_TIMEOUT),
						DEFAULT_READ_TIMEOUT));
	}

	private int getTimeout(int timeout, int openShiftTimeout, int systemPropertyTimeout, int defaultTimeout) {
		if (timeout == NO_TIMEOUT) {
			timeout = openShiftTimeout;
			if (timeout == NO_TIMEOUT) {
				timeout = systemPropertyTimeout;
				if (timeout == NO_TIMEOUT) {
					timeout = defaultTimeout;
				}
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
	
	private int getSystemPropertyInteger(String key) {
		try {
			return Integer.parseInt(System.getProperty(key));
		} catch (NumberFormatException e) {
			return NO_TIMEOUT;
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
}
