/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.DefaultClient;
import com.openshift.restclient.IClient;
import com.openshift.restclient.NoopSSLCertificateCallback;
import com.openshift.restclient.authorization.AuthorizationClientFactory;
import com.openshift.restclient.authorization.BasicAuthorizationStrategy;
import com.openshift.restclient.model.IResource;

/**
 * @author Jeff Cantrill
 */
public class IntegrationTestHelper {

	private Properties prop;
	private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestHelper.class);

	public IntegrationTestHelper(){
		loadProperties();
	}
	
	public String getDefaultClusterAdminUser() {
		return  prop.getProperty("default.clusteradmin.user");
		
	}

	public String getDefaultClusterAdminPassword() {
		return  prop.getProperty("default.clusteradmin.password");
	}
	
	public IClient createClient(){
		final String server = prop.getProperty("serverURL");
		DefaultClient client;
		try {
			client = new DefaultClient(new URL(server), new NoopSSLCertificateCallback());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		return client;
	}
	public IClient createClientForBasicAuth() {
		IClient client = createClient();
		final String user = getDefaultClusterAdminUser();
		final String password = getDefaultClusterAdminPassword();
		client.setAuthorizationStrategy(new BasicAuthorizationStrategy(user, password,"abcdef"));
		return client;
	}
	
	public String getDefaultNamespace(){
		return prop.getProperty("default.project");
	}
	
	public String generateNamespace() {
		return String.format("%s-%s",getDefaultNamespace(), new Random().nextInt(9999));
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
	
	public static void cleanUpResource(IClient client, IResource resource){
		try{
			Thread.sleep(1000);
			LOG.debug(String.format("Deleting resource: %s", resource));
			client.delete(resource);
		}catch(Exception e){
			LOG.error("Exception deleting", e);
		}
	}
}
