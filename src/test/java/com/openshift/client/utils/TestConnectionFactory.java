/******************************************************************************* 
 * Copyright (c) 2012-2014 Red Hat, Inc. 
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

import com.openshift.client.IHttpClient;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.NoopSSLCertificateCallback;
import com.openshift.client.OpenShiftConnectionFactory;
import com.openshift.client.OpenShiftException;

/**
 * User Builder, used to establish a connection and retrieve a user.
 * 
 * @author Andre Dietisheim
 * @author Sean Kavanagh
 * 
 */
public class TestConnectionFactory extends OpenShiftConnectionFactory {

	public IOpenShiftConnection getConnection() throws FileNotFoundException, IOException, OpenShiftException {

		OpenShiftTestConfiguration configuration = new OpenShiftTestConfiguration();
		
		return getConnection(
				configuration.getClientId()
				, configuration.getRhlogin()
				, configuration.getPassword()
				, null
				, null
				, null
				, configuration.getLibraServer()
				, new NoopSSLCertificateCallback());
	}

	public IOpenShiftConnection getConnection(String password, String server, IHttpClient httpClient) throws FileNotFoundException, IOException, OpenShiftException {
		OpenShiftTestConfiguration configuration = new OpenShiftTestConfiguration();
		return getConnection(
				configuration.getClientId(),
				configuration.getRhlogin(),
				password,
				null,
				server,
				httpClient);
	}

	public IOpenShiftConnection getConnection(IHttpClient httpClient) throws FileNotFoundException, IOException, OpenShiftException {
		OpenShiftTestConfiguration configuration = new OpenShiftTestConfiguration();
		return getConnection(configuration.getPassword(), configuration.getLibraServer(), httpClient);
	}


    public IOpenShiftConnection getAuthTokenConnection(String token) throws FileNotFoundException, IOException, OpenShiftException {

        OpenShiftTestConfiguration configuration = new OpenShiftTestConfiguration();
        return  getConnection(
                configuration.getClientId(),
                token,
                configuration.getLibraServer(),
                new NoopSSLCertificateCallback());
             
    }

}
