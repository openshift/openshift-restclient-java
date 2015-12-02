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


import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.internal.restclient.model.Pod;
import com.openshift.internal.restclient.model.Port;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.capability.IBinaryCapability;
import com.openshift.restclient.capability.resources.IPortForwardable;
import com.openshift.restclient.capability.resources.IPortForwardable.PortPair;

/**
 * 
 * @author Jeff Cantrill
 *
 */
public class OpenshiftBinaryPortForwardingIntegrationTest {

	private IntegrationTestHelper helper = new IntegrationTestHelper();
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testPortForwarding() {
		System.setProperty(IBinaryCapability.OPENSHIFT_BINARY_LOCATION, helper.getOpenShiftLocation());
		IClient client = helper.createClient();
		IResourceFactory resourceFactory = client.getResourceFactory();
		Pod pod = resourceFactory.create("v1", ResourceKind.POD);
		final Port port = new Port(new ModelNode());
		port.setProtocol("tcp");
		port.setContainerPort(8080);
		pod.setName("hello-openshift");
		pod.setNamespace("test");
		
		pod.accept(new CapabilityVisitor<IPortForwardable, Object>() {

			@Override
			public Object visit(IPortForwardable capability) {
				capability.forwardPorts(new PortPair(8181, port));
				try {
					Thread.sleep(5 * 1000);
					curl();
				} catch (Exception e) {
					e.printStackTrace();
				}
				capability.stop();
				return null;
			}
		}, new Object());
	}
	
	private void curl() throws Exception {
		URL url = new URL("http://localhost:8181");
	    HttpURLConnection con = (HttpURLConnection) url.openConnection();
	    con.setRequestMethod("GET");
	    con.setDoInput(true);
	    con.connect();
	    System.out.println(IOUtils.toString(con.getInputStream()));
	    con.disconnect();
	}
}
