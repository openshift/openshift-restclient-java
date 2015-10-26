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

import org.jboss.dmr.ModelNode;
import org.junit.BeforeClass;
import org.junit.Test;

import com.openshift.internal.restclient.model.ObjectReference;
import com.openshift.restclient.model.IObjectReference;
import com.openshift.restclient.utils.Samples;

/**
 * @author Jeff Cantrill
 */
public class ObjectRefTest {

	private static IObjectReference objRef;
	
	@BeforeClass
	public static void setup(){
		ModelNode node = ModelNode.fromJSONString(Samples.V1_OBJECT_REF.getContentAsString());
		objRef = new ObjectReference(node);
	}
	
	@Test
	public void testGetKind(){
		assertEquals("ServiceAccount", objRef.getKind());
	}
	@Test
	public void testGetNamespace(){
		assertEquals("test", objRef.getNamespace());
	}
	@Test
	public void testGetName(){
		assertEquals("builder", objRef.getName());
	}
	@Test
	public void testGetUID(){
		assertEquals("ce20b132-7986-11e5-b1e5-080027bdffff", objRef.getUID());
	}
	@Test
	public void getResourceVersion(){
		assertEquals("33366", objRef.getResourceVersion());
	}
	@Test
	public void getApiVersion(){
		assertEquals("v1", objRef.getApiVersion());
	}

}
