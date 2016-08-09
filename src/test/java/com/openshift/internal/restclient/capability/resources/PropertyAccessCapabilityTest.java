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
package com.openshift.internal.restclient.capability.resources;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.model.BuildConfig;
import com.openshift.restclient.IClient;
import com.openshift.restclient.capability.resources.IPropertyAccessCapability;
import com.openshift.restclient.capability.resources.IPropertyAccessCapability.UnresolvablePathException;
import com.openshift.restclient.utils.Samples;

public class PropertyAccessCapabilityTest {
	
	private ModelNode node;
	private IPropertyAccessCapability cap;

	@Before
	public void setup() {
		IClient client = mock(IClient.class);
		node = ModelNode.fromJSONString(Samples.V1_BUILD_CONFIG.getContentAsString());
		node.get(new String[] {"spec", "strategy","sourceStrategy", "xyz"}).set(1986);
		BuildConfig config = new BuildConfig(node, client, new HashMap<>());
		cap = new PropertyAccessCapability(config);
	}
	@Test(expected=UnresolvablePathException.class)
	public void testAsMapWhenPathIsNotFound() {
		cap.asMap("foo.strategy.sourceStrategy.from");
	}

	@Test
	public void testAsString() {
		assertEquals("1986", cap.asString("spec.strategy.sourceStrategy.xyz"));
	}

	@Test(expected=UnresolvablePathException.class)
	public void testAsWhenPathIsNotFound() {
		cap.asString("spec.strategy.sourceStrategy.xyzzz");
	}
	
	@Test
	public void testAsMap() {
		Map<String, Object> from = new HashMap<>();
		from.put("kind","ImageStreamTag");
		from.put("name","ruby-20-centos7:latest");
		Map<String, Object> exp = new HashMap<>();
		exp.put("from", from);
		exp.put("incremental", true);
		exp.put("scripts", "aLocation");
		exp.put("xyz", 1986);
		
		// @TODO Why doesnt this validate?
//		Map<String, Object> envEntry = new HashMap<>();
//		envEntry.put("name", "foo");
//		envEntry.put("value", "bar");
//		List<Object> env = new ArrayList<>();
//		env.add(envEntry);
//		
//		exp.put("env", env);
		Map<String, Object> act = cap.asMap("spec.strategy.sourceStrategy.from");
		assertEquals(from, act);
	}

}
