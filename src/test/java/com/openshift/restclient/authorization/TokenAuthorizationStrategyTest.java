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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.openshift.restclient.authorization.TokenAuthorizationStrategy;
import com.openshift.restclient.authorization.IRequest;

/**
 * @author Jeff Cantrill
 */
@RunWith(MockitoJUnitRunner.class)
public class TokenAuthorizationStrategyTest {
	
	@Mock
	private IRequest request;
	private TokenAuthorizationStrategy strategy;
	
	@Before
	public void setup(){
		strategy = new TokenAuthorizationStrategy("123");
	}
	
	@Test
	public void testAuthorize() {
		strategy.authorize(request);
		
		verify(request).setProperty(eq("Authorization"), eq("Bearer 123"));
	}

}
