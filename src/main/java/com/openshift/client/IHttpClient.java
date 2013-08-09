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
package com.openshift.client;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URL;

import com.openshift.internal.client.RequestParameter;
import com.openshift.internal.client.httpclient.HttpClientException;

/**
 * @author André Dietisheim
 * @author Nicolas Spano
 */
public interface IHttpClient {

	public static final String PROPERTY_CONTENT_TYPE = "Content-Type";
	public static final String PROPERTY_AUTHORIZATION = "Authorization";
	public static final String PROPERTY_ACCEPT = "Accept";
	public static final String PROPERTY_USER_AGENT = "User-Agent";
	
	public static final String PROPERTY_AUTHKEY = "broker_auth_key";
	public static final String PROPERTY_AUTHIV = "broker_auth_iv";

	public static final String MEDIATYPE_APPLICATION_JSON = "application/json";
	public static final String MEDIATYPE_APPLICATION_XML = "application/xml";
	public static final String MEDIATYPE_APPLICATION_FORMURLENCODED = "application/x-www-form-urlencoded";

	public static final String AUTHORIZATION_BASIC = "Basic";

	public static final int STATUS_OK = 200;
	public static final int STATUS_INTERNAL_SERVER_ERROR = 500;
	public static final int STATUS_BAD_REQUEST = 400;
	public static final int STATUS_UNAUTHORIZED = 401;
	public static final int STATUS_NOT_FOUND = 404;

	public static final char SPACE = ' ';
	public static final char COLON = ':';
	public static final char COMMA = ',';
	public static final char SEMICOLON = ';';
	public static final char AMPERSAND = '&';
	public static final char EQUALS = '=';
	
	public static final String VERSION = "version";

    public static final int DEFAULT_CONNECT_TIMEOUT = 10 * 	1000;
    public static final int DEFAULT_READ_TIMEOUT = 2 * 60 * 1000;
	public static final int NO_TIMEOUT = -1;

	public void setUserAgent(String userAgent);
	
	public String getUserAgent();
	
	public void setAcceptVersion(String serviceVersion);

	public String getAcceptVersion();
	
	public String get(URL url) throws HttpClientException, SocketTimeoutException;

    public String get(URL url, int timeout) throws HttpClientException, SocketTimeoutException;

	public String post(URL url, RequestParameter... parameters) throws HttpClientException, SocketTimeoutException, UnsupportedEncodingException;

    public String post(URL url, int timeout, RequestParameter... parameters) throws HttpClientException, SocketTimeoutException, UnsupportedEncodingException;

	public String put(URL url, RequestParameter... parameters) throws HttpClientException, SocketTimeoutException, UnsupportedEncodingException;

    public String put(URL url, int timeout, RequestParameter... parameters) throws HttpClientException, SocketTimeoutException, UnsupportedEncodingException;

    public String delete(URL url) throws HttpClientException, SocketTimeoutException, UnsupportedEncodingException;

    public String delete(URL url, RequestParameter... parameters) throws HttpClientException, SocketTimeoutException, UnsupportedEncodingException;

    public String delete(URL url, int timeout, RequestParameter... parameters) throws HttpClientException, SocketTimeoutException, UnsupportedEncodingException;

	public void setAcceptedMediaType(String acceptedMediaType);
	
	public String getAcceptedMediaType();
}
