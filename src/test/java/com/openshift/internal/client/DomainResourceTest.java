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

import static com.openshift.client.utils.Samples.DELETE_DOMAINS_FOOBARZ;
import static com.openshift.client.utils.Samples.GET_DOMAINS;
import static com.openshift.client.utils.Samples.GET_DOMAINS_EMPTY;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARS;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_NOAPPS;
import static com.openshift.client.utils.Samples.POST_SCALABLE_DOMAINS_FOOBARZ_APPLICATIONS;
import static com.openshift.client.utils.UrlEndsWithMatcher.urlEndsWith;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;

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
import com.openshift.client.utils.Samples;
import com.openshift.internal.client.httpclient.BadRequestException;
import com.openshift.internal.client.httpclient.HttpClientException;
import com.openshift.internal.client.httpclient.UnauthorizedException;
import com.openshift.internal.client.utils.IOpenShiftJsonConstants;

/**
 * @author Xavier Coulon
 * @author Andre Dietisheim
 */
public class DomainResourceTest {

	private static final StandaloneCartridge CARTRIDGE_JBOSSAS_7 = new StandaloneCartridge("jbossas-7");
	private IUser user;
	private IDomain domain;
	private IHttpClient mockClient;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();

	@Before
	public void setup() throws Throwable {
		this.mockClient = createMockClient(GET_DOMAINS);
		this.user = createUser(mockClient);
		this.domain = user.getDomain("foobarz");
	}

