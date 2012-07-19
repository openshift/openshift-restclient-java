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

import static com.openshift.client.utils.MockUtils.anyForm;
import static com.openshift.client.utils.Samples.ADD_DOMAIN_JSON;
import static com.openshift.client.utils.Samples.DELETE_DOMAIN_JSON;
import static com.openshift.client.utils.Samples.GET_APPLICATIONS_WITH2APPS_JSON;
import static com.openshift.client.utils.Samples.GET_DOMAIN;
import static com.openshift.client.utils.Samples.GET_DOMAINS_1EXISTING;
import static com.openshift.client.utils.Samples.GET_DOMAINS_NOEXISTING_JSON;
import static com.openshift.client.utils.Samples.UPDATE_DOMAIN_ID;
import static com.openshift.client.utils.UrlEndsWithMatcher.urlEndsWith;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import com.openshift.client.IDomain;
import com.openshift.client.IGearProfile;
import com.openshift.client.IHttpClient;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftConnectionFactory;
import com.openshift.client.OpenShiftEndpointException;
import com.openshift.client.OpenShiftException;
import com.openshift.client.utils.Samples;
import com.openshift.internal.client.httpclient.BadRequestException;

/**
 * @author Xavier Coulon
 * @author Andre Dietisheim
 */
public class DomainResourceTest {

	private IUser user;
	private IHttpClient mockClient;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();

