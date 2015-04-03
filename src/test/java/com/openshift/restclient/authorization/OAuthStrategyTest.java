/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.authorization;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.mockito.Mock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.openshift.restclient.authorization.IAuthorizationClient;
import com.openshift.restclient.authorization.IAuthorizationContext;
import com.openshift.restclient.authorization.IRequest;
import com.openshift.restclient.authorization.OAuthStrategy;

@RunWith(MockitoJUnitRunner.class)
public class OAuthStrategyTest {
	@Mock
	private IRequest request;
	@Mock
	private IAuthorizationClient authclient;
	@Mock 
	private IAuthorizationContext context;
	private OAuthStrategy strategy;
	private String username = "foo";
	private String password = "bar";
	
	@Before
	public void setup(){
		when(context.getToken()).thenReturn("123");
		when(authclient.getContext(anyString(), eq(username), eq(password))).thenReturn(context);
		strategy = new OAuthStrategy("localhost:8080", authclient, username, password);
	}
	
	@Test
	public void testAuthorize() {
		strategy.authorize(request);
		verify(request).setProperty(eq("Authorization"), eq("Bearer 123"));
	}

}
