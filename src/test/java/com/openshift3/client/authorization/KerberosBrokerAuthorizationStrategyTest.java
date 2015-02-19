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

@RunWith(MockitoJUnitRunner.class)
public class KerberosBrokerAuthorizationStrategyTest {
	
	@Mock
	private IRequest request;
	private KerbrosBrokerAuthorizationStrategy strategy;
	
	@Before
	public void setup(){
		strategy= new KerbrosBrokerAuthorizationStrategy("anAuthKey", "anAuthIv");
	}
	
	@Test
	public void testAuthorize() {
		strategy.authorize(request);
		
		verify(request).setProperty(eq("broker_auth_key"), eq("anAuthKey"));
		verify(request).setProperty(eq("broker_auth_iv"), eq("anAuthIv"));
	}

}
