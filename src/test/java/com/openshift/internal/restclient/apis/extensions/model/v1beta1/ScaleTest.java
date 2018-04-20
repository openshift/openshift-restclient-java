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

package com.openshift.internal.restclient.apis.extensions.model.v1beta1;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.apis.autoscaling.models.Scale;
import com.openshift.restclient.apis.autoscaling.models.IScale;
import com.openshift.restclient.utils.Samples;

public class ScaleTest {

    private static final String JSON = Samples.V1BETA1_API_EXT_SCALE.getContentAsString();
    IScale scale;

    @Before
    public void setUp() throws Exception {
        ModelNode node = ModelNode.fromJSONString(JSON);
        scale = new Scale(node, Collections.emptyMap());
    }

    @Test
    public void testGetSetName() {
        assertEquals("logging-kibana", scale.getName());
        scale.setName("other");
        assertEquals("other", scale.getName());
    }

    @Test
    public void testGetSetNamespace() {
        assertEquals("logging", scale.getNamespace());
        scale.setNamespace("other-ns");
        assertEquals("other-ns", scale.getNamespace());
    }

    @Test
    public void testGetApiVersion() {
        assertEquals("extensions/v1beta1", scale.getApiVersion());
    }

    @Test
    public void testGetKind() {
        assertEquals("Scale", scale.getKind());
    }

    @Test
    public void testSetGetSpecReplicas() {
        assertEquals(2, scale.getSpecReplicas());
        scale.setSpecReplicas(30);
        assertEquals(30, scale.getSpecReplicas());
    }

}
