/*******************************************************************************
 * Copyright (c) 2015-2019 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.OpenShiftAPIVersion;
import com.openshift.internal.restclient.ResourceFactory;
import com.openshift.restclient.IApiTypeMapper;
import com.openshift.restclient.IApiTypeMapper.IVersionedType;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.IService;
import com.openshift.restclient.model.IServicePort;

public class ServiceTest {

    private IService service;
    private IClient client;
    private IApiTypeMapper mapper;

    @Before
    public void setup() {
        client = mock(IClient.class);
        mapper = mock(IApiTypeMapper.class);
        when(client.getOpenShiftAPIVersion()).thenReturn(OpenShiftAPIVersion.v1.name());
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
        IResourceFactory factory = new ResourceFactory(client);
        service = factory.stub(ResourceKind.SERVICE, OpenShiftAPIVersion.v1.name());
    }

    @Test
    public void testSetPorts() {
        ServicePort port = new ServicePort(new ModelNode());
        port.setName("foo");
        port.setTargetPort(12345);
        port.setProtocol("tcp");
        List<IServicePort> ports = new ArrayList<>();
        ports.add(port);
        service.setPorts(ports);
        assertEquals(1, service.getPorts().size());
        assertEquals("foo", service.getPorts().get(0).getName());
    }

    @Test
    public void testGetPods() {
        // setup
        when(client.list(anyString(), anyString(), anyMap())).thenReturn(new ArrayList<IResource>());

        service.addLabel("bar", "foo");
        service.setSelector("foo", "bar");

        // exectute
        service.getPods();

        // confirm called with selector and not something else
        verify(client, times(1)).list(eq(ResourceKind.POD), anyString(), eq(service.getSelector()));
    }

}
