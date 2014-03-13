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
package com.openshift.internal.client;

import static org.fest.assertions.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.openshift.client.OpenShiftException;
import com.openshift.internal.client.httpclient.HttpClientException;

/**
 * @author Andre Dietisheim
 */
public class RestServicePropertiesTest extends TestTimer {

	private static final String VERSION = "0.0.1";
	private RestServiceProperties serviceProperties;

	@Before
	public void setUp() throws FileNotFoundException, IOException, OpenShiftException, HttpClientException {
		this.serviceProperties = new RestServiceProperties() {
			protected Properties getProperties() throws IOException {
				Properties properties = new Properties();
				properties.put(KEY_VERSION, VERSION);
				properties.put(KEY_USERAGENTPATTERN, "{0} {1}");
				return properties;
			}
		};
	}

	@Test
	public void shouldReturnClientIdAndVersion() {
		// pre-conditions
		String clientId = "properties-test";

		// operation
		String userAgent = serviceProperties.getUseragent(clientId);

		// verification
		assertThat(userAgent).isNotEmpty();
		String[] clientIdAndVersion = userAgent.split(" ");
		assertThat(clientIdAndVersion).isNotNull().hasSize(2).containsOnly(clientId, VERSION);
	}
	
	@Test
	public void shouldReturnUserAgentEvenIfClientIdIsNull() {
		// pre-conditions

		// operation
		String userAgent = serviceProperties.getUseragent(null);

		// verification
		assertThat(userAgent).isNotEmpty();
		// length of version + space, no client-id
		assertThat(userAgent).hasSize(VERSION.length() + 1); 
	}

}
