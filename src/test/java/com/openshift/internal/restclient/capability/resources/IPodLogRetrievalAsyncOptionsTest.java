/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.restclient.capability.resources;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.openshift.restclient.capability.resources.IPodLogRetrievalAsync.Options;

public class IPodLogRetrievalAsyncOptionsTest {

	private Options options = new Options();
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testFollowDoesNotOverrideParameter() {
		assertEquals("false", options
		.follow()
		.parameter("follow", "false")
		.getMap().get("follow"));
		
	}
	
	@Test
	public void testFollowIsAddedWhenTrue() {
		assertEquals("true", options.follow().getMap().get("follow"));
	}

	@Test
	public void testFollowIsNotAddedWhenFalse() {
		assertNull(options.follow(false).getMap().get("follow"));
	}
	
	@Test
	public void testContainerDoesNotOverrideParameter() {
		assertEquals("foo", options
		.container("bar")
		.parameter("container", "foo")
		.getMap().get("container"));
		
	}

	@Test
	public void testContainerAddedWhenNotEmpty() {
		assertEquals("bar", options
				.container("bar")
				.getMap().get("container"));
		
	}
	@Test
	public void testContainerNotAddedWhenEmpty() {
		assertNull(options
				.container(" ")
				.getMap().get("container"));
		
	}
}
