/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.OpenShiftAPIVersion;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.internal.restclient.model.properties.ResourcePropertyKeys;
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
		this.node = createv1Beta1ModelNode();
		this.resource = createKubernetesResource(OpenShiftAPIVersion.v1beta1.toString(), node);
	}

	private ModelNode createv1Beta1ModelNode() {
		node = new ModelNode();

		ModelNode annotations = new ModelNode();
		annotations.get("foo").set("bar");
		annotations.get("template").set("foobar");
		node.get(ResourcePropertyKeys.ANNOTATIONS).set(annotations);
		
		node.get(ResourcePropertyKeys.NAME).set("bartender");
		node.get(ResourcePropertyKeys.NAMESPACE).set("foofighters");
		// return dummy kind to avoid NPE
		node.get(ResourcePropertyKeys.KIND).set(ResourceKind.LIST.toString());

		return node;
	}
	
	private KubernetesResource createKubernetesResource(String modelVersion, ModelNode node) {
		return new KubernetesResource(node, null,
				ResourcePropertiesRegistry.getInstance().get(modelVersion, ResourceKind.SERVICE)) {};
	}
	
	@Test
	public void testSetAnnoationWithNullValueShouldReturnGracefully() {
		resource.setAnnoation("black",null);
	}
	
	@Test
	public void testSetAnnoation() {
		resource.setAnnoation("black", "white");
		assertEquals("white",resource.getAnnotation("black"));
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

	@Test
	public void shouldSameHashCodeOnAddedLabels() {
		// pre-condition
		int hashCodeBeforeChange = resource.hashCode();
		
		// operation
		resource.set(ResourcePropertyKeys.LABELS, "kungfoo");

		// verification
		int hashCodeAfterChange = resource.hashCode();
		assertEquals(hashCodeBeforeChange, hashCodeAfterChange);
	}

	@Test
	public void shouldDifferentHashCodeOnDifferentName() {
		// pre-condition
		int hashCodeBeforeChange = resource.hashCode();
		
		// operation
		resource.set(ResourcePropertyKeys.NAME, "brucefoolee");
		
		// verification
		int hashCodeAfterChange = resource.hashCode();
		assertThat(hashCodeBeforeChange).isNotEqualTo(hashCodeAfterChange);
	}

	@Test
	public void shouldDifferentHashCodeOnDifferentResourceKind() {
		// pre-condition
		int hashCodeBeforeChange = resource.hashCode();
		
		// operation
		node.get(ResourcePropertyKeys.KIND).set(ResourceKind.EVENT.toString());
		
		// verification
		int hashCodeAfterChange = resource.hashCode();
		assertThat(hashCodeBeforeChange).isNotEqualTo(hashCodeAfterChange);
	}

	@Test
	public void shouldDifferentHashCodeOnDifferentNamespace() {
		// pre-condition
		int hashCodeBeforeChange = resource.hashCode();
		
		// operation
		node.get(ResourcePropertyKeys.NAMESPACE).set("barfoo");
		
		// verification
		int hashCodeAfterChange = resource.hashCode();
		assertThat(hashCodeBeforeChange).isNotEqualTo(hashCodeAfterChange);
	}
	
	@Test
	public void shouldEqualsOnAddedLabels() {
		// pre-condition
		KubernetesResource otherResource = createKubernetesResource(OpenShiftAPIVersion.v1beta1.toString(), node);
		assertEquals(resource, otherResource);

		// operation
		otherResource.set(ResourcePropertyKeys.LABELS, "bruceleefoo");

		// verification
		assertEquals(resource, otherResource);
	}

	@Test
	public void shouldNotEqualsOnDifferentName() {
		// pre-condition
		KubernetesResource otherResource = createKubernetesResource(OpenShiftAPIVersion.v1beta1.toString(), createv1Beta1ModelNode());
		assertEquals(resource, otherResource);

		// operation
		otherResource.set(ResourcePropertyKeys.NAME, "kungfoo");
		
		// verification
		assertThat(resource).isNotEqualTo(otherResource);
	}

	@Test
	public void shouldNotEqualsOnDifferentNamespace() {
		// pre-condition
		KubernetesResource otherResource = createKubernetesResource(OpenShiftAPIVersion.v1beta1.toString(), createv1Beta1ModelNode());
		assertEquals(resource, otherResource);

		// operation
		otherResource.set(ResourcePropertyKeys.NAMESPACE, "karate");
		
		// verification
		assertThat(resource).isNotEqualTo(otherResource);
	}

	@Test
	public void shouldNotEqualsOnDifferentResourceKind() {
		// pre-condition
		KubernetesResource otherResource = createKubernetesResource(OpenShiftAPIVersion.v1beta1.toString(), createv1Beta1ModelNode());
		assertEquals(resource, otherResource);

		// operation
		otherResource.set(ResourcePropertyKeys.KIND, ResourceKind.EVENT.toString());
		
		// verification
		assertThat(resource).isNotEqualTo(otherResource);
	}

}
