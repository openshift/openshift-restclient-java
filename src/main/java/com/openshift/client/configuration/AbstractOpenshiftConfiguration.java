/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.client.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.openshift.internal.client.utils.StreamUtils;

/**
 * @author André Dietisheim
 */
public abstract class AbstractOpenshiftConfiguration implements IOpenShiftConfiguration {

	public static final String SCHEME_HTTPS = "https://";

	protected static final String KEY_RHLOGIN = "default_rhlogin";
	protected static final String KEY_LIBRA_SERVER = "libra_server";
	protected static final String KEY_LIBRA_DOMAIN = "libra_domain";

	protected static final String KEY_PASSWORD = "rhpassword";
	protected static final String KEY_CLIENT_ID = "client_id";
	
	private static final Pattern SINGLEQUOTED_REGEX = Pattern.compile("'*([^']+)'*");
	private static final char SINGLEQUOTE = '\'';
	
	private static final String SYSPROPERTY_PROXY_PORT = "proxyPort";
	private static final String SYSPROPERTY_PROXY_HOST = "proxyHost";
	private static final String SYSPROPERTY_PROXY_SET = "proxySet";

	private Properties properties;
	private File file;
	private boolean doSSLChecks = false;

	public AbstractOpenshiftConfiguration() throws FileNotFoundException, IOException {
		this(null, null);
	}

	public AbstractOpenshiftConfiguration(IOpenShiftConfiguration parentConfiguration) throws FileNotFoundException,
			IOException {
		this(null, parentConfiguration);
	}

	public AbstractOpenshiftConfiguration(File file, IOpenShiftConfiguration parentConfiguration)
			throws FileNotFoundException, IOException {
		initProperties(file, parentConfiguration == null ? null : parentConfiguration.getProperties());
	}

	protected void initProperties(File file) throws FileNotFoundException, IOException {
		initProperties(file, null);
	}

	protected void initProperties(Properties defaultProperties) throws FileNotFoundException, IOException {
		initProperties(null, defaultProperties);
	}

	protected void initProperties(File file, Properties defaultProperties) throws FileNotFoundException, IOException {
		this.file = file;
		this.properties = getProperties(file, defaultProperties);
	}

	protected Properties getProperties(File file, Properties defaultProperties)
			throws FileNotFoundException, IOException {

		if (file == null
				|| !file.canRead()) {
			return new Properties(defaultProperties);
		}

		FileReader reader = null;
		try {
			Properties properties = new Properties(defaultProperties);
			reader = new FileReader(file);
			properties.load(reader);
			return properties;
		} finally {
			StreamUtils.close(reader);
		}
	}

	protected File getFile() {
		return file;
	}

	public Properties getProperties() {
		return properties;
	}

	public void save() throws IOException {
		if (file == null) {
			return;
		}
		Writer writer = null;
		try {
			writer = new FileWriter(file);
			properties.store(writer, "");
		} finally {
			StreamUtils.close(writer);
		}
	}

	public void setRhlogin(String rhlogin) {
		properties.put(KEY_RHLOGIN, rhlogin);
	}

	public String getRhlogin() {
		return properties.getProperty(KEY_RHLOGIN);
	}

	public void setLibraServer(String libraServer) {
		properties.put(KEY_LIBRA_SERVER, ensureIsSingleQuoted(libraServer));
	}

	public String getLibraServer() {
		return appendScheme(removeSingleQuotes(properties.getProperty(KEY_LIBRA_SERVER)));
	}

	public void setLibraDomain(String libraDomain) {
		properties.put(KEY_LIBRA_DOMAIN, ensureIsSingleQuoted(libraDomain));
	}

	public String getLibraDomain() {
		return removeSingleQuotes(properties.getProperty(KEY_LIBRA_DOMAIN));
	}

	protected String appendScheme(String host) {
		if (host == null) {
			return host;
		}
		return SCHEME_HTTPS + host;
	}

	protected String ensureIsSingleQuoted(String value) {
		return SINGLEQUOTE + removeSingleQuotes(value) + SINGLEQUOTE;
	}

	protected String removeSingleQuotes(String value) {
		if (value == null) {
			return null;
		}
		Matcher matcher = SINGLEQUOTED_REGEX.matcher(value);
		if (matcher.find()
				&& matcher.groupCount() == 1) {
			return matcher.group(1);
		} else {
			return value;
		}
	}
	
	public String getPassword() {
		return properties.getProperty(KEY_PASSWORD);
	}

	public String getClientId() {
		return properties.getProperty(KEY_CLIENT_ID);
	}
	
	public void setEnableSSLCertChecks(boolean doSSLChecks) {
		this.doSSLChecks = doSSLChecks;
	}

	public void setProxySet(boolean proxySet) {
		if (proxySet) {
			System.setProperty(SYSPROPERTY_PROXY_SET, "true");
		} else {
			System.setProperty(SYSPROPERTY_PROXY_SET, "false");
		}
	}

	public void setProxyHost(String proxyHost) {
		System.setProperty(SYSPROPERTY_PROXY_HOST, proxyHost);
	}

	public void setProxyPort(String proxyPort) {
		System.setProperty(SYSPROPERTY_PROXY_PORT, proxyPort);
	}

}
