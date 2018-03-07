/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.capability.resources.testutils;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.net.MalformedURLException;
import java.net.URL;

import com.openshift.restclient.IClient;
import com.openshift.restclient.authorization.IAuthorizationContext;
import com.openshift.restclient.capability.resources.IPortForwardable.PortPair;
import com.openshift.restclient.model.IPod;

public class BinaryCapabilityTestMocks {

	public static final String SERVER_URL = "https://localhost:8443";
	public static final String TOKEN = "phWDXIqSspYBZARPQIzqaevHGDIxduQGbhcZuwj48EI";

	public static final String OC_LOCATION = "/path/to/oc";

	public static final String POD_NAME = "papa-smurf";
	public static final String POD_NAMESPACE = "the-smurfs";

	public static IClient mockClient() throws MalformedURLException {
		IClient client = mock(IClient.class);
		doReturn(new URL(SERVER_URL)).when(client).getBaseURL();
		IAuthorizationContext context = mockAuthorizationContext();
		doReturn(context).when(client).getAuthorizationContext();
		return client;
	}
	
	private static IAuthorizationContext mockAuthorizationContext() {
		IAuthorizationContext context = mock(IAuthorizationContext.class);
		doReturn(TOKEN).when(context).getToken();
		return context;
	}

	public static IPod mockPod() {
		IPod pod = mock(IPod.class);
		doReturn(POD_NAME).when(pod).getName();
		doReturn(POD_NAMESPACE).when(pod).getNamespace();
		return pod;
	}
	
	public static PortPair mockPortPair(int localPort, int remotePort) {
		PortPair ports = mock(PortPair.class);
		doReturn(localPort).when(ports).getLocalPort();
		doReturn(remotePort).when(ports).getRemotePort();
		return ports;
	}
}
