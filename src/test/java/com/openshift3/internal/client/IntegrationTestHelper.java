/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import com.openshift.client.NoopSSLCertificateCallback;
import com.openshift3.client.IClient;
import com.openshift3.client.authorization.AuthorizationClientFactory;
import com.openshift3.client.authorization.OAuthStrategy;

public class IntegrationTestHelper {

	private Properties prop;
	
	public IntegrationTestHelper(){
		loadProperties();
	}
	
	public IClient createClient(){
		final String server = prop.getProperty("serverURL");
		final String user = prop.getProperty("default.clusteradmin.user");
		final String password = prop.getProperty("default.clusteradmin.password");
		DefaultClient client;
		try {
			client = new DefaultClient(new URL(server), new NoopSSLCertificateCallback());
			client.setAuthorizationStrategy(new OAuthStrategy(server, new AuthorizationClientFactory().create(), user, password));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		return client;
	}
	
	public String getDefaultNamespace(){
		return prop.getProperty("default.project");
	}
	
	private void loadProperties(){
		InputStream is = null;
        try {
            this.prop = new Properties();
            is = this.getClass().getResourceAsStream("/openshiftv3IntegrationTest.properties");
            prop.load(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
