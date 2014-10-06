/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client.utils;

import java.io.IOException;

import com.openshift.client.ConnectionBuilder;
import com.openshift.client.OpenShiftException;
import com.openshift.client.configuration.IOpenShiftConfiguration;

/**
 * @author Andre Dietisheim
 */
public class TestConnectionBuilder extends ConnectionBuilder {

	public TestConnectionBuilder(String serverUrl) throws OpenShiftException, IOException {
		super(serverUrl);
	}
	
	public TestConnectionBuilder() throws OpenShiftException, IOException {
		super(null);
	}

	public CredentialsConnectionBuilder defaultCredentials() throws IOException {
		return credentials(getTestConfiguration().getRhlogin(), getTestConfiguration().getPassword());
	}
	
	@Override
	protected IOpenShiftConfiguration createConfiguration() throws IOException {
		return new OpenShiftTestConfiguration();
	}

	protected OpenShiftTestConfiguration getTestConfiguration() throws IOException {
		return (OpenShiftTestConfiguration) getConfiguration();
	}
}
