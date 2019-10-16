/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.capability.resources;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.openshift.restclient.model.IResource;

@RunWith(MockitoJUnitRunner.class)
public class AnnotationCapabilityTest {

    private AnnotationCapability capability;
    @Mock
    private IResource resource;

    @Before
    public void setUp() {
        capability = newCapability();
    }

    private AnnotationCapability newCapability() {
        return new AnnotationCapability("MyCapability", resource) {
            @Override
            protected String getAnnotationKey() {
                return "foobar";
            }
        };

    }

    @Test
    public void supportedWhenAnnotationsHasKey() {
        when(resource.isAnnotatedWith(eq("foobar"))).thenReturn(true);
        assertTrue("Exp. the capability to be supported when the annotation key exists", capability.isSupported());
    }

    @Test
    public void unsupportedWhenAnnotationsDoNotHaveADeploymentKey() {
        assertFalse("Exp. the capability to not be supported when annotation key does not exists",
                capability.isSupported());
    }
}
