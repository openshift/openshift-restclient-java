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

import java.net.SocketTimeoutException;
import java.net.URL;

import com.openshift.internal.client.httpclient.EncodingException;
import com.openshift.internal.client.httpclient.HttpClientException;
import com.openshift.internal.client.httpclient.request.IMediaType;
import com.openshift.internal.client.httpclient.request.Parameter;

/**
 * @author André Dietisheim
 * @author Nicolas Spano
 */
public interface IHttpClient {
	
	public static final String HTTP = "http";

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
	public static final char SLASH = '/';
	public static final char QUESTION_MARK = '?';
	
	public static final String VERSION = "version";

	public static final String SYSPROP_OPENSHIFT_CONNECT_TIMEOUT = "com.openshift.httpclient.connect.timeout";
	public static final String SYSPROP_OPENSHIFT_READ_TIMEOUT = "com.openshift.httpclient.read.timeout";
	public static final String SYSPROP_DEFAULT_CONNECT_TIMEOUT = "sun.net.client.defaultConnectTimeout";
	public static final String SYSPROP_DEFAULT_READ_TIMEOUT = "sun.net.client.defaultReadTimeout";

    public static final int DEFAULT_CONNECT_TIMEOUT = 10 * 	1000;
    public static final int DEFAULT_READ_TIMEOUT = 2 * 60 * 1000;
	public static final int NO_TIMEOUT = 0;

	public String get(URL url, int timeout) throws HttpClientException, SocketTimeoutException;

	public String head(URL url, int timeout) throws HttpClientException, SocketTimeoutException;

	public String post(URL url, IMediaType mediaType, int timeout, Parameter... parameters) throws HttpClientException, SocketTimeoutException, EncodingException;

    public String put(URL url, IMediaType mediaType, int timeout, Parameter... parameters) throws HttpClientException, SocketTimeoutException, EncodingException;

    public String patch(URL url, IMediaType mediaType, int timeout, Parameter... parameters) throws HttpClientException, SocketTimeoutException, EncodingException;

    public String delete(URL url, IMediaType mediaType, int timeout, Parameter... parameters) throws HttpClientException, SocketTimeoutException, EncodingException;

    public String delete(URL url, int timeout) throws HttpClientException, SocketTimeoutException, EncodingException;

	public void setUserAgent(String userAgent);

	public void setAcceptVersion(String version);
	
	public void setAcceptedMediaType(String acceptedMediaType);
	
}
