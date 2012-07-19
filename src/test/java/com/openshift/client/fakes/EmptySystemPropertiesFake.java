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
package com.openshift.client.fakes;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import com.openshift.client.OpenShiftException;
import com.openshift.client.configuration.IOpenShiftConfiguration;
import com.openshift.client.configuration.SystemProperties;

/**
 * @author Andre Dietisheim
 */
public class EmptySystemPropertiesFake extends SystemProperties {

	public EmptySystemPropertiesFake(IOpenShiftConfiguration parentConfiguration) 
			throws OpenShiftException,IOException {
		super(parentConfiguration);
	}

	@Override
	protected Properties getProperties(File file, Properties defaultProperties) {
		return new Properties(defaultProperties);
	}

}
