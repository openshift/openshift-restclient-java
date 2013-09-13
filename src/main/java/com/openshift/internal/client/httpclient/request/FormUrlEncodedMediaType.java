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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.client.IHttpClient;
import com.openshift.internal.client.httpclient.EncodingException;
import com.openshift.internal.client.utils.UrlUtils;

/**
 * A class that encodes request parameters to formurl-encoded format so that they may get sent to
 * the server. There are 4 types that are recognized and correctly encoded:
 * <ul>
 * <li>Parameter (key-/value-pair, keys are always strings, values may be
 * StringValue, ParameterValueArray, ParameterValueMap))</li>
 * <li>StringValue (string value: paramname="value")/li>
 * <li>ParameterValueArray (array of values:  paramname[]="value"&paramname[]="value")</li>
 * <li>ParameterValueMap (map of values: paramname[key]="value"&paramname[key]="value)"</li>
 * </ul>
 * 
 * @author Andre Dietisheim
 * 
 * @see IHttpClient#post(java.net.URL, IMediaType, int,
 *      com.openshift.internal.client.httpclient.request.Parameter...)
 * @see IHttpClient#put(java.net.URL, IMediaType, int,
 *      com.openshift.internal.client.httpclient.request.Parameter...)
 * @see IHttpClient#delete(java.net.URL, IMediaType, int,
 *      com.openshift.internal.client.httpclient.request.Parameter...)
 */
public class FormUrlEncodedMediaType implements IMediaType {

	private static final Logger LOGGER = LoggerFactory.getLogger(FormUrlEncodedMediaType.class);

	private static final String UTF8 = "UTF-8";
	private boolean firstParameter = true;

	@Override
	public String getType() {
		return IHttpClient.MEDIATYPE_APPLICATION_FORMURLENCODED;
	}

	public void writeTo(ParameterValueMap parameterMap, OutputStream out) throws EncodingException {
		// reset for eventual 2nd write
		try {
			this.firstParameter = true;
			for (Parameter parameter : parameterMap.getValue().values()) {
				writeTo(parameter.getName(), parameter.getValue(), out);
			}
		} catch (IOException e) {
			throw new EncodingException(
					MessageFormat.format("Could not encode parameters {0}", parameterMap.toString()), e);
		}
	}

	private void writeTo(String name, ParameterValueMap parameterMap, OutputStream out) throws IOException {
		for (Parameter parameter : parameterMap.getValue().values()) {
			String childName = new StringBuilder()
					.append(name)
					.append('[').append(parameter.getName()).append(']')
					.toString();
			writeTo(childName, parameter.getValue(), out);
		}
	}

	private void writeTo(String name, ParameterValue<?> value, OutputStream out) throws IOException {
		if (StringValue.class.isAssignableFrom(value.getClass())) {
			writeTo(name, (StringValue) value, out);
		} else if (ParameterValueArray.class.isAssignableFrom(value.getClass())) {
			writeTo(name, (ParameterValueArray) value, out);
		} else if (ParameterValueMap.class.isAssignableFrom(value.getClass())) {
			writeTo(name, (ParameterValueMap) value, out);
		}
	}

	private void writeTo(String name, ParameterValueArray array, OutputStream out) throws IOException {
		String childName = name + "[]";
		for (ParameterValue<?> value : array.getValue()) {
			writeTo(childName, value, out);
		}
	}

	private void writeTo(String name, StringValue stringValue, OutputStream out) throws IOException {
		StringBuilder builder = new StringBuilder();
		if (!firstParameter) {
			builder.append(IHttpClient.AMPERSAND);
		} else {
			firstParameter = false;
		}
		String value = encode(stringValue.getValue());
		String parameterString = builder
				.append(name)
				.append(IHttpClient.EQUALS).append(value)
				.toString();
		out.write(parameterString.getBytes());
		LOGGER.trace(out.toString());
	}

	private String encode(String value) throws UnsupportedEncodingException {
		if (UrlUtils.isUrl(value)) {
			// dont encode url payload
			return value;
		}
		return URLEncoder.encode(value, UTF8);
	}
}
