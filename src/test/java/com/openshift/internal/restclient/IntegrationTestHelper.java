/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

import org.jboss.dmr.ModelNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.model.ModelNodeBuilder;
import com.openshift.internal.restclient.model.Pod;
import com.openshift.internal.restclient.model.properties.ResourcePropertyKeys;
import com.openshift.restclient.ClientBuilder;
import com.openshift.restclient.IClient;
import com.openshift.restclient.NotFoundException;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IPod;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.IResource;

import static org.junit.Assert.fail;

/**
 * @author Jeff Cantrill
 */
public class IntegrationTestHelper {

	public static final long MILLISECONDS_PER_SECOND = 1000;
	public static final long MILLISECONDS_PER_MIN = MILLISECONDS_PER_SECOND * 60;
	
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
		IClient client = new ClientBuilder(prop.getProperty(KEY_SERVER_URL))
				.withUserName(getDefaultClusterAdminUser())
				.withPassword(getDefaultClusterAdminPassword())
				.build();
		return client;
	}

	public String getDefaultNamespace(){
		return prop.getProperty(KEY_DEFAULT_PROJECT);
	}

	public String generateNamespace() {
		return String.format("%s-%s",getDefaultNamespace(), new Random().nextInt(9999));
	}
	
	public IProject generateProject(IClient client) {
		IResource request = client.getResourceFactory().stub(ResourceKind.PROJECT_REQUEST, generateNamespace());
		return (IProject) client.create(request);
	}

	/**
	 * Stub a pod definition to the openshift/hello-openshift
	 * image for purposes of testing.
	 * @param client
	 * @param project
	 * @return a pod definition that needs to be further created using the client
	 */
	public static IPod stubPod(IClient client, IProject project) {
		//cluster shouldnt allow us to create pods directly
		ModelNode builder = new ModelNodeBuilder()
				.set(ResourcePropertyKeys.KIND, ResourceKind.POD)
				.set(ResourcePropertyKeys.METADATA_NAME, "hello-openshift")
				.set(ResourcePropertyKeys.METADATA_NAMESPACE, project.getName())
				.add("spec.containers", new ModelNodeBuilder()
					.set(ResourcePropertyKeys.NAME, "hello-openshift")
					.set("image", "openshift/hello-openshift")
					.add("ports", new ModelNodeBuilder()
						.set("containerPort", 8080)
						.set("protocol", "TCP")
					)
				)
				.build();
		return new Pod(builder, client, new HashMap<>());
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
	
	public static void cleanUpResource(IClient client, IResource resource) {
		if(client == null || resource == null) {
			LOG.debug("Skipping cleanup as client %s or resource %s is null", client, resource);
		}
		try {
			Thread.sleep(1000);
			LOG.debug(String.format("Deleting resource: %s", resource));
			client.delete(resource);
		} catch (Exception e) {
			LOG.error("Exception deleting", e);
		}
	}
	
	/**
	 * Wait for the resource to exist for cases where the test is faster
	 * then the server in reconciling its existence;
	 * @param client
	 * @param kind
	 * @param namespace
	 * @param name
	 * @param maxWaitMillis
	 * @return  The resource or null if the maxWaitMillis was exceeded or the resource doesnt exist
	 */
	public static IResource waitForResource(IClient client, String kind, String namespace, String name, long maxWaitMillis) {
		return waitForResource(client, kind, namespace, name, maxWaitMillis, new ReadyConditional() {
			@Override
			public boolean isReady(IResource resource) {
				return resource != null;
			}
			
		});
	}
	/**
	 * Wait for the resource to exist for cases where the test is faster
	 * then the server in reconciling its existence;
	 * 
	 * @param client
	 * @param kind
	 * @param namespace
	 * @param name
	 * @param maxWaitMillis
	 * @param conditional
	 * @return
	 */
	public static IResource waitForResource(IClient client, String kind, String namespace, String name, long maxWaitMillis, ReadyConditional conditional) {
		IResource resource = null;
		final long timeout = System.currentTimeMillis() + maxWaitMillis;
		do {
			try {
				resource = client.get(kind, name, namespace);
				if(resource != null && conditional != null) {
					if(conditional.isReady(resource)) {
						return resource;
					}
					resource = null;
				}
			}catch(NotFoundException e) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					throw new RuntimeException(e1);
				}
			}
		}while(resource == null && System.currentTimeMillis() <= timeout);
		return resource;
	}
	
	/**
	 * Interface that can evaluate a resource to determine if its ready
	 * @author jeff.cantrill
	 *
	 */
	public static interface ReadyConditional {
		
		/**
		 * 
		 * @param resource
		 * @return true if the resource is 'ready'
		 */
		boolean isReady(IResource resource);
	}

	
}
