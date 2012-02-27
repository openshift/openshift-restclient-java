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
package com.openshift.express.internal.client.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.openshift.express.client.OpenShiftException;

/**
 * A factory that creates the json string that the openshift rest service
 * would consume.
 * 
 * @author André Dietisheim
 */
public class OpenShiftEnvelopeFactory implements IOpenShiftRequestFactory {

	private static final char EQ = '=';
	private static final String PROPERTY_PASSWORD = "password";
	private static final String PROPERTY_JSON_DATA = "json_data";
	private static final String PROPERTY_AUTHKEY = "broker_auth_key";
	private static final String PROPERTY_AUTHIV = "broker_auth_iv";
	
	private static final String DATA_ENCODING = "UTF-8";
	private static final char AMP = '&';

	private String[] payloads;
	private String password;
	private String authKey;
	private String authIV;

	public OpenShiftEnvelopeFactory(String password, String authKey, String authIV, String... payloads) {
		this.password = password;
		this.authKey = authKey;
		this.authIV = authIV;
		this.payloads = payloads;
	}
	
	public String createString() throws OpenShiftException {
		try {
			StringBuilder builder = new StringBuilder();
			if (authKey != null && authIV != null) {
				appendProperty(PROPERTY_AUTHKEY, authKey, builder);
				builder.append(AMP);
				appendProperty(PROPERTY_AUTHIV, authIV, builder);
			} else if (password != null) {
				appendProperty(PROPERTY_PASSWORD, password, builder);
			} else {
				throw new OpenShiftException("Could not create request, no password nor authKey specified");
			}
			if (builder.length()>0) {
				builder.append(AMP);
			}
			appendPayload(builder);
			return builder.toString();
		} catch (UnsupportedEncodingException e) {
			throw new OpenShiftException(e, "Could not create request");
		}
	}

	private void appendProperty(String prop, String value, StringBuilder builder) throws UnsupportedEncodingException {
		builder.append(prop)
				.append(EQ)
				.append(URLEncoder.encode(value, DATA_ENCODING));
	}

	private void appendPayload(StringBuilder builder) throws UnsupportedEncodingException {
		StringBuilder payloadBuilder = new StringBuilder();
		for (int i = 0; i < payloads.length; i++) {
			if (i > 0
					&& i < payloads.length + 1) {
				payloadBuilder.append(AMP);
			}
			payloadBuilder.append(payloads[i]);
		}

		if (builder.length() > 0) {
			builder
					.append(PROPERTY_JSON_DATA)
					.append(EQ)
					.append(URLEncoder.encode(payloadBuilder.toString(), DATA_ENCODING));
		}

	}
}
