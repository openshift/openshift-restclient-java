/******************************************************************************* 
 * Copyright (c) 2011-2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.restclient.http;

import java.net.SocketTimeoutException;
import java.net.URL;

import com.openshift.internal.restclient.http.EncodingException;
import com.openshift.internal.restclient.http.HttpClientException;
import com.openshift.restclient.ISSLCertificateCallback;
import com.openshift.restclient.authorization.IAuthorizationStrategy;
import com.openshift.restclient.model.IResource;

/**
 * @author André Dietisheim
 * @author Nicolas Spano
 * @author Corey Daley
 * @author Sean Kavanagh
 * @deprecated This interface and its supporting classes to be deprecated in the near future.
 */
@Deprecated
public interface IHttpClient {
	
	public static final String SYSPROP_OPENSHIFT_CONNECT_TIMEOUT = "com.openshift.httpclient.connect.timeout";
	public static final String SYSPROP_OPENSHIFT_READ_TIMEOUT = "com.openshift.httpclient.read.timeout";
	public static final String SYSPROP_DEFAULT_CONNECT_TIMEOUT = "sun.net.client.defaultConnectTimeout";
	public static final String SYSPROP_DEFAULT_READ_TIMEOUT = "sun.net.client.defaultReadTimeout";

    public static final int DEFAULT_READ_TIMEOUT = 2 * 60 * 1000;
	public static final int NO_TIMEOUT = -1;

	public String get(URL url, int timeout) throws HttpClientException, SocketTimeoutException;

	public String head(URL url, int timeout) throws HttpClientException, SocketTimeoutException;

	public String post(URL url, int timeout, IResource resource) throws HttpClientException, SocketTimeoutException, EncodingException;

    public String put(URL url, int timeout, IResource resource) throws HttpClientException, SocketTimeoutException, EncodingException;

    public String delete(URL url, int timeout) throws HttpClientException, SocketTimeoutException, EncodingException;

	public void setUserAgent(String userAgent);

	public void setAcceptVersion(String version);
	
	public void setAcceptedMediaType(String acceptedMediaType);
	
	public void setAuthorizationStrategy(IAuthorizationStrategy strategy);

	public void setSSLCertificateCallback(ISSLCertificateCallback callback);
	
	public ISSLCertificateCallback getSSLCertificateCallback();
	
}
