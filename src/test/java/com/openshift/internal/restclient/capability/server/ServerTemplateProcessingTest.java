/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.capability.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.openshift.restclient.IApiTypeMapper;
import com.openshift.restclient.IClient;
import com.openshift.restclient.PredefinedResourceKind;
import com.openshift.restclient.capability.server.ITemplateProcessing;

public class ServerTemplateProcessingTest {

    private IApiTypeMapper mapper;
    private IClient client;
    private ITemplateProcessing cap;

    @Before
    public void setup() {
        mapper = mock(IApiTypeMapper.class);
        client = mock(IClient.class);
        cap = new ServerTemplateProcessing(client);

    }

    @Test
    public void testIsSupportedWhenApiEndpointExists() {
        when(mapper.isSupported(PredefinedResourceKind.PROCESSED_TEMPLATES.getIdentifier())).thenReturn(true);
        when(client.adapt(IApiTypeMapper.class)).thenReturn(mapper);

        assertTrue("Exp. endpoint to be supported when processedtemplates is supported", cap.isSupported());
    }

    @Test
    public void testIsSupportedWhenApiEndpointDoesNotExists() {
        when(mapper.isSupported(PredefinedResourceKind.PROCESSED_TEMPLATES.getIdentifier())).thenReturn(false);
        when(client.adapt(IApiTypeMapper.class)).thenReturn(mapper);

        assertFalse("Exp. endpoint to not be supported when processedtemplates does not exist", cap.isSupported());
    }

    @Test
    public void testIsNotSupportedWhenNotAdaptableToApiTypeMapper() {
        when(client.adapt(IApiTypeMapper.class)).thenReturn(null);

        assertFalse("Exp. endpoint to not be supported when not adaptable", cap.isSupported());
    }

}
