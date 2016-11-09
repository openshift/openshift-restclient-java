/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.HashMap;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.restclient.IClient;
import com.openshift.restclient.model.IBuildConfig;

/**
 * Tests unrelated to the underlying model structure and
 * should api version independent
 * @author Jeff Maury
 *
 */
public class BuildConfigTest {

	private IBuildConfig config;

	@Before
	public void setUp() throws Exception {
	    config = new BuildConfig(new ModelNode(), mock(IClient.class), new HashMap<>());
	}

	@Test
	public void testBuildTriggersShouldReturn() {
		assertEquals(0, config.getBuildTriggers().size());
	}

}
