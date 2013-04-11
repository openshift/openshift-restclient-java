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
import static com.openshift.client.utils.Samples.ADD_APPLICATION_JSON;
import static com.openshift.client.utils.Samples.ADD_DOMAIN_JSON;
import static com.openshift.client.utils.Samples.DELETE_DOMAIN_JSON;
import static com.openshift.client.utils.Samples.GET_APPLICATIONS_WITH1APP_JSON;
import static com.openshift.client.utils.Samples.GET_APPLICATIONS_WITH2APPS_JSON;
import static com.openshift.client.utils.Samples.GET_APPLICATIONS_WITHNOAPP_JSON;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import com.openshift.client.ApplicationScale;
import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IGearProfile;
import com.openshift.client.IHttpClient;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.InvalidCredentialsOpenShiftException;
import com.openshift.client.OpenShiftConnectionFactory;
import com.openshift.client.OpenShiftEndpointException;
import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.utils.Samples;
import com.openshift.internal.client.httpclient.BadRequestException;
import com.openshift.internal.client.httpclient.HttpClientException;
import com.openshift.internal.client.httpclient.UnauthorizedException;

/**
 * @author Xavier Coulon
 * @author Andre Dietisheim
 */
public class DomainResourceTest {

	private IUser user;
	private IDomain domain;
	private IHttpClient mockClient;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();

	@Before
	public void setup() throws Throwable {
		this.mockClient = createMockClient(GET_DOMAINS_1EXISTING);
		this.user = createUser(mockClient);
		this.domain = user.getDomain("foobar");
	}

