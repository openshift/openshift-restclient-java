/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.authorization;

import static org.mockito.Mockito.*;

import java.net.URLConnection;

import org.junit.Test;

import com.openshift.restclient.authorization.URLConnectionRequest;

public class URLConnectionAuthorizationRequestTest {

	@Test
	public void testSetProperty() {
		URLConnection conn = mock(URLConnection.class);
		
		URLConnectionRequest request = new URLConnectionRequest(conn);
		request.setProperty("foo", "bar");
		
		verify(conn).setRequestProperty(eq("foo"), eq("bar"));
	}

}
