/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.v1;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.ResourceFactory;
import com.openshift.restclient.IClient;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.utils.Samples;

/**
 * Test to validate the lookup paths are correct for the version
 * @author Jeff Cantrill
 */
public class UnrecognizedResourceTest{

	private IResource service;
	
	@Before
	public void setUp(){
		IClient client = mock(IClient.class);
		service = new ResourceFactory(client).create(Samples.V1_UNRECOGNIZED.getContentAsString());
	}
	
	@Test
	public void testGetName() {
		assertEquals("database", service.getName());
	}

	@Test
	public void testGetNamespace() {
		assertEquals("test", service.getNamespace());
	}

	@Test
	public void testGetKind() {
		assertEquals("IMadeThisOneUp", service.getKind());
	}

}
