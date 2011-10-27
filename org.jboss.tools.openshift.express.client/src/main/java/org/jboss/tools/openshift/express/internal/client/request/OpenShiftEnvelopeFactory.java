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
package org.jboss.tools.openshift.express.internal.client.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jboss.tools.openshift.express.client.OpenShiftException;

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
	private static final String DATA_ENCODING = "UTF-8";
	private static final char AMP = '&';

	private String[] payloads;
	private String password;

	public OpenShiftEnvelopeFactory(String password, String... payloads) {
		this.password = password;
		this.payloads = payloads;
	}

	public String createString() throws OpenShiftException {
		try {
			StringBuilder builder = new StringBuilder();
			appendPassword(builder);
			builder.append(AMP);
			appendPayload(builder);
			return builder.toString();
		} catch (UnsupportedEncodingException e) {
			throw new OpenShiftException(e, "Could not create request");
		}
	}

	private void appendPassword(StringBuilder builder) throws UnsupportedEncodingException {
		builder.append(PROPERTY_PASSWORD)
				.append(EQ)
				.append(URLEncoder.encode(password, DATA_ENCODING));
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
