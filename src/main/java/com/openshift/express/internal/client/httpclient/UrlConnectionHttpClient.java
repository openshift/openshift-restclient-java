/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.express.internal.client.httpclient;

import java.io.FileNotFoundException;
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

import com.openshift.express.client.IHttpClient;
import com.openshift.express.internal.client.utils.StreamUtils;

/**
 * @author André Dietisheim
 */
public class UrlConnectionHttpClient implements IHttpClient {

	private static final String PROPERTY_CONTENT_TYPE = "Content-Type";
	private static final int DEFAULT_CONNECT_TIMEOUT = 10 * 1024;
	private static final int DEFAULT_READ_TIMEOUT = 60 * 1024;
	private static final String SYSPROP_OPENSHIFT_CONNECT_TIMEOUT = "com.openshift.express.httpclient.timeout";
	private static final String SYSPROP_DEFAULT_CONNECT_TIMEOUT = "sun.net.client.defaultConnectTimeout";
	private static final String SYSPROP_DEFAULT_READ_TIMEOUT = "sun.net.client.defaultReadTimeout";
	
	private URL url;
	private String userAgent;
	private boolean doSSLChecks;

	public UrlConnectionHttpClient(String userAgent, URL url) {
		this(userAgent, url, false);
	}

	public UrlConnectionHttpClient(String userAgent, URL url, boolean verifyHostNames) {
		this.userAgent = userAgent;
		this.url = url;
		this.doSSLChecks = verifyHostNames;
	}

	public String post(String data) throws HttpClientException, SocketTimeoutException {
		HttpURLConnection connection = null;
		try {
			connection = createConnection(userAgent, url);
			connection.setDoOutput(true);
			StreamUtils.writeTo(data.getBytes(), connection.getOutputStream());
			return StreamUtils.readToString(connection.getInputStream());
		} catch (FileNotFoundException e) {
			throw new NotFoundException(
					MessageFormat.format("Could not find resource {0}", url.toString()), e);
		} catch (IOException e) {
			throw createException(e, connection);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public String get() throws HttpClientException, SocketTimeoutException {
		HttpURLConnection connection = null;
		try {
			connection = createConnection(userAgent, url);
			return StreamUtils.readToString(connection.getInputStream());
		} catch (FileNotFoundException e) {
			throw new NotFoundException(
					MessageFormat.format("Could not find resource {0}", url.toString()), e);
		} catch (IOException e) {
			throw createException(e, connection);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private HttpClientException createException(IOException ioe, HttpURLConnection connection)
			throws SocketTimeoutException {
		try {
			int responseCode = connection.getResponseCode();
			String errorMessage = StreamUtils.readToString(connection.getErrorStream());
			switch (responseCode) {
			case 500:
				return new InternalServerErrorException(errorMessage, ioe);
			case 400:
				return new BadRequestException(errorMessage, ioe);
			case 401:
				return new UnauthorizedException(errorMessage, ioe);
			case 404:
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

	private HttpURLConnection createConnection(String userAgent, URL url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		if (isHttps(url)
				&& !doSSLChecks) {
			HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
			httpsConnection.setHostnameVerifier(new NoopHostnameVerifier());
			setPermissiveSSLSocketFactory(httpsConnection);
		}
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setAllowUserInteraction(false);
		setConnectTimeout(connection);
		setReadTimeout(connection);
		connection.setRequestProperty(PROPERTY_CONTENT_TYPE, "application/x-www-form-urlencoded");
		connection.setInstanceFollowRedirects(true);
		connection.setRequestProperty(USER_AGENT, userAgent);
		return connection;
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
			sslContext.init(new KeyManager[0], new TrustManager[] { new PermissiveTrustManager() }, new SecureRandom());
			SSLSocketFactory socketFactory = sslContext.getSocketFactory();
			((HttpsURLConnection) connection).setSSLSocketFactory(socketFactory);
		} catch (KeyManagementException e) {
			// ignore
		} catch (NoSuchAlgorithmException e) {
			// ignore
		}
	}

	private static class NoopHostnameVerifier implements HostnameVerifier {

		public boolean verify(String hostname, SSLSession sslSession) {
			return true;
		}
	}

	private static class PermissiveTrustManager implements X509TrustManager {

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
}
