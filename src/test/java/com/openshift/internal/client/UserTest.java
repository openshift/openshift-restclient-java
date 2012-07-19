/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client;

import static com.openshift.client.utils.UrlEndsWithMatcher.urlEndsWith;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IHttpClient;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftConnectionFactory;
import com.openshift.client.utils.Samples;
import com.openshift.internal.client.RestService;

/**
 * @author Xavier Coulon
 * @author Andre Dietisheim
 */
public class UserTest {

	private IUser user;
	private IHttpClient mockClient;

	@Before
	public void setup() throws Throwable {
		mockClient = mock(IHttpClient.class);
		when(mockClient.get(urlEndsWith("/broker/rest/api")))
		.thenReturn(Samples.GET_REST_API_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/user"))).thenReturn(
				Samples.GET_USER_JSON.getContentAsString());
		final IOpenShiftConnection connection = new OpenShiftConnectionFactory().getConnection(new RestService("http://mock",
				"clientId", mockClient), "foo@redhat.com", "bar");
		this.user = connection.getUser();
	}

	@Test
	public void shouldLoadUser() throws Throwable {
		// verifications
		assertThat(user.getRhlogin()).isEqualTo("foo@redhat.com");
		assertThat(user.getPassword()).isEqualTo("bar");
	}

	@Test
	public void shouldUpdateDomainNamespace() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains"))).thenReturn(
				Samples.GET_DOMAINS_1EXISTING.getContentAsString());
		when(mockClient.put(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobar"))).thenReturn(
				Samples.UPDATE_DOMAIN_ID.getContentAsString());
		final IDomain domain = user.getDomain("foobar");
		// operation
		domain.rename("foobarbaz");
		// verifications
		final IDomain updatedDomain = user.getDomain("foobarbaz");
		assertThat(updatedDomain.getId()).isEqualTo("foobarbaz");
		assertThat(LinkRetriever.retrieveLink(updatedDomain, "UPDATE").getHref()).contains("/foobarbaz");
		verify(mockClient, times(1)).put(anyMapOf(String.class, Object.class), any(URL.class));
	}

	@Ignore
	@Test
	public void shouldLoadEmptyListOfApplications() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains"))).thenReturn(
				Samples.GET_DOMAINS_1EXISTING.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				Samples.GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		// operation
		final List<IApplication> applications = user.getDomains().get(0).getApplications();
		// verifications
		assertThat(applications).hasSize(2);
		verify(mockClient, times(2)).get(any(URL.class));
	}
}
