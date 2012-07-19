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

import com.openshift.client.OpenShiftException;

/**
 * The system wide OpenShift configuration that is at /etc/express.conf
 * 
 * @author André Dietisheim
 */
public class SystemConfiguration extends AbstractOpenshiftConfiguration {

	private static final String CONFIGURATION_FOLDER = File.separatorChar + "etc" + File.separatorChar + "openshift";
	private static final String CONFIGURATION_FILE = "express.conf";

	public SystemConfiguration(IOpenShiftConfiguration parentConfiguration) throws OpenShiftException, IOException {
		super(new File(CONFIGURATION_FOLDER, CONFIGURATION_FILE), parentConfiguration);
	}
}
