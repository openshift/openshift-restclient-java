/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.authorization;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.openshift.restclient.utils.Base64Coder;

/**
 * @author Jeff Cantrill
 */
@RunWith(MockitoJUnitRunner.class)
public class BasicAuthorizationStrategyTest {
	
	@Mock
	private IRequest request;
	private BasicAuthorizationStrategy strategy;
	
	@Before
	public void setup(){
		strategy= new BasicAuthorizationStrategy("aUserName", "aPassword",null);
	}
	
	@Test
	public void testAuthorize() {
		String usernamePassword =  String.format("Basic %s", Base64Coder.encode("aUserName:aPassword"));
		strategy.authorize(request);
		verify(request).setProperty(eq("Authorization"), eq(usernamePassword));
	}
	
	@Test
	public void BasicStrategyShouldEqualBasicStrategyWithDifferentUsername() {
		assertThat(new BasicAuthorizationStrategy("aUsername", "aPassword", null))
				.isNotEqualTo(new BasicAuthorizationStrategy("differentUsername", "aPassword", null));
	}

	@Test
	public void BasicStrategyShouldEqualBasicStrategyWithDifferentToken() {
		assertThat(new BasicAuthorizationStrategy("aUsername", "aPassword", "123"))
				.isEqualTo(new BasicAuthorizationStrategy("aUsername", "aPassword", "234"));
	}

	@Test
	public void BasicStrategyShouldEqualBasicStrategyWithDifferentPassword() {
		assertThat(new BasicAuthorizationStrategy("aUsername", "aPassword", null))
			.isEqualTo(new BasicAuthorizationStrategy("aUsername", "differentPassword", null));
	}

	@Test
	public void BasicStrategyShouldNotEqualNonBasicStrategy() {
		assertThat(new BasicAuthorizationStrategy("aUsername", "aPassword", null))
				.isNotEqualTo(new IAuthorizationStrategy() {


					@Override
					public String getUsername() {
						return null;
					}

					@Override
					public String getToken() {
						return null;
					}

					@Override
					public void authorize(IRequest request) {
					}

					@Override
					public void accept(IAuthorizationStrategyVisitor visitor) {
					}
				});
	}
}
