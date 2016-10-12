/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.deploy;

import com.openshift.restclient.IClient;
import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

public class DeployRequestTest {
	@Mock private IClient client;
	private DeploymentRequest config;
	private ModelNode node = new ModelNode();
	
	@Before
	public void setup(){
		config = new DeploymentRequest(node, new HashMap<String, String[]>());
	}
	
	@Test
	public void testDeploymentRequest(){

		config.setForce( true );
		assertTrue("Exp. isForce to be true when set to true", config.isForce());
		config.setLatest(true);
		assertEquals("Exp. isLatest to be true when set to true",true, config.isLatest());
		config.setName("foo");
		assertEquals("foo", config.getName());

	}

}
