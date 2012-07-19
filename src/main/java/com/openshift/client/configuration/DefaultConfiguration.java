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
import java.io.IOException;
import java.util.Properties;

import com.openshift.client.OpenShiftException;

/**
 * @author André Dietisheim
 */
public class DefaultConfiguration extends AbstractOpenshiftConfiguration {

	public static final String LIBRA_SERVER = "openshift.redhat.com";
	public static final String LIBRA_DOMAIN = "rhcloud.com";
	
	public DefaultConfiguration() throws OpenShiftException, IOException {
		super();
	}

	@Override
	protected Properties getProperties(File file, Properties defaultProperties) {
		Properties properties = new Properties();
	    properties.put(KEY_LIBRA_SERVER, LIBRA_SERVER);
	    properties.put(KEY_LIBRA_DOMAIN, LIBRA_DOMAIN);
		return properties;
	}
}