	private IHttpClient createMockClient(Samples domainsResponse) throws SocketTimeoutException, Throwable {
		IHttpClient mockClient = mock(IHttpClient.class);
		when(mockClient.get(urlEndsWith("/broker/rest/api")))
				.thenReturn(Samples.GET_REST_API_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/user"))).thenReturn(Samples.GET_USER_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains"))).thenReturn(domainsResponse.getContentAsString());
		return mockClient;
	}

	private IUser createUser(IHttpClient client) throws FileNotFoundException, IOException {
		final IOpenShiftConnection connection = new OpenShiftConnectionFactory().getConnection(new RestService(
				"http://mock", "clientId", client), "foo@redhat.com", "bar");
		return connection.getUser();
	}

	@Test
	public void shouldLoadEmptyListOfDomains() throws Throwable {
		// pre-conditions
		IHttpClient client = createMockClient(GET_DOMAINS_NOEXISTING_JSON);
		IUser user = createUser(client);
		// operation
		final List<IDomain> domains = user.getDomains();
		// verifications
		assertThat(domains).hasSize(0);
		// 3 calls: /API + /API/user + /API/domains
		verify(client, times(3)).get(any(URL.class));
	}

	@Test
	public void shouldLoadSingleUserDomain() throws Throwable {
		// pre-conditions
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
		when(mockClient.put(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobar"))).thenReturn(
				UPDATE_DOMAIN_ID.getContentAsString());
		final IDomain domain = user.getDomain("foobar");
		// operation
		List<IGearProfile> availableGearSizes = domain.getAvailableGearProfiles();
		// verifications
		assertThat(availableGearSizes).onProperty("name").contains(
				"small", "micro", "medium", "large", "exlarge","jumbo");
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
	public void shouldLoadListOfApplicationsWithNoElement() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITHNOAPP_JSON.getContentAsString());
		// operation
		final List<IApplication> apps = domain.getApplications();
		// verifications
		assertThat(apps).isEmpty();
		// 4 calls: /API + /API/user + /API/domains +
		// /API/domains/foobar/applications
		verify(mockClient, times(4)).get(any(URL.class));
	}

	@Test
	public void shouldLoadListOfApplicationsWith1Element() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH1APP_JSON.getContentAsString());
		// operation
		final List<IApplication> apps = domain.getApplications();
		// verifications
		assertThat(apps).hasSize(1);
		// 4 calls: /API + /API/user + /API/domains +
		// /API/domains/foobar/applications
		verify(mockClient, times(4)).get(any(URL.class));

	}

	@Test
	public void shouldLoadListOfApplicationsWith2Elements() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		// operation
		final List<IApplication> apps = domain.getApplications();
		// verifications
		assertThat(apps).hasSize(2);
		// 4 calls: /API + /API/user + /API/domains +
		// /API/domains/foobar/applications
		verify(mockClient, times(4)).get(any(URL.class));
	}

	@Test(expected = InvalidCredentialsOpenShiftException.class)
	public void shouldNotLoadListOfApplicationsWithInvalidCredentials() throws OpenShiftException,
			HttpClientException, SocketTimeoutException {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenThrow(
				new UnauthorizedException("invalid credentials (mock)", null));
		// operation
		domain.getApplications();
		// verifications
		// expect an exception
	}

	@Test
	public void shouldCreateApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITHNOAPP_JSON.getContentAsString());
		when(mockClient.post(anyForm(), urlEndsWith("/domains/foobar/applications"))).thenReturn(
				ADD_APPLICATION_JSON.getContentAsString());
		// operation
		final IStandaloneCartridge cartridge = new StandaloneCartridge("jbossas-7");
		final IApplication app = domain.createApplication("sample", cartridge, ApplicationScale.NO_SCALE, null);
		// verifications
		assertThat(app.getName()).isEqualTo("sample");
		assertThat(app.getGearProfile().getName()).isEqualTo("small");
		assertThat(app.getApplicationScale()).isEqualTo(ApplicationScale.NO_SCALE);
		assertThat(app.getApplicationUrl()).isEqualTo("http://sample-foobar.stg.rhcloud.com/");
		assertThat(app.getCreationTime()).isNotNull();
		assertThat(app.getGitUrl()).isNotNull().startsWith("ssh://")
				.endsWith("@sample-foobar.stg.rhcloud.com/~/git/sample.git/");
		assertThat(app.getCartridge()).isEqualTo(cartridge);
		assertThat(app.getUUID()).isNotNull();
		assertThat(app.getDomain()).isEqualTo(domain);
		assertThat(LinkRetriever.retrieveLinks(app)).hasSize(14);
		assertThat(domain.getApplications()).hasSize(1).contains(app);
	}

	@Test(expected = OpenShiftException.class)
	public void shouldNotCreateApplicationWithMissingName() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		when(mockClient.post(anyForm(), urlEndsWith("/domains"))).thenReturn(ADD_DOMAIN_JSON.getContentAsString());
		// operation
		domain.createApplication(null, new StandaloneCartridge("jbossas-7"), null, null);
		// verifications
		// expected exception
	}

	@Test(expected = OpenShiftException.class)
	public void shouldNotCreateApplicationWithMissingCartridge() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		when(mockClient.post(anyForm(), urlEndsWith("/domains"))).thenReturn(ADD_DOMAIN_JSON.getContentAsString());
		// operation
		domain.createApplication("foo", null, null, null);
		// verifications
		// expected exception
	}

	@Test
	public void shouldNotRecreateExistingApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		// operation
		try {
			domain.createApplication("sample", new StandaloneCartridge("jbossas-7"), null, null);
			// expect an exception
			fail("Expected exception here...");
		} catch (OpenShiftException e) {
			// OK
		}
		// verifications
		assertThat(domain.getApplications()).hasSize(2);
	}

	@Test
	public void shouldGetApplicationByNameCaseInsensitive() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		// operation
		IApplication lowerCaseQueryResult = domain.getApplicationByName("scalable");
		IApplication upperCaseQueryResult = domain.getApplicationByName("SCALABLE");

		// verifications
		assertThat(lowerCaseQueryResult).isNotNull();
		assertThat(lowerCaseQueryResult.getName()).isEqualTo("scalable");
		assertThat(upperCaseQueryResult).isNotNull();
		assertThat(upperCaseQueryResult.getName()).isEqualTo("scalable");
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
