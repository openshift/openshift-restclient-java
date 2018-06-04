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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.model.Route;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.PredefinedResourceKind;
import com.openshift.restclient.model.route.IRoute;
import com.openshift.restclient.model.route.ITLSConfig;
import com.openshift.restclient.model.route.ITargetPort;
import com.openshift.restclient.model.route.TLSTerminationType;
import com.openshift.restclient.utils.Samples;

/**
 * Test to validate the lookup paths are correct for the version
 */
public class RouteTest {

    private static final String VERSION = "v1";
    private static final Samples sample = Samples.V1_ROUTE;
    private IRoute route;
    private IClient client;

    @Before
    public void setUp() {
        client = mock(IClient.class);
        ModelNode node = ModelNode.fromJSONString(sample.getContentAsString());
        route = new Route(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION, PredefinedResourceKind.ROUTE.getIdentifier()));
    }

    @Test
    public void getTLSConfigWhenUndefined() {
        ModelNode node = ModelNode.fromJSONString(Samples.V1_ROUTE_WO_TLS.getContentAsString());
        route = new Route(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION, PredefinedResourceKind.ROUTE.getIdentifier()));
        ITLSConfig tlsConfig = route.getTLSConfig();
        assertNull(tlsConfig);
    }

    @Test
    public void createTLSConfigWhenUndefined() {
        ModelNode node = ModelNode.fromJSONString(Samples.V1_ROUTE_WO_TLS.getContentAsString());
        route = new Route(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION, PredefinedResourceKind.ROUTE.getIdentifier()));
        ITLSConfig tls = route.createTLSConfig();
        assertNotNull(tls);
        assertEquals("", tls.getTerminationType());
        assertEquals("", tls.getCertificate());
        assertEquals("", tls.getCACertificate());
        assertEquals("", tls.getKey());
    }

    @Test
    public void getPortWhenUndefined() {
        ModelNode node = ModelNode.fromJSONString(Samples.V1_ROUTE_WO_TLS.getContentAsString());
        route = new Route(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION, PredefinedResourceKind.ROUTE.getIdentifier()));
        ITargetPort port = route.getPort();
        assertNull(port);
    }

    @Test
    public void createPortWhenUndefined() {
        ModelNode node = ModelNode.fromJSONString(Samples.V1_ROUTE_WO_TLS.getContentAsString());
        route = new Route(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION, PredefinedResourceKind.ROUTE.getIdentifier()));
        ITargetPort port = route.createPort();
        assertNotNull(port);
        assertEquals("", port.getTargetPortName());
        assertEquals(-1, port.getTargetPort().intValue());
    }

    @Test
    public void getNumericPortWhenDefined() {
        ModelNode node = ModelNode.fromJSONString(Samples.V1_ROUTE_PORT_NUMERIC.getContentAsString());
        route = new Route(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION, PredefinedResourceKind.ROUTE.getIdentifier()));
        ITargetPort port = route.getPort();
        assertNotNull(port);
        assertEquals(8080, port.getTargetPort().intValue());
    }

    @Test
    public void getNamePortWhenDefined() {
        ModelNode node = ModelNode.fromJSONString(Samples.V1_ROUTE_PORT_NAME.getContentAsString());
        route = new Route(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION, PredefinedResourceKind.ROUTE.getIdentifier()));
        ITargetPort port = route.getPort();
        assertNotNull(port);
        assertEquals("http-8080", port.getTargetPortName());
    }

    @Test
    public void testGetHost() {
        assertEquals("www.example.com", route.getHost());
    }

    @Test
    public void testGetPath() {
        assertEquals("/abc", route.getPath());
    }

    @Test
    public void testGetAndSetPath() {
        route.setPath("/def");
        assertEquals("/def", route.getPath());
    }

    @Test
    public void getServiceName() throws Exception {
        assertEquals("frontend", route.getServiceName());
    }

    @Test
    public void setServiceName() throws Exception {
        route.setServiceName("frontend-alt");
        assertEquals("frontend-alt", route.getServiceName());
    }

    @Test
    public void getTLSConfig() throws Exception {
        ITLSConfig tls = route.getTLSConfig();
        assertEquals(TLSTerminationType.EDGE, tls.getTerminationType());
        assertEquals("theCert", tls.getCertificate());
        assertEquals("theCACert", tls.getCACertificate());
        assertEquals("theKey", tls.getKey());
    }

    @Test
    public void createTLSConfigWhenAlreadyDefined() throws Exception {
        ITLSConfig tls = route.createTLSConfig();
        assertEquals(TLSTerminationType.EDGE, tls.getTerminationType());
        assertEquals("theCert", tls.getCertificate());
        assertEquals("theCACert", tls.getCACertificate());
        assertEquals("theKey", tls.getKey());
    }

}
