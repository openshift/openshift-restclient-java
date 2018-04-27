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

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.model.ObjectReference;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IObjectReference;
import com.openshift.restclient.utils.Samples;

public class ObjectRefTest {

    private static final String CONTENT = Samples.V1_OBJECT_REF.getContentAsString();
    private IObjectReference objRef;
    private ModelNode node;

    @Before
    public void setup() {
        node = ModelNode.fromJSONString(CONTENT);
        objRef = new ObjectReference(node);
    }

    @Test
    public void testGetKind() {
        assertEquals("ServiceAccount", objRef.getKind());
    }

    @Test
    public void testSetKind() {
        objRef.setKind(ResourceKind.BUILD);
        assertEquals(ResourceKind.BUILD, new ObjectReference(node.clone()).getKind());
    }

    @Test
    public void testGetNamespace() {
        assertEquals("test", objRef.getNamespace());
    }

    @Test
    public void testSetNamespace() {
        objRef.setNamespace("newnamespace");
        assertEquals("newnamespace", new ObjectReference(node.clone()).getNamespace());
    }

    @Test
    public void testGetName() {
        assertEquals("builder", objRef.getName());
    }

    @Test
    public void testSetName() {
        objRef.setName("newname");
        assertEquals("newname", new ObjectReference(node.clone()).getName());
    }

    @Test
    public void testGetUID() {
        assertEquals("ce20b132-7986-11e5-b1e5-080027bdffff", objRef.getUID());
    }

    @Test
    public void getResourceVersion() {
        assertEquals("33366", objRef.getResourceVersion());
    }

    @Test
    public void getApiVersion() {
        assertEquals("v1", objRef.getApiVersion());
    }

}
