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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.authorization.IAuthorizationContext;
import com.openshift.restclient.authorization.TokenAuthorizationStrategy;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.capability.resources.IBuildTriggerable;
import com.openshift.restclient.model.IBuild;
import com.openshift.restclient.model.IBuildConfig;

/**
 * 
 * @author jeff.cantrill
 *
 */
public class BuildTriggerIntegrationTest {

	IBuildConfig config;
	
	@Before
	public void setUp() throws Exception {
		//client
		IClient client = new IntegrationTestHelper().createClientForBasicAuth();
		IAuthorizationContext auth = client.getContext(client.getBaseURL().toString());
		client.setAuthorizationStrategy(new TokenAuthorizationStrategy(auth.getToken()));
		//create buildconfig or retrieve from already initialized server
		config = client.get(ResourceKind.BUILD_CONFIG, "ruby-sample-build", "test");
		assertNotNull(config);
	}

	@Test
	public void testTriggerABuild() {
		IBuild build = config.accept(new CapabilityVisitor<IBuildTriggerable, IBuild>() {

			@Override
			public IBuild visit(IBuildTriggerable capability) {
				return capability.trigger();
			}
		}, null);
		assertNotNull("Exp. to be able to trigger a build", build);
		System.out.println(build.toString());
	}

}
