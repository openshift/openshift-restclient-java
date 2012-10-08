/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client.httpclient;

import java.io.FileNotFoundException;
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
import java.text.MessageFormat;
import java.util.Map;

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
import com.openshift.internal.client.utils.StreamUtils;

/**
 * @author Andre Dietisheim
 */
public class UrlConnectionHttpClient implements IHttpClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(UrlConnectionHttpClient.class);

	private static final int DEFAULT_CONNECT_TIMEOUT = 10 * 1024;
	private static final int DEFAULT_READ_TIMEOUT = 60 * 1024;
	private static final String SYSPROP_OPENSHIFT_CONNECT_TIMEOUT = "com.openshift.httpclient.timeout";
	private static final String SYSPROP_DEFAULT_CONNECT_TIMEOUT = "sun.net.client.defaultConnectTimeout";
	private static final String SYSPROP_DEFAULT_READ_TIMEOUT = "sun.net.client.defaultReadTimeout";
	private static final String SYSPROP_ENABLE_SNI_EXTENSION = "jsse.enableSNIExtension";

	private String userAgent;
	private boolean sslChecks;
	private String username;
	private String password;
	private String authKey;
	private String authIV;
	private IMediaType requestMediaType;
	private String acceptedMediaType;
	private String version;

	public UrlConnectionHttpClient(String username, String password, String userAgent, boolean sslChecks,
			IMediaType requestMediaType, String acceptedMediaType, String version) {
		this(username, password, userAgent, sslChecks, requestMediaType, acceptedMediaType, version, null, null);
	}
	
	public UrlConnectionHttpClient(String username, String password, String userAgent, boolean sslChecks,
			IMediaType requestMediaType, String acceptedMediaType, String version, String authKey, String authIV) {
		this.username = username;
		this.password = password;
		this.userAgent = userAgent;
		this.sslChecks = sslChecks;
		this.requestMediaType = requestMediaType;
		this.acceptedMediaType = acceptedMediaType;
		this.authKey = authKey;
		this.authIV = authIV;
	}


	public String get(URL url) throws HttpClientException, SocketTimeoutException {
		HttpURLConnection connection = null;
		try {
			connection = createConnection(username, password, authKey, authIV, userAgent, url);
			return StreamUtils.readToString(connection.getInputStream());
		} catch (FileNotFoundException e) {
			throw new NotFoundException(
					MessageFormat.format("Could not find resource \"{0}\"", url.toString()), e);
		} catch (IOException e) {
			throw createException(e, connection);
		} finally {
			disconnect(connection);
		}
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String put(Map<String, Object> parameters, URL url)
			throws SocketTimeoutException, UnsupportedEncodingException, HttpClientException {
		return put(requestMediaType.encodeParameters(parameters), url);
	}

	protected String put(String data, URL url) throws HttpClientException, SocketTimeoutException {
		return write(data, HttpMethod.PUT.toString(), url);
	}

	public String post(Map<String, Object> parameters, URL url)
			throws SocketTimeoutException, UnsupportedEncodingException, HttpClientException {
		return post(requestMediaType.encodeParameters(parameters), url);
	}

	protected String post(String data, URL url) throws HttpClientException, SocketTimeoutException {
		return write(data, HttpMethod.POST.toString(), url);
	}

	public String delete(Map<String, Object> parameters, URL url)
			throws HttpClientException, SocketTimeoutException, UnsupportedEncodingException {
		return delete(requestMediaType.encodeParameters(parameters), url);
	}

	public String delete(URL url)
			throws HttpClientException, SocketTimeoutException, UnsupportedEncodingException {
		return delete((String) null, url);
	}

	protected String delete(String data, URL url) throws HttpClientException, SocketTimeoutException {
		return write(data, HttpMethod.DELETE.toString(), url);
	}

	protected String write(String data, String requestMethod, URL url)
			throws SocketTimeoutException, HttpClientException {
		HttpURLConnection connection = null;
		try {
			connection = createConnection(username, password, authKey, authIV, userAgent, url);
			connection.setRequestMethod(requestMethod);
			connection.setDoOutput(true);
			if (data != null) {
				LOGGER.trace("Sending \"{}\" to {}", data, url);
				StreamUtils.writeTo(data.getBytes(), connection.getOutputStream());
			}
			return StreamUtils.readToString(connection.getInputStream());
		} catch (FileNotFoundException e) {
			throw new NotFoundException(
					MessageFormat.format("Could not find resource {0}", url.toString()), e);
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
			String errorMessage = StreamUtils.readToString(connection.getErrorStream());
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

	private void setAuthorisation(String username, String password, String authKey, String authIV, HttpURLConnection connection) {
		if (username == null
				|| password == null || username.trim().length() == 0 || password.trim().length() == 0) {
			if (authKey != null && authIV != null) {
		
				connection.setRequestProperty(PROPERTY_AUTHKEY,authKey);
				connection.setRequestProperty(PROPERTY_AUTHIV,authIV);
				
			} else {
				return;
			}
		}

		String credentials = Base64Coder.encodeString(
				new StringBuilder().append(username).append(COLON).append(password).toString());
		connection.setRequestProperty(PROPERTY_AUTHORIZATION,
				new StringBuilder().append(AUTHORIZATION_BASIC).append(SPACE).append(credentials).toString());
	}

	private void setSSLChecks(URL url, HttpURLConnection connection) {
		if (isHttps(url)
				&& !sslChecks) {
			// JDK7 bug workaround
			System.setProperty(SYSPROP_ENABLE_SNI_EXTENSION, "false");

			HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
			httpsConnection.setHostnameVerifier(new NoopHostnameVerifier());
			setPermissiveSSLSocketFactory(httpsConnection);
		}
	}

	private void setConnectTimeout(URLConnection connection) {
		int timeout = getSystemPropertyInteger(SYSPROP_OPENSHIFT_CONNECT_TIMEOUT);
		if (timeout > -1) {
			connection.setConnectTimeout(timeout);
			return;
		}
		timeout = getSystemPropertyInteger(SYSPROP_DEFAULT_CONNECT_TIMEOUT);
		if (timeout == -1) {
			connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
		}
	}

	private void setReadTimeout(URLConnection connection) {
		int timeout = getSystemPropertyInteger(SYSPROP_DEFAULT_READ_TIMEOUT);
		if (timeout == -1) {
			connection.setReadTimeout(DEFAULT_READ_TIMEOUT);
		}
	}

	private int getSystemPropertyInteger(String key) {
		try {
			return Integer.parseInt(System.getProperty(key));
		} catch (NumberFormatException e) {
			return -1;
		}
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

		return createConnection(username, password, null, null, userAgent, url);
	}
	
	protected HttpURLConnection createConnection(String username, String password, String authKey, String authIV, String userAgent, URL url)
			throws IOException {

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
		setReadTimeout(connection);
		// wont work when switching http->https
		// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4620571
		connection.setInstanceFollowRedirects(true);
		setAcceptHeader(connection);
		
		if (authKey != null && authKey.length() > 0)
			setUserAgent("OpenShift");
		
		setUserAgent(connection);
		
		connection.setRequestProperty(PROPERTY_CONTENT_TYPE, requestMediaType.getType());
		return connection;
	}

	private void setUserAgent(HttpURLConnection connection) {
		if (userAgent != null) {
			connection.setRequestProperty(PROPERTY_USER_AGENT, userAgent);
		}
	}

	private void setAcceptHeader(HttpURLConnection connection) {
		StringBuilder builder =
				new StringBuilder(acceptedMediaType);
		/*
		 * Not supported on PROD/STG yet
		 */
		if (version != null) {
			builder.append(SEMICOLON).append(SPACE)
					.append(VERSION).append(EQUALS).append(version);
		}

		connection.setRequestProperty(PROPERTY_ACCEPT, builder.toString());
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
