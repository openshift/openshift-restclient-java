/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.v1beta1;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.model.Route;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.route.IRoute;
import com.openshift.restclient.model.route.ITLSConfig;
import com.openshift.restclient.model.route.ITLSConfig.TLSTerminationType;
import com.openshift.restclient.utils.Samples;

/**
 * Test to validate the lookup paths are correct for the version
 * @author Jeff Cantrill
 */
public class V1Beta1RouteTest{

	private static final String VERSION = "v1beta1";
	private IRoute route;
	
	@Before
	public void setUp(){
		IClient client = mock(IClient.class);
		ModelNode node = ModelNode.fromJSONString(Samples.V1BETA1_ROUTE.getContentAsString());
		route = new Route(node, client, ResourcePropertiesRegistry.getInstance().get(VERSION, ResourceKind.Route));
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
	public void testSetPath() {
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
		assertEquals(TLSTerminationType.edge, tls.getTerminationType());
		assertEquals("theCert", tls.getCertificate());
		assertEquals("theCACert", tls.getCACertificate());
		assertEquals("theKey", tls.getKey());
	}

}
