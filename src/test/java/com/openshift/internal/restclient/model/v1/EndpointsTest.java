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
package com.openshift.internal.restclient.model.v1;

import static org.fest.assertions.Assertions.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.openshift.internal.restclient.ResourceFactory;
import com.openshift.restclient.IClient;
import com.openshift.restclient.api.models.IEndpoints;
import com.openshift.restclient.api.models.IEndpoints.IEndpointAddress;
import com.openshift.restclient.api.models.IEndpoints.IEndpointPort;
import com.openshift.restclient.api.models.IEndpoints.IEndpointSubset;
import com.openshift.restclient.utils.Samples;

@RunWith(MockitoJUnitRunner.class)
public class EndpointsTest {

	private static String JSON = Samples.V1_ENDPOINTS.getContentAsString();
	
	@Mock
	private IClient client;
	private IEndpoints endpoint;

	@Before
	public void setUp() throws Exception {
	    ResourceFactory factory = new ResourceFactory(client);
		endpoint = factory.create(JSON);
	}

	@Test
	public void testDeserialization() {
	    List<IEndpointSubset> subSets = endpoint.getSubSets();
	    assertThat(subSets).isNotEmpty();
	    IEndpointSubset subset = subSets.get(0);
	    
	    List<IEndpointAddress> addresses = subset.getAddresses();
        assertThat(addresses).isNotEmpty();
        IEndpointAddress address = addresses.get(0);
        assertThat(address.getName()).isEmpty();
        assertThat(address.getHostName()).isEmpty();
        assertThat(address.getNodeName()).isEmpty();
        assertThat(address.getIP()).isEqualTo("192.168.121.62");
        assertThat(address.getTargetRef()).isNotNull();

        List<IEndpointAddress> notReady = subset.getNotReadyAddresses();
        assertThat(notReady).isNotEmpty();
        address = notReady.get(0);
        assertThat(address.getName()).isEqualTo("notready");
        assertThat(address.getHostName()).isEqualTo("foo.bar");
        assertThat(address.getNodeName()).isEqualTo("xyz.abc");
        assertThat(address.getIP()).isEqualTo("192.168.121.68");
        assertThat(address.getTargetRef()).isNull();

        List<IEndpointPort> ports = subset.getPorts();
        assertThat(ports).isNotEmpty();
        IEndpointPort port = ports.get(0);
        assertThat(port.getName()).isEqualTo("443-tcp");
        assertThat(port.getPort()).isEqualTo(443);
        assertThat(port.getProtocol()).isEqualTo("TCP");
	}

}
