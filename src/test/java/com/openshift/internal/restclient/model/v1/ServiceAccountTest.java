/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.v1;

import com.openshift.internal.restclient.model.ServiceAccount;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.serviceaccount.IServiceAccount;
import com.openshift.restclient.utils.Samples;
import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * @author David Simansky | dsimansk@redhat.com
 */
public class ServiceAccountTest {

	private static final String VERSION = "v1";
	private static final Samples sample = Samples.V1_SERVICE_ACCOUNT;
	private IServiceAccount serviceAccount;
	
	@Before
	public void setUp() {
		IClient client = mock(IClient.class);
		ModelNode node = ModelNode.fromJSONString(sample.getContentAsString());
		serviceAccount = new ServiceAccount(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION,
				ResourceKind.SERVICE_ACCOUNT));
	}
	
	@Test
	public void testGetSecrets() {
		String[] expSecrets = new String []{"deployer-token-luifi", "deployer-dockercfg-jq62e"};
		assertArrayEquals(expSecrets, serviceAccount.getSecrets().toArray());
	}

	@Test
	public void testGetImagePullSecrets() {
		String[] expSecrets = new String []{"deployer-dockercfg-jq62e"};
		assertArrayEquals(expSecrets, serviceAccount.getImagePullSecrets().toArray());
	}

	@Test
	public void testAddSecret() {
		serviceAccount.addSecret("my-test-secret");
		assertEquals(3, serviceAccount.getSecrets().size());
		assertTrue(serviceAccount.getSecrets().contains("my-test-secret"));
	}

	@Test
	public void testAddNullSecret() {
		serviceAccount.addSecret(null);
		assertEquals(3, serviceAccount.getSecrets().size());
		assertTrue(serviceAccount.getSecrets().contains(""));
	}

	@Test
	public void testAddImagePullSecret() {
		serviceAccount.addImagePullSecret("my-test-secret");
		assertEquals(2, serviceAccount.getImagePullSecrets().size());
		assertTrue(serviceAccount.getImagePullSecrets().contains("my-test-secret"));
	}

	@Test
	public void testAddNullImagePullSecret() {
		serviceAccount.addImagePullSecret(null);
		assertEquals(2, serviceAccount.getImagePullSecrets().size());
		assertTrue(serviceAccount.getImagePullSecrets().contains(""));
	}

}
