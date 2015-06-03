/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.v1beta3;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.model.Secret;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.secret.ISecret;
import com.openshift.restclient.model.secret.SecretType;
import com.openshift.restclient.utils.Samples;

/**
 * Test to validate that Secret object works as expected
 * @author Jiri Pechanec
 */
public class SecretTest{

	private static final String VERSION = "v1beta3";
	private static final Samples sample = Samples.V1BETA3_SECRET;
	private ISecret secret;
	
	@Before
	public void setUp() {
		IClient client = mock(IClient.class);
		ModelNode node = ModelNode.fromJSONString(sample.getContentAsString());
		secret = new Secret(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION, ResourceKind.Secret));
	}
	
	@Test
	public void testSecretType() {
		assertEquals(SecretType.Opaque.toString(), secret.getType());
	}

	@Test
	public void testGetData() {
		assertEquals("value-1\r\n", new String(secret.getData("id-rsa.pub")));
		assertEquals("value-2\r\n\r\n", new String(secret.getData("id-rsa")));
	}

	@Test
	public void testAddAndGetData() {
		secret.addData("my-key1", "secret word".getBytes());
		secret.addData("my-key2", new ByteArrayInputStream("blah blah".getBytes()));
		assertEquals("secret word", new String(secret.getData("my-key1")));
		assertEquals("blah blah", new String(secret.getData("my-key2")));
	}


}
