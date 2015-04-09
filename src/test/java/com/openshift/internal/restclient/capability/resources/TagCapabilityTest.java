/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.capability.resources;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.openshift.internal.restclient.capability.resources.TagCapability;
import com.openshift.restclient.model.IResource;

/**
 * @author Jeff Cantrill
 */
@RunWith(MockitoJUnitRunner.class)
public class TagCapabilityTest {

	private TagCapability capability;
	@Mock private IResource resource;
	
	@Before
	public void setup(){
		when(resource.getAnnotation("tags")).thenReturn("instant-app,ruby,mysql");
		capability = new TagCapability(resource);
	}
	
	@Test
	public void testGetTags() {
		assertArrayEquals(new String[]{"instant-app","ruby","mysql"},capability.getTags().toArray());
	}

}
