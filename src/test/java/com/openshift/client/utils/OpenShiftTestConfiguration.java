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
package com.openshift.client.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.openshift.client.OpenShiftException;
import com.openshift.client.configuration.AbstractOpenshiftConfiguration;
import com.openshift.client.configuration.DefaultConfiguration;
import com.openshift.client.configuration.IOpenShiftConfiguration;
import com.openshift.client.fakes.SystemConfigurationFake;
import com.openshift.client.fakes.SystemPropertiesFake;
import com.openshift.client.fakes.UserConfigurationFake;
import com.openshift.internal.client.utils.StreamUtils;
import com.openshift.internal.client.utils.StringUtils;

/**
 * @author André Dietisheim
 * @author Corey Daley
 */
public class OpenShiftTestConfiguration extends AbstractOpenshiftConfiguration {

	public static final String LIBRA_SERVER_STG = "https://stg.openshift.redhat.com";
	public static final String LIBRA_SERVER_PROD = "https://openshift.redhat.com";

	private static final String INTEGRATION_TEST_PROPERTIES = "/integrationTest.properties";
	private static final String DEVSERVER_PREFIX = "https://ec2-";

	public OpenShiftTestConfiguration() throws FileNotFoundException, IOException, OpenShiftException {
		super(new SystemPropertiesFake(
				new IntegrationTestConfiguration(
						new UserConfigurationFake(
								new SystemConfigurationFake(
										new DefaultConfiguration())))));
	}

	public String getStagingServer() {
		return LIBRA_SERVER_STG;
	}

	public String getProductionServer() {
		return LIBRA_SERVER_PROD;
	}

	public boolean isDevelopmentServer() {
		return !StringUtils.isEmpty(getLibraServer())
				&& getLibraServer().startsWith(DEVSERVER_PREFIX);
	}

	private static class IntegrationTestConfiguration extends AbstractOpenshiftConfiguration {

		public IntegrationTestConfiguration(IOpenShiftConfiguration parentConfiguration)
				throws FileNotFoundException, IOException {
			super(parentConfiguration);
		}

		protected Properties getProperties(File file, Properties defaultProperties)
				throws FileNotFoundException, IOException {
			InputStream in = null;
			try {
				Properties properties = new Properties(defaultProperties);
				properties.load(getClass().getResourceAsStream(INTEGRATION_TEST_PROPERTIES));
				return properties;
			} finally {
				StreamUtils.close(in);
			}
		}
	}
}
