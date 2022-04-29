/*******************************************************************************
 * Copyright (c) 2015-2019 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.internal.restclient;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.openshift.restclient.IApiTypeMapper;
import com.openshift.restclient.IApiTypeMapper.IVersionedType;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IService;

public class ResourceFactoryTest {

    private ResourceFactory factory;

    @Before
    public void setup() {
        IClient client = mock(IClient.class);
        IApiTypeMapper mapper = mock(IApiTypeMapper.class);
        when(client.getOpenShiftAPIVersion()).thenReturn(OpenShiftAPIVersion.v1.toString());
        when(client.adapt(IApiTypeMapper.class)).thenReturn(mapper);
        when(mapper.getType(anyString(), eq(ResourceKind.SERVICE))).thenReturn(new IVersionedType() {
            
            @Override
            public String getVersion() {
                return "v1";
            }
            
            @Override
            public String getPrefix() {
                return null;
            }
            
            @Override
            public String getKind() {
                return ResourceKind.SERVICE;
            }
            
            @Override
            public String getApiGroupName() {
                return null;
            }
        });
        factory = new ResourceFactory(client);
    }

    @Test
    public void testStubWithNamespace() {
        IService service = factory.stub(ResourceKind.SERVICE, "foo", "bar");
        assertEquals("foo", service.getName());
        assertEquals("bar", service.getNamespaceName());
    }

    @Test
    public void testCreateWithKindAndName() {
        IService service = factory.create("v1", ResourceKind.SERVICE, "foo");
        assertEquals("foo", service.getName());
    }

}
