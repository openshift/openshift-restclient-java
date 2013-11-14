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
package com.openshift.internal.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

import com.openshift.internal.client.utils.StreamUtils;
import com.openshift.internal.client.utils.StringUtils;

/**
 * @author Andre Dietisheim
 */
public class RestServiceProperties {

	private static final String PROPERTIES_FILE = "restservice.properties";

	static final String KEY_USERAGENTPATTERN = "useragent";
	static final String KEY_VERSION = "version";
	static final String KEY_CLIENTID = "clientid";
	
	private String version;
	private String userAgent;
	private String clientId;

	private Properties properties;

	public String getVersion() {
		if (version == null) {
			version = getStringProperty(KEY_VERSION);
		}
		return version;
	}

	private String getStringProperty(String key) {
		try {
			return getProperties().getProperty(key);
		} catch (IOException e) {
			return null;
		}
	}

	public String getUseragent(String clientId) {
		String userAgent = null;
		String version = getVersion();
		String useragentPattern = getUseragentPattern();
		if (!StringUtils.isEmpty(useragentPattern)) {
			userAgent = MessageFormat.format(useragentPattern, 
					StringUtils.nullToEmptyString(version), 
					StringUtils.nullToEmptyString(clientId));
		}
		return userAgent;
	}

	protected String getUseragentPattern() {
		if (userAgent == null) {
			userAgent = getStringProperty(KEY_USERAGENTPATTERN);
		}

		return userAgent;
	}

	public String getClientId() {
		if (clientId == null) {
			clientId = getStringProperty(KEY_CLIENTID);
		}

		return clientId;
	}

	protected Properties getProperties() throws IOException {
		if (properties == null) {
			InputStream in = null;
			try {
				properties = new Properties();
				in = getClass().getResourceAsStream("/" + PROPERTIES_FILE);
				if (in == null) {
					throw new FileNotFoundException(
							MessageFormat.format("Could not load properties file {0}", PROPERTIES_FILE));
				}
				properties.load(in);
			} finally {
				StreamUtils.quietlyClose(in);
			}
		}
		return properties;
	}
}
