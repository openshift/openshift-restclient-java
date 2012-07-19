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

import java.io.FileNotFoundException;
import java.io.IOException;

import com.openshift.client.OpenShiftException;

/**
 * @author André Dietisheim
 */
public class OpenShiftConfiguration extends AbstractOpenshiftConfiguration {

	public OpenShiftConfiguration() throws FileNotFoundException, IOException, OpenShiftException {
		super(new SystemProperties(
				new UserConfiguration(
						new SystemConfiguration(
								new DefaultConfiguration()))));
	}
}
