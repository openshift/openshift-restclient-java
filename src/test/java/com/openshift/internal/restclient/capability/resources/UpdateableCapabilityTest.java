/*******************************************************************************
 * Copyright (c) 2015-2019 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.capability.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.internal.restclient.ResourceFactory;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.capability.resources.IUpdatable;
import com.openshift.restclient.model.IService;

@RunWith(MockitoJUnitRunner.class)
public class UpdateableCapabilityTest {

    @Mock
    private IClient client;
    private IService service;
    private IResourceFactory factory;

    @Before
    public void setup() {
        when(client.getOpenShiftAPIVersion()).thenReturn("v1");
        this.factory = new ResourceFactory(client);
        this.service = factory.stub(ResourceKind.SERVICE, "foo", IntegrationTestHelper.getDefaultNamespace());
        service.setAnnotation("foo", "bar");
    }

    @Test
    public void testUpdateCapability() {
        IService target = factory.stub(ResourceKind.SERVICE, "foo", IntegrationTestHelper.getDefaultNamespace());
        target.setAnnotation("foo", "xyz");

        service.accept(new CapabilityVisitor<IUpdatable, IService>() {

            @Override
            public IService visit(IUpdatable capability) {
                capability.updateFrom(target);
                return null;
            }
        }, null);
        assertNotSame("Exp. services to not be the same instance", target, service);
        assertEquals("Exp. the annotation to be updated", "xyz", service.getAnnotation("foo"));
        assertEquals("Exp. the JSON to be the same", target.toJson(), service.toJson());
    }

}
