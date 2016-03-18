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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.restclient.ClientBuilder;
import com.openshift.restclient.IClient;
import com.openshift.restclient.NoopSSLCertificateCallback;
import com.openshift.restclient.authorization.AuthorizationClientFactory;
import com.openshift.restclient.authorization.BasicAuthorizationStrategy;
import com.openshift.restclient.authorization.IAuthorizationClient;
import com.openshift.restclient.authorization.IAuthorizationContext;
import com.openshift.restclient.authorization.IAuthorizationDetails;
import com.openshift.restclient.authorization.TokenAuthorizationStrategy;
import com.openshift.restclient.model.IResource;

import static org.junit.Assert.fail;

/**
 * @author Jeff Cantrill
 */
public class IntegrationTestHelper {

	private static final String KEY_DEFAULT_PROJECT = "default.project";
	private static final String KEY_SERVER_URL = "serverURL";
	private static final String KEY_PASSWORD = "default.clusteradmin.password";
	private static final String KEY_USER = "default.clusteradmin.user";
	private static final String KEY_OPENSHIFT_LOCATION = "default.openshift.location";

	private static final String INTEGRATIONTEST_PROPERTIES = "/openshiftv3IntegrationTest.properties";

	private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestHelper.class);

	private final Properties prop;

	public IntegrationTestHelper() {
		this.prop = loadProperties(INTEGRATIONTEST_PROPERTIES);
	}

	public IClient createClient(){
		return new ClientBuilder(prop.getProperty(KEY_SERVER_URL)).build();
	}

	public IClient createClientForBasicAuth() {
		IClient client = createClient();
		final String user = getDefaultClusterAdminUser();
		final String password = getDefaultClusterAdminPassword();
		client.setAuthorizationStrategy(new BasicAuthorizationStrategy(user, password, ""));
		IAuthorizationClient authClient = new AuthorizationClientFactory().create(client);
		IAuthorizationContext context = authClient.getContext(client.getBaseURL().toString());
		client.setAuthorizationStrategy(new TokenAuthorizationStrategy(context.getToken()));
		return client;
	}

	public String getDefaultNamespace(){
		return prop.getProperty(KEY_DEFAULT_PROJECT);
	}

	public String generateNamespace() {
		return String.format("%s-%s",getDefaultNamespace(), new Random().nextInt(9999));
	}

	/**
	 * Loads the properties from the given {@code propertyFileName}, then
	 * overrides from the System properties if any was given (this is a convenient way to
	 * override the default settings and avoid conflicting with the properties file in git)
	 * 
	 * @return the properties to use in the test
	 * @throws IOException 
	 */
	private static Properties loadProperties(final String propertyFileName) {
		final Properties properties = new Properties();
		try {
			properties.load(IntegrationTestHelper.class.getResourceAsStream(propertyFileName));
		} catch (IOException e) {
			e.printStackTrace();
			fail("Failed to load properties from file " + INTEGRATIONTEST_PROPERTIES + ": " + e.getMessage());
		}
		overrideIfExists(properties, KEY_SERVER_URL);
		overrideIfExists(properties, KEY_DEFAULT_PROJECT);
		overrideIfExists(properties, KEY_OPENSHIFT_LOCATION);
		overrideIfExists(properties, KEY_USER);
		overrideIfExists(properties, KEY_PASSWORD);
		return properties;
	}
	
	
	private static void overrideIfExists(final Properties properties, final String propertyName){
		// then override with the VM arguments (if any)
		final String propertyValue = System.getProperty(propertyName);
		if (propertyValue != null) {
			properties.setProperty(propertyName, propertyValue);
		}
	}
	
	public static void cleanUpResource(IClient client, IResource resource) {
		try {
			Thread.sleep(1000);
			LOG.debug(String.format("Deleting resource: %s", resource));
			client.delete(resource);
		} catch (Exception e) {
			LOG.error("Exception deleting", e);
		}
	}

	public String getOpenShiftLocation() {
		return  prop.getProperty(KEY_OPENSHIFT_LOCATION);
	}

	public String getDefaultClusterAdminUser() {
		return  prop.getProperty(KEY_USER);
	}

	public String getDefaultClusterAdminPassword() {
		return  prop.getProperty(KEY_PASSWORD);
	}

	public String getServerUrl() {
		return  prop.getProperty(KEY_SERVER_URL);
	}
	
}
