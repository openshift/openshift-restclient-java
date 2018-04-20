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

package com.openshift.internal.restclient.apis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

import com.openshift.restclient.api.ITypeFactory;
import com.openshift.restclient.api.models.ITypeMeta;
import com.openshift.restclient.apis.autoscaling.models.IScale;
import com.openshift.restclient.model.IBuild;
import com.openshift.restclient.utils.Samples;

public class TypeMetaFactoryTest {

    private final ITypeFactory factory = new TypeMetaFactory();

    @Test
    public void testStubKind() {
        Object obj = factory.stubKind("extensions/v1beta1.Scale", Optional.of("foo"), Optional.of("bar"));
        assertTrue(obj instanceof IScale);
        IScale scale = (IScale) obj;
        assertEquals("foo", scale.getName());
        assertEquals("bar", scale.getNamespace());
        assertEquals("Scale", scale.getKind());
        assertEquals("extensions/v1beta1", scale.getApiVersion());
    }

    @Test
    public void testExtensionScale() {
        Object response = factory.createInstanceFrom(Samples.V1BETA1_API_EXT_SCALE.getContentAsString());
        assertTrue(response instanceof IScale);
    }

    @Test
    public void testUnrecognized() {
        Object response = factory.createInstanceFrom(Samples.V1_BUILD.getContentAsString());
        assertFalse(response instanceof IBuild);
        assertTrue(response instanceof ITypeMeta);
    }

}