	private IHttpClient createMockClient(Samples domainsResponse) throws SocketTimeoutException, Throwable {
		IHttpClient mockClient = mock(IHttpClient.class);
		when(mockClient.get(urlEndsWith("/broker/rest/api")))
				.thenReturn(Samples.GET_API.getContentAsString());
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
		IHttpClient client = createMockClient(GET_DOMAINS_EMPTY);
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
				GET_DOMAINS_FOOBARS.getContentAsString());
		int numOfDomains = user.getDomains().size();
		// operation
		final IDomain domain = user.createDomain("foobars");
		// verifications
		assertThat(user.getDomains().size()).isSameAs(numOfDomains + 1);
		assertThat(domain.getId()).isEqualTo("foobars");
		assertThat(domain.getSuffix()).isEqualTo("rhcloud.com");
	}

	@Test(expected = OpenShiftException.class)
	public void shouldNotRecreateExistingDomain() throws Throwable {
		// pre-conditions
		// operation
		user.createDomain("foobarz");
		// verifications
		// expect an exception
	}

	@Test
	public void shouldDestroyDomain() throws Throwable {
		// pre-conditions
		when(mockClient.delete(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobar")))
			.thenReturn(DELETE_DOMAINS_FOOBARZ.getContentAsString());
		// operation
		final IDomain domain = user.getDomain("foobarz");
		domain.destroy();
		// verifications
		assertThat(user.getDomain("foobarz")).isNull();
		assertThat(user.getDomains()).isEmpty();
	}

	@Test
	public void shouldNotDestroyDomainWithApp() throws Throwable {
		// pre-conditions
		when(mockClient.delete(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz")))
			.thenThrow(new BadRequestException(
					"Domain contains applications. Delete applications first or set force to true.", null));
		// operation
		final IDomain domain = user.getDomain("foobarz");
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
		when(mockClient.put(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz")))
			.thenReturn(GET_DOMAINS_FOOBARS.getContentAsString());
		final IDomain domain = user.getDomain("foobarz");
		// operation
		domain.rename("foobars");
		// verifications
		assertThat(domain.getId()).isEqualTo("foobars");
		final IDomain updatedDomain = user.getDomain("foobars");
		assertThat(updatedDomain).isNotNull();
		assertThat(updatedDomain.getId()).isEqualTo("foobars");
		assertThat(LinkRetriever.retrieveLink(updatedDomain, "UPDATE").getHref()).contains("/foobars");
		verify(mockClient, times(1)).put(anyMapOf(String.class, Object.class), any(URL.class));
	}

	@Test
	public void shouldListAvailableGearSizes() throws Throwable {
		// pre-conditions
		final IDomain domain = user.getDomain("foobarz");
		// operation
		List<IGearProfile> availableGearSizes = domain.getAvailableGearProfiles();
		// verifications
		assertThat(availableGearSizes).onProperty("name")
				.contains("small", "micro", "medium", "large", "exlarge", "jumbo");
	}

	@Test
	public void shouldRefreshDomainAndReloadApplications() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz")))
				.thenReturn(GET_DOMAINS_FOOBARZ.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		final IDomain domain = user.getDomain("foobarz");
		assertThat(domain).isNotNull();
		domain.getApplications();
		// operation
		domain.refresh();
		// verifications
		verify(mockClient, times(1)).get(urlEndsWith("/domains/foobarz")); // explicit refresh on this location
		verify(mockClient, times(2)).get(urlEndsWith("/domains/foobarz/applications")); // two calls, before and while refresh
	}

	@Test
	public void shouldRefreshDomainAndNotReloadApplications() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz")))
				.thenReturn(GET_DOMAINS_FOOBARZ.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		final IDomain domain = user.getDomain("foobarz");
		assertThat(domain).isNotNull();
		// operation
		domain.refresh();
		// verifications
		verify(mockClient, times(1)).get(urlEndsWith("/domains")); // explicit refresh on this location
		verify(mockClient, times(0)).get(urlEndsWith("/domains/foobarz/applications")); // no call, neither before and while refresh
	}

	@Test
	public void shouldLoadListOfApplicationsWithNoElement() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS_NOAPPS.getContentAsString());
		// operation
		final List<IApplication> apps = domain.getApplications();
		// verifications
		assertThat(apps).isEmpty();
		// 4 calls: /API + /API/user + /API/domains +
		// /API/domains/foobar/applications
		verify(mockClient, times(4)).get(any(URL.class));
	}

	@Test
	public void shouldLoadListOfApplicationsWith2Elements() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		// operation
		final List<IApplication> apps = domain.getApplications();
		// verifications
		assertThat(apps).hasSize(2);
		// 4 calls: /API + /API/user + /API/domains +
		// /API/domains/foobarz/applications
		verify(mockClient, times(4)).get(any(URL.class));
	}

	@Test
	public void shouldNotLoadApplicationTwice() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		// operation
		List<IApplication> apps = domain.getApplications();
		assertThat(apps).hasSize(2);

		// verifications
		reset(mockClient);
		apps = domain.getApplications(); // dont do new client request
		verify(mockClient, times(0)).get(any(URL.class));

	}
	
	@Test(expected = InvalidCredentialsOpenShiftException.class)
	public void shouldNotLoadListOfApplicationsWithInvalidCredentials() 
			throws OpenShiftException, HttpClientException, SocketTimeoutException {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenThrow(new UnauthorizedException("invalid credentials (mock)", null));
		// operation
		domain.getApplications();
		// verifications
		// expect an exception
	}

	@Test
	public void shouldCreateApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
			.thenReturn(
				GET_DOMAINS_FOOBARZ_APPLICATIONS_NOAPPS.getContentAsString());
		when(mockClient.post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications"))).thenReturn(
				POST_SCALABLE_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		// operation
		final IApplication app = domain.createApplication("sample", CARTRIDGE_JBOSSAS_7, ApplicationScale.NO_SCALE, null);
		// verifications
		assertThat(app.getName()).isEqualTo("scalable");
		assertThat(app.getGearProfile().getName()).isEqualTo("small");
		assertThat(app.getApplicationScale()).isEqualTo(ApplicationScale.NO_SCALE);
		assertThat(app.getApplicationUrl()).isEqualTo("http://scalable-foobarz.rhcloud.com/");
		assertThat(app.getCreationTime()).isNotNull();
		assertThat(app.getGitUrl()).isNotNull().startsWith("ssh://")
				.endsWith("@scalable-foobarz.rhcloud.com/~/git/scalable.git/");
		assertThat(app.getInitialGitUrl()).isNotNull().isEqualTo("git://github.com/openshift/openshift-java-client.git");
		assertThat(app.getCartridge()).isEqualTo(CARTRIDGE_JBOSSAS_7);
		assertThat(app.getUUID()).isNotNull();
		assertThat(app.getDomain()).isEqualTo(domain);
		assertThat(LinkRetriever.retrieveLinks(app)).hasSize(18);
		assertThat(domain.getApplications()).hasSize(1).contains(app);
	}

	@Test
	public void shouldRequestCreateApplicationWithNameAndCartridgeOnly() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS_NOAPPS.getContentAsString());
		when(mockClient.post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(POST_SCALABLE_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		// operation
		domain.createApplication("foo", CARTRIDGE_JBOSSAS_7);
		
		// verification
		assertPostParameters(mockClient, 
				new Pair(IOpenShiftJsonConstants.PROPERTY_NAME, "foo"),
				new Pair(IOpenShiftJsonConstants.PROPERTY_CARTRIDGE, CARTRIDGE_JBOSSAS_7.getName()));
	}

	@Test
	public void shouldRequestCreateApplicationWithNameCartridgeAndScaleOnly() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS_NOAPPS.getContentAsString());
		when(mockClient.post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(POST_SCALABLE_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		// operation
		domain.createApplication("foo", CARTRIDGE_JBOSSAS_7, ApplicationScale.SCALE);
		
		// verification
		assertPostParameters(mockClient, 
				new Pair(IOpenShiftJsonConstants.PROPERTY_NAME, "foo"),
				new Pair(IOpenShiftJsonConstants.PROPERTY_CARTRIDGE, CARTRIDGE_JBOSSAS_7.getName()),
				new Pair(IOpenShiftJsonConstants.PROPERTY_SCALE, ApplicationScale.SCALE.getValue()));
	}

	@Test
	public void shouldRequestCreateApplicationWithNameCartridgeScaleGearProfileOnly() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS_NOAPPS.getContentAsString());
		when(mockClient.post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(POST_SCALABLE_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		// operation
		domain.createApplication("foo", CARTRIDGE_JBOSSAS_7, ApplicationScale.SCALE, GearProfile.JUMBO);
		
		// verification
		assertPostParameters(mockClient, 
				new Pair(IOpenShiftJsonConstants.PROPERTY_NAME, "foo"),
				new Pair(IOpenShiftJsonConstants.PROPERTY_CARTRIDGE, CARTRIDGE_JBOSSAS_7.getName()),
				new Pair(IOpenShiftJsonConstants.PROPERTY_SCALE, ApplicationScale.SCALE.getValue()),
				new Pair(IOpenShiftJsonConstants.PROPERTY_GEAR_PROFILE, GearProfile.JUMBO.getName())
		);
	}

	@Test
	public void shouldRequestCreateApplicationWithNameCartridgeScaleGearProfileAndGitUrl() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS_NOAPPS.getContentAsString());
		when(mockClient.post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(POST_SCALABLE_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		// operation
		domain.createApplication("foo", CARTRIDGE_JBOSSAS_7, ApplicationScale.SCALE, GearProfile.JUMBO, "git://github.com/adietish/openshift-java-client.git");
		
		// verification
		assertPostParameters(mockClient, 
				new Pair(IOpenShiftJsonConstants.PROPERTY_NAME, "foo"),
				new Pair(IOpenShiftJsonConstants.PROPERTY_CARTRIDGE, CARTRIDGE_JBOSSAS_7.getName()),
				new Pair(IOpenShiftJsonConstants.PROPERTY_SCALE, ApplicationScale.SCALE.getValue()),
				new Pair(IOpenShiftJsonConstants.PROPERTY_GEAR_PROFILE, GearProfile.JUMBO.getName()),
				new Pair(IOpenShiftJsonConstants.PROPERTY_INITIAL_GIT_URL, "git://github.com/adietish/openshift-java-client.git")
		);
	}

	private static class Pair {
		private String key;
		private String value;

		private Pair(String key, String value) {
			this.key = key;
			this.value = value;
		}
		
		public String getKey() {
			return key;
		}
		
		public String getValue() {
			return value;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void assertPostParameters(IHttpClient client, Pair... pairs) throws SocketTimeoutException, HttpClientException, UnsupportedEncodingException {
		ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
		verify(mockClient).post(captor.capture(), any(URL.class));
		Map postedParameters = captor.getValue();
		assertThat(postedParameters).hasSize(pairs.length);
		for(Pair pair: pairs) {
			assertThat(postedParameters.get(pair.getKey())).isEqualTo(pair.getValue());
		}
	}
	
	@Test(expected = OpenShiftException.class)
	public void shouldNotCreateApplicationWithMissingName() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		// operation
		domain.createApplication(null, CARTRIDGE_JBOSSAS_7, null, null);
		// verifications
		// expected exception
	}

	@Test(expected = OpenShiftException.class)
	public void shouldNotCreateApplicationWithMissingCartridge() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		// operation
		domain.createApplication("foo", null, null, null);
		// verifications
		// expected exception
	}

	@Test
	public void shouldNotRecreateExistingApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		// operation
		try {
			domain.createApplication("springeap6", CARTRIDGE_JBOSSAS_7, null, null);
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
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications"))).thenReturn(
				GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		// operation
		IApplication lowerCaseQueryResult = domain.getApplicationByName("springeap6");
		IApplication upperCaseQueryResult = domain.getApplicationByName("SPRINGEAP6");

		// verifications
		assertThat(lowerCaseQueryResult).isNotNull();
		assertThat(lowerCaseQueryResult.getName()).isEqualTo("springeap6");
		assertThat(upperCaseQueryResult).isNotNull();
		assertThat(upperCaseQueryResult.getName()).isEqualTo("springeap6");
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
