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
package com.openshift.express.internal.client.test.fakes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.openshift.express.client.OpenShiftException;
import com.openshift.express.client.configuration.DefaultConfiguration;
import com.openshift.express.client.configuration.SystemConfiguration;

/**
 * @author André Dietisheim
 */
public class SystemConfigurationFake extends SystemConfiguration {

	public SystemConfigurationFake() throws OpenShiftException, IOException {
		this(null);
	}

	public SystemConfigurationFake(DefaultConfiguration defaultConfiguration) throws OpenShiftException, IOException {
		super(defaultConfiguration);
	}
	
	protected Properties getProperties(File file, Properties defaultProperties) throws FileNotFoundException, IOException {
		Properties properties = new Properties(defaultProperties);
		init(properties);
		return properties;
	}
	
	protected void init(Properties properties) {
	}
}
