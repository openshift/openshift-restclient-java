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

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import com.openshift.client.IHttpClient;
import com.openshift.internal.client.RequestParameter;
import com.openshift.internal.client.utils.StringUtils;

/**
 * @author Andre Dietisheim
 */
public class FormUrlEncodedMediaType implements IMediaType {

	private static final String UTF8 = "UTF-8";

	@Override
	public String getType() {
		return IHttpClient.MEDIATYPE_APPLICATION_FORMURLENCODED;
	}

	@Override
	public void write(RequestParameter[] parameters, OutputStream out) throws IOException {
		if (parameters == null
				|| parameters.length == 0) {
			return;
		}
		for (RequestParameter parameter : parameters) {
			parameter.writeTo(out, this);
			out.write(IHttpClient.AMPERSAND);
		}
	}

	@Override
	public void write(String name, String value, OutputStream out) throws IOException {
		writeSimpleParameterName(name, out);
		out.write(URLEncoder.encode(value, UTF8).getBytes());
	}

	private void writeSimpleParameterName(String name, OutputStream out) throws IOException {
		out.write(name.getBytes());
		out.write(IHttpClient.EQUALS);
	}

	@Override
	public void write(String name, List<String> values, OutputStream out) throws IOException {
		/**
		 * <name>[]=<value>&<name>[]=<value>
		 */
		boolean firstValueWritten = false;
		for (String value : values) {
			if (StringUtils.isEmpty(value)) {
				continue;
			}
			if (firstValueWritten) {
				out.write(IHttpClient.AMPERSAND);
			}
			out.write(
					MessageFormat.format("{0}[]={1}",
							name,
							URLEncoder.encode(value, UTF8).getBytes())
							.getBytes());
			firstValueWritten = true;
		}
	}

	@Override
	public void write(String name, Map<String, String> values, OutputStream out) throws IOException {
		/**
		 * <name>[][<key>]=<value>&<name>[<key>]=<value>
		 */
		boolean firstValueWritten = false;
		for (Map.Entry<String, String> entry: values.entrySet()) {
			if (StringUtils.isEmpty(entry.getValue())) {
				continue;
			}
			if (firstValueWritten) {
				out.write(IHttpClient.AMPERSAND);
			}
			out.write(MessageFormat.format("{0}[][{1}]={2}",
					name,
					entry.getKey(),
					URLEncoder.encode(entry.getValue(), UTF8).getBytes()).getBytes());
			firstValueWritten = true;
		}
	}
}
