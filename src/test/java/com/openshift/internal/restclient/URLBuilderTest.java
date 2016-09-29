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
 * @author Jeff Cantrill
 */
@RunWith(MockitoJUnitRunner.class)
public class URLBuilderTest extends TypeMapperFixture{
	
	private static final String BASE_URL = "https://localhost:8443";
	private URLBuilder builder;
	
	@Before
	public void setup() throws MalformedURLException {
		builder = new URLBuilder(new URL(BASE_URL), mapper);
	}
	
	@Test
	public void testBuildingURLForAWatchService() throws Exception {
		IResource resource = givenAResource(ResourceKind.SERVICE, KubernetesAPIVersion.v1,"foo");
		
		Map<String,String> params = new HashMap<>();
		params.put("foo", "bar");
		
		String url = builder.
				resource(resource)
				.watch()
				.addParmeter("resourceVersion", "123")
				.addParameters(params)
				.build().toString();
		assertEquals(String.format("%s/api/v1/namespaces/foo/services?watch=true&resourceVersion=123&foo=bar", BASE_URL),url.toString());
	}
	
	@Test
	public void testBuildingURLForAProjectUsingResource() throws Exception {
		IResource resource = givenAResource(ResourceKind.PROJECT, KubernetesAPIVersion.v1,"foo");
		
		String url = builder.
				resource(resource)
				.name("foo")
				.build().toString();
		assertEquals(String.format("%s/oapi/v1/projects/foo", BASE_URL),url.toString());
	}

	@Test
	public void testBaseURLWithTrailingSlash() throws Exception {
		builder = new URLBuilder(new URL(BASE_URL + "///"), mapper);
		IResource resource = givenAResource(ResourceKind.SERVICE, KubernetesAPIVersion.v1,"foo");
		
		String url = whenBuildingTheURLFor(resource, "foo");
		assertEquals(String.format("%s/api/v1/namespaces/foo/services/bar", BASE_URL),url.toString());
	}

	@Test
	public void testAddingASubResource() {
		IResource resource = givenAResource(ResourceKind.REPLICATION_CONTROLLER, KubernetesAPIVersion.v1, "foo");
		String url = builder.
			resource(resource)
			.name("bar")
			.subresource("status")
			.build().toString();
		assertEquals(String.format("%s/api/v1/namespaces/foo/replicationcontrollers/bar/status", BASE_URL),url.toString());
	}

	@Test
	public void testAddingASubContext() {
		IResource resource = givenAResource(ResourceKind.POD, KubernetesAPIVersion.v1, "https:demo-app-8-3gehi:8778");
		String url = builder.
				resource(resource)
				.name("bar")
				.subresource("proxy")
				.subContext("jolokia/exec/java.util.logging:type=Logging/getLoggerLevel/abc")
				.build().toString();
		assertEquals(String.format("%s/api/v1/namespaces/https:demo-app-8-3gehi:8778/pods/bar/proxy/jolokia/exec/java.util.logging:type=Logging/getLoggerLevel/abc", BASE_URL),url.toString());
	}

	private String whenBuildingTheURLFor(IResource resource, String namespace) {
		return builder.
			resource(resource)
			.namespace(namespace)
			.name("bar")
			.build().toString();
	}
	
	private IResource givenAResource(String kind, KubernetesAPIVersion version, String namespace) {
		IResource resource = mock(IResource.class);
		when(resource.getApiVersion()).thenReturn(version.toString());
		when(resource.getKind()).thenReturn(kind);
		when(resource.getNamespace()).thenReturn(namespace);
		return resource;
	}
}
