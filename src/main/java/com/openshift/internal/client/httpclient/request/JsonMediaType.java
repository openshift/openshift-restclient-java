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

import java.io.OutputStream;
import java.io.PrintWriter;

import org.jboss.dmr.ModelNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.client.IHttpClient;
import com.openshift.internal.client.httpclient.EncodingException;

/**
 * A class that encodes request parameters to json so that they may get sent to
 * the server. There are 4 types that are recognized and correctly encoded:
 * <ul>
 * <li>Parameter (key-/value-pair, keys are always strings, values may be
 * StringValue, ParameterValueArray, ParameterValueMap))</li>
 * <li>StringValue (string value, in json: string)/li>
 * <li>ParameterValueArray (array of values, in json: array</li>
 * <li>ParameterValueMap (map of values, in json: object</li>
 * </ul>
 * 
 * @author Andre Dietisheim
 * 
 * @see IHttpClient#post(java.net.URL, IMediaType, int, com.openshift.internal.client.httpclient.request.Parameter...)
 * @see IHttpClient#put(java.net.URL, IMediaType, int, com.openshift.internal.client.httpclient.request.Parameter...)
 * @see IHttpClient#delete(java.net.URL, IMediaType, int, com.openshift.internal.client.httpclient.request.Parameter...)
 */
public class JsonMediaType implements IMediaType {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonMediaType.class);

	@Override
	public String getType() {
		return IHttpClient.MEDIATYPE_APPLICATION_JSON;
	}

	public void writeTo(ParameterValueMap parameterMap, OutputStream out) throws EncodingException {
		ModelNode root = new ModelNode();
		for (Parameter parameter : parameterMap.getValue().values()) {
			ModelNode property = root.get(parameter.getName());
			create(parameter.getValue(), property);
		}

		writeTo(out, root);
		log(root);
	}

	private void create(ParameterValueMap parameterMap, ModelNode node) {
		for (Parameter parameter : parameterMap.getValue().values()) {
			ModelNode property = node.get(parameter.getName());
			create(parameter.getValue(), property);
		}
	}

	private void create(ParameterValue<?> value, ModelNode node) {
		if (StringValue.class.isAssignableFrom(value.getClass())) {
			create((StringValue) value, node);
		} else if (ParameterValueArray.class.isAssignableFrom(value.getClass())) {
			create((ParameterValueArray) value, node);
		} else if (ParameterValueMap.class.isAssignableFrom(value.getClass())) {
			create((ParameterValueMap) value, node);
		}
	}

	private void create(ParameterValueArray array, ModelNode node) {
		for (ParameterValue<?> value : array.getValue()) {
			ModelNode member = new ModelNode();
			create(value, member);
			node.add(member);
		}
	}

	private void create(StringValue stringValue, ModelNode node) {
		node.set(stringValue.getValue());
	}

	private void writeTo(OutputStream out, ModelNode node) {
		PrintWriter writer = new PrintWriter(out);
		node.writeJSONString(writer, true);
		writer.flush();
	}

	private void log(ModelNode node) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(node.toJSONString(true));
		}
	}

}
