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


import java.io.BufferedInputStream;

import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.capability.IBinaryCapability;
import com.openshift.restclient.capability.resources.IPodLogRetrieval;
import com.openshift.restclient.model.IPod;

/**
 * 
 * @author Jeff Cantrill
 *
 */
public class OpenshiftBinaryPodLogRetrievalIntegrationTest {

	private static final String HELLO_OPENSHIFT = "docker-registry-1-uuho4";
	private IntegrationTestHelper helper = new IntegrationTestHelper();
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testLogRetrieval() {
		System.setProperty(IBinaryCapability.OPENSHIFT_BINARY_LOCATION, helper.getOpenShiftLocation());
		IClient client = helper.createClientForBasicAuth();
		IPod pod = client.get(ResourceKind.POD, HELLO_OPENSHIFT, "default");

		pod.accept(new CapabilityVisitor<IPodLogRetrieval, Object>() {

			@Override
			public Object visit(IPodLogRetrieval cap) {
				try {
					BufferedInputStream os = new BufferedInputStream(cap.getLogs(false, ""));//HELLO_OPENSHIFT));
					int c;
					while((c = os.read()) != -1) {
						System.out.print((char)c);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					cap.stop();
				}
				return null;
			}

		}, new Object());
	}
}
