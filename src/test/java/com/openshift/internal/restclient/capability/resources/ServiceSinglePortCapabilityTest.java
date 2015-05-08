/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.capability.resources;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.openshift.internal.restclient.model.Service;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.utils.Samples;

/**
 * @author jeff.cantrill
 */
@RunWith(MockitoJUnitRunner.class)
public class ServiceSinglePortCapabilityTest {
	
	private static final String V1BETA1 = "v1beta1";
	private ServiceSinglePortCapability capability;
	
	@Before
	public void setUp() throws Exception {
		capability = givenACapabilityForAService(V1BETA1);
	}
	
	private ServiceSinglePortCapability givenACapabilityForAService(String version) {
		Samples sample = version.equals(V1BETA1) ? Samples.V1BETA1_SERVICE : Samples.V1BETA3_SERVICE;
		IClient client = mock(IClient.class);
		ModelNode node = ModelNode.fromJSONString(sample.getContentAsString());
		Service service = new Service(node, client, ResourcePropertiesRegistry.getInstance().get(version, ResourceKind.Service));
		return new ServiceSinglePortCapability(service);
	}
	
	@Test
	public void testGetPort() {
		assertEquals(5001, capability.getPort());
	}
	
	@Test
	public void testSetPort() {
		capability.setPort(9999);
		assertEquals(9999, capability.getPort());
	}

	@Test
	public void testSetContainerPort() {
		capability.setContainerPort(12345);
		assertEquals(12345, capability.getContainerPort());
	}

	@Test
	public void testGetContainerPort() {
		assertEquals(5000, capability.getContainerPort());
	}
	
	@Test
	public void isSupportedShouldReturnTrueForV1Beta1() {
		assertTrue(capability.isSupported());
	}

	@Test
	public void isSupportedShouldReturnFalseForLaterThanV1Beta1() {
		ServiceSinglePortCapability capability = givenACapabilityForAService("v1beta3");
		assertFalse(capability.isSupported());
	}

}
