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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IResource;

/**
 * @author jeff.cantrill
 */
@RunWith(MockitoJUnitRunner.class)
public class URLBuilderTest {
	
	private static final String BASE_URL = "https://localhost:8443";
	private URLBuilder builder;
	private Map<String, String> mappings = new HashMap<String, String>();
	
	@Before
	public void setup() throws MalformedURLException {
		mappings.put(ResourceKind.SERVICE, "api/v1beta3");
		mappings.put(ResourceKind.POD, "api/v1beta1");
		builder = new URLBuilder(new URL(BASE_URL), mappings);
		
	}
	
	@Test
	public void testV1Beta1() {
		IResource resource = givenAResource(ResourceKind.POD, KubernetesAPIVersion.v1beta1);
		String url = whenBuildingTheURLFor(resource, "foo");
		assertEquals(String.format("%s/api/v1beta1/pods/bar?namespace=foo", BASE_URL),url.toString());
	}

	@Test
	public void testV1Beta3() {
		IResource resource = givenAResource(ResourceKind.SERVICE, KubernetesAPIVersion.v1beta3);
		String url = whenBuildingTheURLFor(resource, "foo");
		
		assertEquals(String.format("%s/api/v1beta3/namespaces/foo/services/bar", BASE_URL),url.toString());
	}

	@Test
	public void testV1Beta3WithoutANamespace() {
		IResource resource = givenAResource(ResourceKind.SERVICE, KubernetesAPIVersion.v1beta3);
		String url = whenBuildingTheURLFor(resource, "");
		
		assertEquals(String.format("%s/api/v1beta3/services/bar", BASE_URL),url.toString());
	}
	private String whenBuildingTheURLFor(IResource resource, String namespace) {
		return builder.
			resource(resource)
			.namespace(namespace)
			.name("bar")
			.build().toString();
	}
	
	private IResource givenAResource(String kind, KubernetesAPIVersion version) {
		IResource resource = mock(IResource.class);
		when(resource.getApiVersion()).thenReturn(version.toString());
		when(resource.getKind()).thenReturn(kind);
		return resource;
	}
}
