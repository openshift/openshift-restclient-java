/*******************************************************************************
 * Copyright (c) 2014-2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.openshift.internal.restclient.model.Pod;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.authorization.BasicAuthorizationStrategy;
import com.openshift.restclient.authorization.IAuthorizationStrategy;
import com.openshift.restclient.authorization.TokenAuthorizationStrategy;
import com.openshift.restclient.http.IHttpClient;
import com.openshift.restclient.model.IPod;

/**
 * @author Jeff Cantrill
 * @author Andre Dietisheim
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultClientTest extends TypeMapperFixture{

	private static final String VERSION = "v1";

	private DefaultClient client;
//	private IHttpClient httpClient;
	private ModelNode response;
	private Pod podFrontEnd;
	private Pod podBackEnd;
	private IResourceFactory factory;
	private URL baseUrl;
	

	@Before
	public void setUp() throws Exception{
		super.setUp();
		this.baseUrl = new URL("http://myopenshift");
		givenAClient();
		givenAPodList();
		when(getHttpClient().get(eq(new URL("http://myopenshift/api/v1/namespaces/aNamespace/pods")), anyInt()))
			.thenReturn(response.toJSONString(false));
	}

	private void givenAClient() throws MalformedURLException{
		factory = new ResourceFactory(null);
		client = new DefaultClient(baseUrl, getHttpClient(), null, factory, null, null, getApiTypeMapper(), IHttpClient.NO_TIMEOUT);
	}

	private void givenAPodList(){
		this.podFrontEnd = factory.create(VERSION, ResourceKind.POD);
		podFrontEnd.setName("frontend");
		podFrontEnd.setNamespace("aNamespace");
		podFrontEnd.addLabel("name", "frontend");
		podFrontEnd.addLabel("env", "production");

		this.podBackEnd = factory.create(VERSION, ResourceKind.POD);
		podBackEnd.setName("backend");
		podBackEnd.setNamespace("aNamespace");
		podBackEnd.addLabel("name", "backend");
		podBackEnd.addLabel("env", "production");

		Pod otherPod = factory.create(VERSION, ResourceKind.POD);
		otherPod.setName("other");
		otherPod.setNamespace("aNamespace");
		otherPod.addLabel("env", "production");

		this.response = new ModelNode();
		response.get("apiVersion").set(VERSION);
		response.get("kind").set("PodList");
		ModelNode items = response.get("items");
		items.add(podFrontEnd.getNode());
		items.add(otherPod.getNode());
		items.add(podBackEnd.getNode());
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

		verify(getHttpClient()).setAuthorizationStrategy(eq(strategy));
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

	@Test
	public void clientShouldEqualClientWithSameUrl() throws Exception {
		assertThat(new DefaultClient(baseUrl, null, null, null))
			.isEqualTo(new DefaultClient(baseUrl, null, null, null));
	}
		
	@Test
	public void clientShouldNotEqualClientWithDifferentUrl() throws Exception {
		assertThat(new DefaultClient(baseUrl, null, null, null))
				.isNotEqualTo(new DefaultClient(new URL("http://localhost:8443"), null, null, null));
	}
	
	public void client_should_equal_client_with_same_TokenAuthStrategy_with_different_token() throws Exception {
		DefaultClient tokenClientOne = new DefaultClient(baseUrl, null, null, null);
		tokenClientOne.setAuthorizationStrategy(new TokenAuthorizationStrategy("tokenOne", "aUser"));

		DefaultClient tokenClientTwo = new DefaultClient(baseUrl, null, null, null);
		tokenClientTwo.setAuthorizationStrategy(new TokenAuthorizationStrategy("tokenTwo", "aUser"));

		assertThat(tokenClientOne).isEqualTo(tokenClientTwo);
	}

	@Test
	public void client_should_not_equal_client_with_same_TokenAuthStrategy_with_different_username() throws Exception {
		DefaultClient tokenClientOne = new DefaultClient(baseUrl, null, null, null);
		tokenClientOne.setAuthorizationStrategy(new TokenAuthorizationStrategy("aToken", "aUser"));

		DefaultClient tokenClientTwo = new DefaultClient(baseUrl, null, null, null);
		tokenClientTwo.setAuthorizationStrategy(new TokenAuthorizationStrategy("aToken", "differentUser"));

		assertThat(tokenClientOne).isNotEqualTo(tokenClientTwo);
	}

	@Test
	public void client_should_not_equal_client_with_same_BasicAuthStrategy_with_different_username() throws Exception {
		DefaultClient tokenClientOne = new DefaultClient(baseUrl, null, null, null);
		tokenClientOne.setAuthorizationStrategy(new BasicAuthorizationStrategy("aUser", "aPassword", "aToken"));

		DefaultClient tokenClientTwo = new DefaultClient(baseUrl, null, null, null);
		tokenClientTwo.setAuthorizationStrategy(new BasicAuthorizationStrategy("differentUser", "aPassword", "aToken"));

		assertThat(tokenClientOne).isNotEqualTo(tokenClientTwo);
	}

	@Test
	public void client_should_equal_client_with_same_BasicAuthStrategy_with_different_password_and_different_token() throws Exception {
		DefaultClient tokenClientOne = new DefaultClient(baseUrl, null, null, null);
		tokenClientOne.setAuthorizationStrategy(new BasicAuthorizationStrategy("aUser", "aPassword", "aToken"));

		DefaultClient tokenClientTwo = new DefaultClient(baseUrl, null, null, null);
		tokenClientTwo.setAuthorizationStrategy(new BasicAuthorizationStrategy("aUser", "differentPassword", "differentToken"));

		assertThat(tokenClientOne).isEqualTo(tokenClientTwo);
	}

	@Test
	public void tokenAuthClient_should_equal_basicAuthclient_with_same_username() throws Exception {
		DefaultClient tokenClient = new DefaultClient(baseUrl, null, null, null);
		tokenClient.setAuthorizationStrategy(new TokenAuthorizationStrategy("aToken", "aUser"));

		DefaultClient basicAuthClient = new DefaultClient(baseUrl, null, null, null);
		basicAuthClient.setAuthorizationStrategy(new BasicAuthorizationStrategy("aUser", "aPassword", "differentToken"));

		assertThat(tokenClient).isEqualTo(basicAuthClient);
	}

	@Test
	public void tokenAuthClient_should_not_equal_basicAuthclient_with_different_username() throws Exception {
		DefaultClient tokenClient = new DefaultClient(baseUrl, null, null, null);
		tokenClient.setAuthorizationStrategy(new TokenAuthorizationStrategy("aToken", "aUser"));

		DefaultClient basicAuthClient = new DefaultClient(baseUrl, null, null, null);
		basicAuthClient.setAuthorizationStrategy(new BasicAuthorizationStrategy("differentUser", "aPassword", "aToken"));

		assertThat(tokenClient).isNotEqualTo(basicAuthClient);
	}

	@Test
	public void basicAuthClient_should_equal_tokenClient_with_same_username() throws Exception {
		DefaultClient basicAuthClient = new DefaultClient(baseUrl, null, null, null);
		basicAuthClient.setAuthorizationStrategy(new BasicAuthorizationStrategy("aUser", "aPassword", "differentToken"));

		DefaultClient tokenClient = new DefaultClient(baseUrl, null, null, null);
		tokenClient.setAuthorizationStrategy(new TokenAuthorizationStrategy("aToken", "aUser"));

		assertThat(basicAuthClient).isEqualTo(tokenClient);
	}

	@Test
	public void clientShouldEqualClientWithDifferentCert() throws Exception {
		X509Certificate certOne = mock(X509Certificate.class);
		when(certOne.getSigAlgName()).thenReturn("sig1");
		DefaultClient certClientOne = new DefaultClient(baseUrl, null, null, null, "cert1", certOne, IHttpClient.NO_TIMEOUT);

		X509Certificate certTwo = mock(X509Certificate.class);
		when(certTwo.getSigAlgName()).thenReturn("sig2");
		DefaultClient certClientTwo = new DefaultClient(baseUrl, null, null, null, "cert2", certTwo, IHttpClient.NO_TIMEOUT);

		assertThat(certClientTwo).isEqualTo(certClientOne);
	}




}
