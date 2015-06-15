/******************************************************************************* 
 * Copyright (c) 2014-2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.restclient;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.DefaultClient;
import com.openshift.internal.restclient.ResourceFactory;
import com.openshift.internal.restclient.model.Pod;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.authorization.IAuthorizationStrategy;
import com.openshift.restclient.http.IHttpClient;
import com.openshift.restclient.model.IPod;

/**
 * @author Jeff Cantrill
 */
public class DefaultClientTest {

	private DefaultClient client;
	private IHttpClient httpClient;
	private ModelNode response;
	private Pod podFrontEnd;
	private Pod podBackEnd;
	private IResourceFactory factory;
	private URL baseUrl; 
	
	private void givenAPodList(){
		podFrontEnd = factory.create("v1beta1", ResourceKind.POD);
		podFrontEnd.setName("frontend");
		podFrontEnd.setNamespace("aNamespace");
		podFrontEnd.addLabel("name", "frontend");
		podFrontEnd.addLabel("env", "production");
		
		podBackEnd = factory.create("v1beta1", ResourceKind.POD);
		podBackEnd.setName("backend");
		podBackEnd.setNamespace("aNamespace");
		podBackEnd.addLabel("name", "backend");
		podBackEnd.addLabel("env", "production");
		
		Pod otherPod = factory.create("v1beta1", ResourceKind.POD);
		otherPod.setName("other");
		otherPod.setNamespace("aNamespace");
		otherPod.addLabel("env", "production");
		
		response = new ModelNode();
		response.get("apiVersion").set("v1beta1");
		response.get("kind").set("PodList");
		ModelNode items = response.get("items");
		items.add(podFrontEnd.getNode());
		items.add(otherPod.getNode());
		items.add(podBackEnd.getNode());
	}
	
	private void givenAClient() throws MalformedURLException{
		httpClient = mock(IHttpClient.class);
		client = new DefaultClient(baseUrl, httpClient, null);
		factory = new ResourceFactory(client);
	}
	
	@Before
	public void setUp() throws Exception{
		baseUrl = new URL("http://myopenshift");
		URL kubeApi = new URL(baseUrl, "api");
		URL osApi = new URL(baseUrl, "osapi");
		givenAClient();
		givenAPodList();
		when(httpClient.get(any(URL.class), anyInt()))
			.thenReturn(response.toJSONString(false));
		when(httpClient.get(eq(kubeApi), anyInt()))
			.thenReturn("{\"versions\": [ \"v1beta1\"]}");
		when(httpClient.get(eq(osApi), anyInt()))
			.thenReturn("{\"versions\": [ \"v1beta1\"]}");
	}
	@SuppressWarnings("serial")
	@Test
	public void testListResourceFilteringWithExactMatch() throws Exception {
		Map<String, String> labels = new HashMap<String, String>(){{
			put("name","backend");
			put("env","production");
		}};
		List<IPod> pods = client.list(ResourceKind.POD, "aNamespace", labels);
		assertEquals("Expected 1 pod to be returned", 1, pods.size());
		assertEquals("Expected the frontend pod", podBackEnd, pods.get(0));
	}

	@Test 
	public void testSetAuthStrategySetsIHttpClientAuthStrategy(){
		IAuthorizationStrategy strategy = mock(IAuthorizationStrategy.class);
		client.setAuthorizationStrategy(strategy );
		
		verify(httpClient).setAuthorizationStrategy(eq(strategy));
	}
	
	@Test
	public void testListResourceFilteringNoMatch() throws Exception {
		Map<String, String> labels = new HashMap<String, String>();
		labels.put("foo", "bar");
		List<IPod> pods = client.list(ResourceKind.POD, "aNamespace", labels);
		assertEquals("Expected no pod to be returned", 0, pods.size());
	}

	@SuppressWarnings("serial")
	@Test
	public void testListResourceFilteringWithPartialMatch() throws Exception {
		Map<String, String> labels = new HashMap<String, String>(){{
			put("name","frontend");
		}};
		List<IPod> pods = client.list(ResourceKind.POD, "aNamespace", labels);
		assertEquals("Expected 1 pod to be returned", 1, pods.size());
		assertEquals("Expected the backend pod", podFrontEnd, pods.get(0));
	}
	
	@SuppressWarnings("serial")
	@Test
	public void testListResourceFilteringSingleLabel() throws Exception {
		Map<String, String> labels = new HashMap<String, String>(){{
			put("env","production");
		}};
		List<IPod> pods = client.list(ResourceKind.POD, "aNamespace", labels);
		assertEquals("Expected all pods to be returned", 3, pods.size());
	}

}
