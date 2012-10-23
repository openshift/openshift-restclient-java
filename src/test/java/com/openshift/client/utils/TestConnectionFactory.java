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
package com.openshift.client.utils;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.OpenShiftException;
import com.openshift.client.OpenShiftConnectionFactory;

/**
 * User Builder, used to establish a connection and retrieve a user.
 * 
 * @author Andre Dietisheim
 * 
 */
public class TestConnectionFactory extends OpenShiftConnectionFactory {

	public IOpenShiftConnection getConnection() throws FileNotFoundException, IOException, OpenShiftException {
		OpenShiftTestConfiguration configuration = new OpenShiftTestConfiguration();
		
		IOpenShiftConnection connection = getConnection(
				configuration.getClientId()
				, configuration.getRhlogin()
				, configuration.getPassword()
				, null
				, null
				, configuration.getLibraServer()
				, configuration.getProxySet()
				, configuration.getProxyHost()
				, configuration.getProxyPort());
		
		return connection;
	}
}
