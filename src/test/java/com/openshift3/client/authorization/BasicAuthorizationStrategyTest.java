/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.client.authorization;

import static org.mockito.Mockito.*;

import org.mockito.Mock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.openshift.client.utils.Base64Coder;
import com.openshift3.client.authorization.BasicAuthorizationStrategy;

@RunWith(MockitoJUnitRunner.class)
public class BasicAuthorizationStrategyTest {
	
	@Mock
	private IRequest request;
	private BasicAuthorizationStrategy strategy;
	
	@Before
	public void setup(){
		strategy= new BasicAuthorizationStrategy("aUserName", "aPassword");
	}
	
	@Test
	public void testAuthorize() {
		String usernamePassword =  String.format("Basic %s", Base64Coder.encode("aUserName:aPassword"));
		strategy.authorize(request);
		verify(request).setProperty(eq("Authorization"), eq(usernamePassword));
	}

}
