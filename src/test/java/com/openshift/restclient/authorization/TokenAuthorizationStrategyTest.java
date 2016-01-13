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

	@Test
	public void TokenStrategiesShoulEqualTokenStrategyWithDifferentToken() {
		assertThat(new TokenAuthorizationStrategy("123")).isEqualTo(new TokenAuthorizationStrategy("42"));
	}
	
	@Test
	public void TokenStrategiesWithSameTokensShouldEqual() {
		assertThat(new TokenAuthorizationStrategy("123")).isEqualTo(new TokenAuthorizationStrategy("123"));
	}

	@Test
	public void TokenStrategiesShoulNotEqualTokenStrategyWithDifferentUsername() {
		TokenAuthorizationStrategy tokenStrategy1 = new TokenAuthorizationStrategy("123", "aUsername");
		TokenAuthorizationStrategy tokenStrategy2 = new TokenAuthorizationStrategy("123", "differentUser");
		assertThat(tokenStrategy1).isNotEqualTo(tokenStrategy2);
	}

	@Test
	public void TokenStrategiesShoulEqualTokenStrategyWithSameUsername() {
		TokenAuthorizationStrategy tokenStrategy1 = new TokenAuthorizationStrategy("123", "aUser");
		TokenAuthorizationStrategy tokenStrategy2 = new TokenAuthorizationStrategy("123", "aUser");
		assertThat(tokenStrategy1).isEqualTo(tokenStrategy2);
	}

	@Test
	public void TokenStrategyShouldNotEqualNonTokenStrategy() {
		assertThat(new TokenAuthorizationStrategy("123"))
				.isNotEqualTo(new IAuthorizationStrategy() {

					@Override
					public String getToken() {
						return null;
					}


					@Override
					public String getUsername() {
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
