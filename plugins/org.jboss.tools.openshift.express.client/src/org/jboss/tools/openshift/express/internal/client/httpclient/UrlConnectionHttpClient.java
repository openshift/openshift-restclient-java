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
package org.jboss.tools.openshift.express.internal.client.httpclient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;

import org.jboss.tools.openshift.express.client.IHttpClient;
import org.jboss.tools.openshift.express.internal.client.utils.StreamUtils;

/**
 * @author André Dietisheim
 */
public class UrlConnectionHttpClient implements IHttpClient {

	private static final String PROPERTY_CONTENT_TYPE = "Content-Type";
	private static final int TIMEOUT = 10 * 1024;

	private URL url;
	private String userAgent;

	public UrlConnectionHttpClient(String userAgent, URL url) {
		this.userAgent = userAgent;
		this.url = url;
	}
	
	public String post(String data) throws HttpClientException {
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

	public String get() throws HttpClientException {
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
	
	private HttpClientException createException(IOException ioe, HttpURLConnection connection) {
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
			default:
				return new HttpClientException(errorMessage, ioe);
			}
		} catch (IOException e) {
			return new HttpClientException(e);
		}
	}

	private HttpURLConnection createConnection(String userAgent, URL url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setAllowUserInteraction(false);
		connection.setConnectTimeout(TIMEOUT);
		connection.setRequestProperty(PROPERTY_CONTENT_TYPE, "application/x-www-form-urlencoded");
		connection.setInstanceFollowRedirects(true);
		connection.setRequestProperty(USER_AGENT, userAgent);
		return connection;
	}
}
