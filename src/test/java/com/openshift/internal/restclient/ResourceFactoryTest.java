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
				ResourceKind.CONFIG, 
				ResourceKind.PROCESSED_TEMPLATES
		});
		final String version = OpenShiftAPIVersion.v1beta3.toString();
		for (String kind : ResourceKind.values()) {
			if(!v1beta3Exlusions.contains(kind)) {
				factory.create(version, kind);
			}
		}
	}
	
	@Test
	public void testStubWithNamespace() {
		IService service = factory.stub(ResourceKind.SERVICE, "foo", "bar");
		assertEquals("foo", service.getName());
		assertEquals("bar", service.getNamespace());
	}

}