	@Before
	public void setup() throws Throwable {
		mockClient = mock(IHttpClient.class);
		when(mockClient.get(urlEndsWith("/broker/rest/api")))
				.thenReturn(Samples.GET_REST_API_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/user"))).thenReturn(Samples.GET_USER_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains"))).thenReturn(GET_DOMAINS_1EXISTING.getContentAsString());
		final IOpenShiftConnection connection = new OpenShiftConnectionFactory().getConnection(new RestService(
				"http://mock", "clientId", mockClient), "foo@redhat.com", "bar");
		this.user = connection.getUser();
	}

	@Test
	public void shouldLoadEmptyListOfDomains() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains"))).thenReturn(GET_DOMAINS_NOEXISTING_JSON.getContentAsString());
		// operation
		final List<IDomain> domains = user.getDomains();
		// verifications
		assertThat(domains).hasSize(0);
		// 3 calls: /API + /API/user + /API/domains
		verify(mockClient, times(3)).get(any(URL.class));
	}

	@Test
	public void shouldLoadSingleUserDomain() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains"))).thenReturn(GET_DOMAINS_1EXISTING.getContentAsString());
		// operation
		final List<IDomain> domains = user.getDomains();
		// verifications
		assertThat(domains).hasSize(1);
		// 3 calls: /API + /API/user + /API/domains
		verify(mockClient, times(3)).get(any(URL.class));
	}

	@Test
	public void shouldCreateNewDomain() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains"))).thenReturn(GET_DOMAINS_NOEXISTING_JSON.getContentAsString());
		when(mockClient.post(anyMapOf(String.class, Object.class), urlEndsWith("/domains"))).thenReturn(
				ADD_DOMAIN_JSON.getContentAsString());
		// operation
		final IDomain domain = user.createDomain("foobar2");
		// verifications
		assertThat(domain.getId()).isEqualTo("foobar2");
		assertThat(domain.getSuffix()).isEqualTo("stg.rhcloud.com");
	}

	@Test(expected = OpenShiftException.class)
	public void shouldNotRecreateExistingDomain() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains"))).thenReturn(GET_DOMAINS_1EXISTING.getContentAsString());
		when(mockClient.post(anyMapOf(String.class, Object.class), urlEndsWith("/domains"))).thenReturn(
				ADD_DOMAIN_JSON.getContentAsString());
		// operation
		user.createDomain("foobar");
		// verifications
		// expect an exception
	}

	@Test
	public void shouldDestroyDomain() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains"))).thenReturn(GET_DOMAINS_1EXISTING.getContentAsString());
		when(mockClient.delete(anyForm(), urlEndsWith("/domains/foobar"))).thenReturn(
				DELETE_DOMAIN_JSON.getContentAsString());
		// operation
		final IDomain domain = user.getDomain("foobar");
		domain.destroy();
		// verifications
		assertThat(user.getDomain("foobar")).isNull();
		assertThat(user.getDomains()).isEmpty();
	}

	@Test
	public void shouldNotDestroyDomainWithApp() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains"))).thenReturn(GET_DOMAINS_1EXISTING.getContentAsString());
		final BadRequestException badRequestException = new BadRequestException(
				"Domain contains applications. Delete applications first or set force to true.", null);
		when(mockClient.delete(anyForm(), urlEndsWith("/domains/foobar"))).thenThrow(badRequestException);
		// operation
		final IDomain domain = user.getDomain("foobar");
		try {
			domain.destroy();
			fail("Expected an exception here..");
		} catch (OpenShiftEndpointException e) {
			assertThat(e.getCause()).isInstanceOf(BadRequestException.class);
		}
		// verifications
		assertThat(domain).isNotNull();
		assertThat(user.getDomains()).isNotEmpty().contains(domain);
	}

	@Test
	public void shouldUpdateDomainId() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains"))).thenReturn(GET_DOMAINS_1EXISTING.getContentAsString());
		when(mockClient.put(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobar"))).thenReturn(
				UPDATE_DOMAIN_ID.getContentAsString());
		final IDomain domain = user.getDomain("foobar");
		// operation
		domain.rename("foobarbaz");
		// verifications
		final IDomain updatedDomain = user.getDomain("foobarbaz");
		assertThat(updatedDomain.getId()).isEqualTo("foobarbaz");
		assertThat(LinkRetriever.retrieveLink(updatedDomain, "UPDATE").getHref()).contains("/foobarbaz");
		verify(mockClient, times(1)).put(anyMapOf(String.class, Object.class), any(URL.class));
	}

	@Test
	public void shouldListAvailableGearSizes() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains"))).thenReturn(GET_DOMAINS_1EXISTING.getContentAsString());
		when(mockClient.put(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobar"))).thenReturn(
				UPDATE_DOMAIN_ID.getContentAsString());
		final IDomain domain = user.getDomain("foobar");
		// operation
		List<IGearProfile> availableGearSizes = domain.getAvailableGearProfiles();
		// verifications
		assertThat(availableGearSizes).onProperty("name").contains("small", "micro", "medium", "large", "exlarge",
				"jumbo");
	}

	@Test
	public void shouldRefreshDomainAndReloadApplications() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar"))).thenReturn(GET_DOMAIN.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		final IDomain domain = user.getDomain("foobar");
		domain.getApplications();
		// operation
		domain.refresh();
		// verifications
		verify(mockClient, times(1)).get(urlEndsWith("/domains/foobar")); // explicit refresh on this location
		verify(mockClient, times(2)).get(urlEndsWith("/domains/foobar/applications")); // two calls, before and while refresh
	}

	@Test
	public void shouldRefreshDomainAndNotReloadApplications() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar"))).thenReturn(GET_DOMAIN.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		final IDomain domain = user.getDomain("foobar");
		// operation
		domain.refresh();
		// verifications
		verify(mockClient, times(1)).get(urlEndsWith("/domains")); // explicit refresh on this location
		verify(mockClient, times(0)).get(urlEndsWith("/domains/foobar/applications")); // no call, neither before and while refresh
	}

	
	@Test
	@Ignore
	public void shouldRefreshDomain() throws Throwable {
		fail("not implemented yet");
	}

	@Test
	@Ignore
	public void shouldNotReloadDomainTwice() throws Throwable {
		fail("not implemented yet");
	}

	@Test
	@Ignore
	public void shouldNotifyAfterDomainCreated() throws Throwable {
		fail("not implemented yet");
	}

	@Test
	@Ignore
	public void shouldNotifyAfterDomainUpdated() throws Throwable {
		fail("not implemented yet");
	}

	@Test
	@Ignore
	public void shouldNotifyAfterDomainDestroyed() throws Throwable {
		fail("not implemented yet");
	}
}
