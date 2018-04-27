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

package com.openshift.internal.restclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.openshift.restclient.IApiTypeMapper.IVersionedApiResource;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IBuildConfig;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.IService;
import com.openshift.restclient.model.MocksFactory;

@RunWith(MockitoJUnitRunner.class)
public class ApiTypeMapperTest extends TypeMapperFixture {

    private MocksFactory factory = new MocksFactory();

    @Test
    public void testKubernetesResourceIsSupportedAfterInitiallyErrorIsThrown() throws Exception {
        IService resource = factory.stub(ResourceKind.SERVICE);
        try {
            getHttpClient().whenRequestTo(TypeMapperFixture.base + "/api").thenThrow(new RuntimeException());
            assertTrue("Exp. Kube support", mapper.isSupported(resource));
        } catch (RuntimeException e) {
            getHttpClient().whenRequestTo(TypeMapperFixture.base + "/api")
                    .thenReturn(responseOf(TypeMapperFixture.VERSIONS));
            assertTrue("Exp. Kube support", mapper.isSupported(resource));
        }
    }

    @Test
    public void testKubernetesResourceIsSupported() {
        IService resource = factory.stub(ResourceKind.SERVICE);
        assertTrue("Exp. Kube support", mapper.isSupported(resource));

        IVersionedApiResource endpoint = mapper.getEndpointFor("v1", ResourceKind.SERVICE);
        assertTrue("Exp. services to be namespaces", endpoint.isNamespaced());
    }

    @Test
    public void testOpenShiftResourceIsSupported() {
        IBuildConfig resource = factory.mock(IBuildConfig.class);
        // check subresource or action first
        when(resource.getKind()).thenReturn("buildconfigs/webhooks");
        assertFalse("Exp. ApiGroups to not recognize 'action' as a resource", mapper.isSupported(resource));

        resource = factory.stub(IBuildConfig.class);
        assertTrue("Exp. OpenShift support", mapper.isSupported(resource));
    }

    @Test
    public void testRandomResourceIsNotSupported() {
        IResource resource = factory.mock(IResource.class);
        assertFalse("Exp. no random supported kinds", mapper.isSupported(resource));
    }

    @Test
    public void testApiGroupResourceIsSupported() {
        IResource resource = mock(IResource.class);
        when(resource.getKind()).thenReturn("DaemonSet");
        when(resource.getApiVersion()).thenReturn("extensions/v1beta1");
        assertTrue("Exp. extension to be supported", mapper.isSupported(resource));

        IVersionedApiResource endpoint = mapper.getEndpointFor("extensions/v1beta1", "DaemonSet");
        assertEquals("daemonsets", endpoint.getName());
        assertTrue(endpoint.isNamespaced());
        assertTrue(endpoint.isSupported("status"));
    }

}
