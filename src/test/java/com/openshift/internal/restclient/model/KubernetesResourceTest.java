/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.capability.resources.IDeploymentTraceability;
import com.openshift.restclient.capability.resources.ITemplateTraceability;

/**
 * @author Jeff Cantrill
 */
public class KubernetesResourceTest {

	private ModelNode node;
	private KubernetesResource resource;

	@Before
	public void setup(){
		ModelNode annotations = new ModelNode();
		annotations.get("foo").set("bar");
		annotations.get("template").set("foobar");
		node = new ModelNode();
		node.get("annotations").set(annotations);
		
		resource = new KubernetesResource(node, null, ResourcePropertiesRegistry.getInstance().get("v1beta1", ResourceKind.Service));
		
	}
	@Test
	public void testGetAnnotation() {
		assertEquals("bar", resource.getAnnotation("foo"));
	}

	@Test
	public void isAnnotatedReturnsTrueForKnownAnnotation() {
		assertTrue(resource.isAnnotatedWith("foo"));
	}

	@Test
	public void isAnnotatedReturnsFalseForUnKnownAnnotation() {
		assertFalse(resource.isAnnotatedWith("bar"));
	}

	@Test
	public void supportsIsFalseForUnsupportedCapability() {
		assertFalse("Expected to not support capability because IClient is null",resource.supports(IDeploymentTraceability.class));
	}

	@Test
	public void getCapabilityReturnsNonNullWhenSupportedCapability() {
		assertTrue("Exp. to support capability since resource has template annotation", resource.supports(ITemplateTraceability.class));
		assertNotNull(resource.getCapability(ITemplateTraceability.class));
	}
	
	@Test
	public void testAcceptVisitor(){
		final List<Boolean> visited = new ArrayList<Boolean>();
		resource.accept(new CapabilityVisitor<ITemplateTraceability, Object>(){

			@Override
			public Object  visit(ITemplateTraceability capability) {
				visited.add(Boolean.TRUE);
				return (Object)null;
			}
			
		}, new Object());
		assertEquals("Exp. the visitor to be visited", 1, visited.size());
	}
}
