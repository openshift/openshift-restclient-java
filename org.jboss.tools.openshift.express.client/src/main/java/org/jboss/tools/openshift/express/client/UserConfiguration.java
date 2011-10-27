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
package org.jboss.tools.openshift.express.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import org.jboss.tools.openshift.express.internal.client.utils.StreamUtils;

/**
 * @author André Dietisheim
 */
public class UserConfiguration {

	private static final String CONFIGURATION_FOLDER = ".openshift";
	private static final String CONFIGURATION_FILE = "express.conf";
	private static final String PROPERTY_USERHOME = "user.home";
	protected static final String KEY_RHLOGIN = "default_rhlogin";
	
	private Properties properties;
	protected File file;
	
	public UserConfiguration() throws OpenShiftException, IOException {
		this.file = getUserConfigurationFile();
		this.properties = getUserProperties(file);
	}

	protected File getUserConfigurationFile() throws OpenShiftException, IOException {
		String userHome = System.getProperty(PROPERTY_USERHOME);
		if (userHome == null) {
			throw new OpenShiftException("Could not read user configuration: user home directory not found");
		}
		return new File(userHome + File.separatorChar + CONFIGURATION_FOLDER, CONFIGURATION_FILE);
	}

	protected Properties getUserProperties(File userConfigurationFile) throws FileNotFoundException, IOException {
		FileReader reader = null;
		try {
			Properties userConfigurationProperties = new Properties();
			reader = new FileReader(userConfigurationFile);
			userConfigurationProperties.load(reader);
			return userConfigurationProperties;
		} finally {
			StreamUtils.close(reader);
		}
	}

	public String getRhlogin() {
		return properties.getProperty(KEY_RHLOGIN);
	}
	
	public void setRhlogin(String rhlogin) {
		properties.put(KEY_RHLOGIN, rhlogin);
	}
	
	public void store() throws IOException {
		Writer writer = null;
		try {
			writer = new FileWriter(file);
			properties.store(writer, "");
		} finally {
			StreamUtils.close(writer);
		}
	}
}
