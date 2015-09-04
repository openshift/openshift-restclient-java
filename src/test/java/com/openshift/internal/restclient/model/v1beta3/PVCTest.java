/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.v1beta3;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.ResourceFactory;
import com.openshift.restclient.IClient;
import com.openshift.restclient.model.volume.IPersistentVolumeClaim;
import com.openshift.restclient.model.volume.PVCAccessModes;
import com.openshift.restclient.utils.Samples;

/**
 * @author Jeff Cantrill
 */
public class PVCTest {

	private static final String V1 = "v1beta3";
	private IPersistentVolumeClaim claim;
	
	@Before
	public void setup(){
		IClient client = mock(IClient.class);
		claim = new ResourceFactory(client).create(Samples.V1BETA3_PVC.getContentAsString());
		assertEquals(V1, claim.getApiVersion());
	}
	
	@Test
	public void testGetAccessModes(){
		assertArrayEquals(new String[] {PVCAccessModes.READ_WRITE_ONCE}, claim.getAccessModes().toArray());
	}
	
	@Test
	public void testGetStatus(){
		assertEquals("Pending", claim.getStatus());
	}
	
	@Test
	public void testGetRequestedStorage(){
		assertEquals("15m", claim.getRequestedStorage());
	}

}
