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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import com.openshift.client.IHttpClient;

/**
 * @author Andre Dietisheim
 */
public class FormUrlEncodedMediaType implements IMediaType {

	private static final String UTF8 = "UTF-8";

	public String getType() {
		return IHttpClient.MEDIATYPE_APPLICATION_FORMURLENCODED;
	}

	public String encodeParameters(Map<String, Object> parameters) throws UnsupportedEncodingException {
		return toUrlEncoded(parameters);
	}

	private String toUrlEncoded(Map<String, Object> parameters) throws UnsupportedEncodingException {
		if (parameters == null
				|| parameters.isEmpty()) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		for (Entry<String, Object> entry : parameters.entrySet()) {
			append(entry.getKey(), URLEncoder.encode(String.valueOf(entry.getValue()), UTF8), builder);
		}
		return builder.toString();
	}

	private void append(String name, Object value, StringBuilder builder) {
		if (builder.length() > 0) {
			builder.append(IHttpClient.AMPERSAND);
		}
		builder.append(name)
				.append(IHttpClient.EQUALS)
				.append(value.toString());
	}

}
