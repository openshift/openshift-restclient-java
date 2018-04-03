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
package com.openshift.internal.restclient;

import static org.mockito.Mockito.*;
import static junit.framework.Assert.*;

import java.util.Arrays;
import java.util.List;

import com.openshift.restclient.PredefinedResourceKind;
import org.junit.Before;
import org.junit.Test;

import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IService;

/**
 * @author Jeff Cantrill
 */
public class ResourceFactoryTest {

	private ResourceFactory factory;

	@Before
	public void setup() {
		IClient client = mock(IClient.class);
		when(client.getOpenShiftAPIVersion()).thenReturn(OpenShiftAPIVersion.v1.toString());
		factory = new ResourceFactory(client);
	}
	
	/*
	 * Validate the implementation classes implemented the expected constructor
	 */
	@Test
	public void testV1Beta3Implementations() {
		List<String> v1beta3Exlusions = Arrays.asList(new String [] {
				PredefinedResourceKind.CONFIG.getIdentifier(),
				PredefinedResourceKind.PROCESSED_TEMPLATES.getIdentifier()
		});
		final String version = OpenShiftAPIVersion.v1beta3.toString();
		for (ResourceKind kind : PredefinedResourceKind.values()) {
			if(!v1beta3Exlusions.contains(kind)) {
				factory.create(version, kind.getIdentifier());
			}
		}
	}
	
	@Test
	public void testStubWithNamespace() {
		IService service = factory.stub(PredefinedResourceKind.SERVICE.getIdentifier(), "foo", "bar");
		assertEquals("foo", service.getName());
		assertEquals("bar", service.getNamespaceName());
	}

	@Test
	public void testCreateWithKindAndName() {
		IService service = factory.create("v1", PredefinedResourceKind.SERVICE.getIdentifier(), "foo");
		assertEquals("foo", service.getName());
	}

}
