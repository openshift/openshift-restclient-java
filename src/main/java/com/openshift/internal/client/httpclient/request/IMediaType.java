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
package com.openshift.internal.client.httpclient.request;

import java.io.IOException;
import java.io.OutputStream;

import com.openshift.client.IHttpClient;
import com.openshift.internal.client.httpclient.EncodingException;

/**
 * 
 * A media type that encodes and writes request parameters before they get sent by the client.
 * 
 * @author Andre Dietisheim
 * 
 * @see IHttpClient#post(java.net.URL, IMediaType, int, com.openshift.internal.client.httpclient.request.Parameter...)
 * @see IHttpClient#put(java.net.URL, IMediaType, int, com.openshift.internal.client.httpclient.request.Parameter...)
 * @see IHttpClient#delete(java.net.URL, IMediaType, int, com.openshift.internal.client.httpclient.request.Parameter...)
 */
public interface IMediaType {

	public String getType();

	public void writeTo(ParameterValueMap parameterMap, OutputStream out) throws EncodingException;

}
