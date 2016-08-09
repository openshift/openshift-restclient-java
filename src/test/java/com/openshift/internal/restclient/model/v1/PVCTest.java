/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.v1;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.ResourceFactory;
import com.openshift.restclient.IClient;
import com.openshift.restclient.model.volume.IPersistentVolumeClaim;
import com.openshift.restclient.model.volume.PVCAccessModes;
import com.openshift.restclient.utils.Samples;

import java.util.Collections;

/**
 * @author Jeff Cantrill
 */
public class PVCTest {

	private static final String V1 = "v1";
	private IPersistentVolumeClaim claim;
	
	@Before
	public void setup(){
		IClient client = mock(IClient.class);
		claim = new ResourceFactory(client).create(Samples.V1_PVC.getContentAsString());
		assertEquals(V1, claim.getApiVersion());
	}
	
	@Test
	public void testGetAccessModes(){
		assertArrayEquals(new String[] {PVCAccessModes.READ_WRITE_ONCE}, claim.getAccessModes().toArray());
	}

	@Test
	public void testSetAccessModes() {
		claim.setAccessModes(Collections.singleton(PVCAccessModes.READ_WRITE_MANY));
		assertArrayEquals(new String[] {PVCAccessModes.READ_WRITE_MANY}, claim.getAccessModes().toArray());
	}

	
	@Test
	public void testGetStatus(){
		assertEquals("Pending", claim.getStatus());
	}
	
	@Test
	public void testGetRequestedStorage(){
		assertEquals("15m", claim.getRequestedStorage());
	}

	@Test
	public void testSetRequestedStorage() {
		claim.setRequestedStorage("1Gi");
		assertEquals("1Gi", claim.getRequestedStorage());
	}
	
	@Test
	public void testGetVolumeName() {
		assertEquals("pv02", claim.getVolumeName());
	}

}
