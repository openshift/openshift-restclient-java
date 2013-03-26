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
import static com.openshift.client.utils.Samples.ADD_APPLICATION_ALIAS_JSON;
import static com.openshift.client.utils.Samples.ADD_APPLICATION_CARTRIDGE_JSON;
import static com.openshift.client.utils.Samples.ADD_APPLICATION_JSON;
import static com.openshift.client.utils.Samples.ADD_DOMAIN_JSON;
import static com.openshift.client.utils.Samples.DELETE_APPLICATION_CARTRIDGE_JSON;
import static com.openshift.client.utils.Samples.GET_APPLICATIONS_WITH1APP_JSON;
import static com.openshift.client.utils.Samples.GET_APPLICATIONS_WITH2APPS_1LOCALHOST_JSON;
import static com.openshift.client.utils.Samples.GET_APPLICATIONS_WITH2APPS_JSON;
import static com.openshift.client.utils.Samples.GET_APPLICATIONS_WITHNOAPP_JSON;
import static com.openshift.client.utils.Samples.GET_APPLICATION_CARTRIDGES_WITH1ELEMENT_JSON;
import static com.openshift.client.utils.Samples.GET_APPLICATION_CARTRIDGES_WITH2ELEMENTS_JSON;
import static com.openshift.client.utils.Samples.GET_APPLICATION_WITH1CARTRIDGE1ALIAS_JSON;
import static com.openshift.client.utils.Samples.GET_APPLICATION_WITH2CARTRIDGES2ALIASES_JSON;
import static com.openshift.client.utils.Samples.GET_DOMAINS_1EXISTING;
import static com.openshift.client.utils.Samples.REMOVE_APPLICATION_ALIAS_JSON;
import static com.openshift.client.utils.Samples.START_APPLICATION_JSON;
import static com.openshift.client.utils.Samples.STOP_APPLICATION_JSON;
import static com.openshift.client.utils.Samples.STOP_FORCE_APPLICATION_JSON;
import static com.openshift.client.utils.UrlEndsWithMatcher.urlEndsWith;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;

import org.fest.assertions.Condition;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.openshift.client.ApplicationScale;
import com.openshift.client.EmbeddableCartridge;
import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IHttpClient;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.InvalidCredentialsOpenShiftException;
import com.openshift.client.OpenShiftConnectionFactory;
import com.openshift.client.OpenShiftEndpointException;
import com.openshift.client.OpenShiftException;
import com.openshift.client.OpenShiftTimeoutException;
import com.openshift.client.cartridge.IEmbeddedCartridge;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.utils.Samples;
import com.openshift.internal.client.httpclient.HttpClientException;
import com.openshift.internal.client.httpclient.InternalServerErrorException;
import com.openshift.internal.client.httpclient.UnauthorizedException;

/**
 * @author Xavier Coulon
 * @author Andre Dietisheim
 */
public class ApplicationResourceTest {

	private IDomain domain;
	private IHttpClient mockClient;

	@Before
	public void setup() throws Throwable {
		mockClient = mock(IHttpClient.class);
		when(mockClient.get(urlEndsWith("/broker/rest/api")))
				.thenReturn(Samples.GET_REST_API_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/user"))).thenReturn(
				Samples.GET_USER_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains"))).thenReturn(GET_DOMAINS_1EXISTING.getContentAsString());
		final IOpenShiftConnection connection =
				new OpenShiftConnectionFactory().getConnection(
						new RestService("http://mock", "clientId", mockClient), "foo@redhat.com", "bar");
		IUser user = connection.getUser();
		this.domain = user.getDomain("foobar");
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
	public void shouldDestroyApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		final IApplication app = domain.getApplicationByName("sample");
		// operation
		app.destroy();
		// verifications
		assertThat(domain.getApplications()).hasSize(1).excludes(app);
	}

	@Test
	public void shouldStopApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		when(mockClient.post(anyForm(), urlEndsWith("/domains/foobar/applications/sample/events"))).thenReturn(
				STOP_APPLICATION_JSON.getContentAsString());
		final IApplication app = domain.getApplicationByName("sample");
		// operation
		app.stop();
		// verifications
		verify(mockClient, times(1)).post(anyForm(), urlEndsWith("/domains/foobar/applications/sample/events"));
	}

	@Test
	public void shouldForceStopApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		when(mockClient.post(anyForm(), urlEndsWith("/domains/foobar/applications/sample/events"))).thenReturn(
				STOP_FORCE_APPLICATION_JSON.getContentAsString());
		final IApplication app = domain.getApplicationByName("sample");
		// operation
		app.stop(true);
		// verifications
		verify(mockClient, times(1)).post(anyForm(), urlEndsWith("/domains/foobar/applications/sample/events"));
	}

	@Test
	public void shouldStartApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		when(mockClient.post(anyForm(), urlEndsWith("/domains/foobar/applications/sample/events"))).thenReturn(
				START_APPLICATION_JSON.getContentAsString());
		final IApplication app = domain.getApplicationByName("sample");
		// operation
		app.start();
		// verifications
		verify(mockClient, times(1)).post(anyForm(), urlEndsWith("/domains/foobar/applications/sample/events"));
	}

	@Test
	public void shouldRestartApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		when(mockClient.post(anyForm(), urlEndsWith("/domains/foobar/applications/sample/events"))).thenReturn(
				STOP_APPLICATION_JSON.getContentAsString());
		final IApplication app = domain.getApplicationByName("sample");
		// operation
		app.restart();
		// verifications
		verify(mockClient, times(1)).post(anyForm(), urlEndsWith("/domains/foobar/applications/sample/events"));

	}

	@Ignore("Need higher quotas on stg")
	@Test
	public void shouldScaleDownApplication() throws Throwable {
	}

	@Test
	public void shouldNotScaleDownApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		when(mockClient.post(anyForm(), urlEndsWith("/domains/foobar/applications/sample/events")))
				.thenThrow(
						new InternalServerErrorException(
								"Failed to add event scale-down to application sample due to: Cannot scale a non-scalable application"));
		final IApplication app = domain.getApplicationByName("sample");
		// operation
		try {
			app.scaleDown();
			fail("Expected an exception here..");
		} catch (OpenShiftEndpointException e) {
			assertThat(e.getCause()).isInstanceOf(InternalServerErrorException.class);
		}
		// verifications
		verify(mockClient, times(1)).post(anyForm(), urlEndsWith("/domains/foobar/applications/sample/events"));
	}

	@Ignore("Need higher quotas on stg")
	@Test
	public void shouldScaleUpApplication() throws Throwable {

	}

	@Test
	public void shouldNotScaleUpApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		when(mockClient.post(anyForm(), urlEndsWith("/domains/foobar/applications/sample/events")))
				.thenThrow(
						new InternalServerErrorException(
								"Failed to add event scale-up to application sample due to: Cannot scale a non-scalable application"));
		final IApplication app = domain.getApplicationByName("sample");
		// operation
		try {
			app.scaleUp();
			fail("Expected an exception here..");
		} catch (OpenShiftEndpointException e) {
			assertThat(e.getCause()).isInstanceOf(InternalServerErrorException.class);
		}
		// verifications
		verify(mockClient, times(1)).post(anyForm(), urlEndsWith("/domains/foobar/applications/sample/events"));
	}

	@Test
	public void shouldAddAliasToApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobar/applications/sample"))).thenReturn(
				GET_APPLICATION_WITH1CARTRIDGE1ALIAS_JSON.getContentAsString());
		when(mockClient.post(anyForm(), urlEndsWith("/domains/foobar/applications/sample/events"))).thenReturn(
				ADD_APPLICATION_ALIAS_JSON.getContentAsString());
		final IApplication app = domain.getApplicationByName("sample");
		assertThat(app.getAliases()).hasSize(1).contains("an_alias");
		// operation
		app.addAlias("another_alias");
		// verifications
		verify(mockClient, times(1)).post(anyForm(), urlEndsWith("/domains/foobar/applications/sample/events"));
		assertThat(app.getAliases()).hasSize(2).contains("an_alias", "another_alias");
	}

	@Test
	public void shouldNotAddExistingAliasToApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobar/applications/sample"))).thenReturn(
				GET_APPLICATION_WITH1CARTRIDGE1ALIAS_JSON.getContentAsString());
		when(mockClient.post(anyForm(), urlEndsWith("/domains/foobar/applications/sample/events")))
				.thenThrow(
						new InternalServerErrorException(
								"Failed to add event add-alias to application sample due to: Alias 'another_alias' already exists for 'sample'"));
		final IApplication app = domain.getApplicationByName("sample");
		assertThat(app.getAliases()).hasSize(1).contains("an_alias");
		// operation
		try {
			app.addAlias("another_alias");
			fail("Expected an exception..");
		} catch (OpenShiftEndpointException e) {
			assertThat(e.getCause()).isInstanceOf(InternalServerErrorException.class);
		}
		// verifications
		assertThat(app.getAliases()).hasSize(1).contains("an_alias");
		verify(mockClient, times(1)).post(anyForm(), urlEndsWith("/domains/foobar/applications/sample/events"));
	}

	@Test
	public void shouldRemoveAliasFromApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobar/applications/sample"))).thenReturn(
				GET_APPLICATION_WITH1CARTRIDGE1ALIAS_JSON.getContentAsString());
		when(mockClient.post(anyForm(), urlEndsWith("/domains/foobar/applications/sample/events"))).thenReturn(
				REMOVE_APPLICATION_ALIAS_JSON.getContentAsString());
		final IApplication app = domain.getApplicationByName("sample");
		assertThat(app.getAliases()).hasSize(1).contains("an_alias");
		// operation
		app.removeAlias("an_alias");
		// verifications
		verify(mockClient, times(1)).post(anyForm(), urlEndsWith("/domains/foobar/applications/sample/events"));
		assertThat(app.getAliases()).hasSize(0);
	}

	@Test
	public void shouldNotRemoveAliasFromApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobar/applications/sample"))).thenReturn(
				GET_APPLICATION_WITH1CARTRIDGE1ALIAS_JSON.getContentAsString());
		when(mockClient.post(anyForm(), urlEndsWith("/domains/foobar/applications/sample/events")))
				.thenThrow(
						new InternalServerErrorException(
								"Failed to add event remove-alias to application sample due to: Alias 'an_alias' does not exist for 'sample'"));
		final IApplication app = domain.getApplicationByName("sample");
		assertThat(app.getAliases()).hasSize(1).contains("an_alias");
		// operation
		try {
			app.removeAlias("another_alias");
			fail("Expected an exception..");
		} catch (OpenShiftEndpointException e) {
			assertThat(e.getCause()).isInstanceOf(InternalServerErrorException.class);
		}
		// verifications
		assertThat(app.getAliases()).hasSize(1).contains("an_alias");
		verify(mockClient, times(1)).post(anyForm(), urlEndsWith("/domains/foobar/applications/sample/events"));
	}

	@Test
	public void shouldListExistingCartridges() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobar/applications/sample"))).thenReturn(
				GET_APPLICATION_WITH2CARTRIDGES2ALIASES_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobar/applications/sample/cartridges"))).thenReturn(
				GET_APPLICATION_CARTRIDGES_WITH2ELEMENTS_JSON.getContentAsString());
		final IApplication app = domain.getApplicationByName("sample");
		// operation
		final List<IEmbeddedCartridge> embeddedCartridges = app.getEmbeddedCartridges();
		// verifications
		assertThat(embeddedCartridges).hasSize(2);
	}

	@Test
	public void shouldReloadExistingEmbeddedCartridges() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobar/applications/sample"))).thenReturn(
				GET_APPLICATION_WITH1CARTRIDGE1ALIAS_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobar/applications/sample/cartridges"))).thenReturn(
				GET_APPLICATION_CARTRIDGES_WITH1ELEMENT_JSON.getContentAsString());
		final IApplication app = domain.getApplicationByName("sample");
		assertThat(app.getEmbeddedCartridges()).hasSize(1);
		// simulate new content on openshift, that should be grabbed while doing
		// a refresh()
		when(mockClient.get(urlEndsWith("/domains/foobar/applications/sample/cartridges"))).thenReturn(
				GET_APPLICATION_CARTRIDGES_WITH2ELEMENTS_JSON.getContentAsString());
		// operation
		app.refresh();
		assertThat(app.getEmbeddedCartridges()).hasSize(2);
		verify(mockClient, times(2)).get(urlEndsWith("/domains/foobar/applications/sample/cartridges"));
	}

	@Test
	public void shouldListAvailableCartridges() throws Throwable {
		// pre-conditions
		// operation
		final List<String> availableCartridges = domain.getAvailableCartridgeNames();
		// verifications
		assertThat(availableCartridges).containsExactly(
				"nodejs-0.6", 
				"jbossas-7", 
				"python-2.6", 
				"jenkins-1.4",
				"ruby-1.8", 
				"diy-0.1", 
				"php-5.3", 
				"perl-5.10");
	}

	@Test
	public void shouldAddCartridgeToApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobar/applications/sample"))).thenReturn(
				GET_APPLICATION_WITH1CARTRIDGE1ALIAS_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobar/applications/sample/cartridges"))).thenReturn(
				GET_APPLICATION_CARTRIDGES_WITH1ELEMENT_JSON.getContentAsString());
		when(mockClient.post(anyForm(), urlEndsWith("/domains/foobar/applications/sample/cartridges"))).thenReturn(
				ADD_APPLICATION_CARTRIDGE_JSON.getContentAsString());
		final IApplication app = domain.getApplicationByName("sample");
		// operation
		app.addEmbeddableCartridge(new EmbeddableCartridge("mysql-5.1"));
		
		// verifications
		assertThat(app.getEmbeddedCartridge("mysql-5.1")).satisfies(new Condition<Object>() {
			@Override
			public boolean matches(Object value) {
				final EmbeddedCartridgeResource cartridge = (EmbeddedCartridgeResource) value;
				return cartridge != null && cartridge.getName() != null
						&& !LinkRetriever.retrieveLinks(cartridge).isEmpty();
			}
		});
		verify(mockClient, times(1)).post(anyForm(), urlEndsWith("/domains/foobar/applications/sample/cartridges"));
		assertThat(app.getEmbeddedCartridges()).hasSize(2);
	}

	@Test
	public void shouldNotAddCartridgeToApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobar/applications/sample"))).thenReturn(
				GET_APPLICATION_WITH1CARTRIDGE1ALIAS_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobar/applications/sample/cartridges"))).thenReturn(
				GET_APPLICATION_CARTRIDGES_WITH1ELEMENT_JSON.getContentAsString());
		when(mockClient.post(anyForm(), urlEndsWith("/domains/foobar/applications/sample/cartridges"))).thenThrow(
				new SocketTimeoutException("mock..."));
		final IApplication app = domain.getApplicationByName("sample");
		assertThat(app.getEmbeddedCartridges()).hasSize(1);
		// operation
		try {
			app.addEmbeddableCartridge(new EmbeddableCartridge("mysql-5.1"));
			fail("Expected an exception here...");
		} catch (OpenShiftTimeoutException e) {
			// ok
		}
		// verifications
		verify(mockClient, times(1)).post(anyForm(), urlEndsWith("/domains/foobar/applications/sample/cartridges"));
		assertThat(app.getEmbeddedCartridge("mysql-5.1")).isNull();
		assertThat(app.getEmbeddedCartridges()).hasSize(1);
	}

	@Test
	public void shouldRemoveCartridgeFromApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobar/applications/sample"))).thenReturn(
				GET_APPLICATION_WITH1CARTRIDGE1ALIAS_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobar/applications/sample/cartridges"))).thenReturn(
				GET_APPLICATION_CARTRIDGES_WITH2ELEMENTS_JSON.getContentAsString());
		when(mockClient.delete(anyForm(), urlEndsWith("/domains/foobar/applications/sample/cartridges/mysql-5.1")))
				.thenReturn(
						DELETE_APPLICATION_CARTRIDGE_JSON.getContentAsString());
		final IApplication application = domain.getApplicationByName("sample");
		assertThat(application.getEmbeddedCartridges()).hasSize(2);
		// operation
		application.getEmbeddedCartridge("mysql-5.1").destroy();
		// verifications
		verify(mockClient, times(1)).delete(anyForm(),
				urlEndsWith("/domains/foobar/applications/sample/cartridges/mysql-5.1"));
		assertThat(application.getEmbeddedCartridge("mysql-5.1")).isNull();
		assertThat(application.getEmbeddedCartridges()).hasSize(1);
	}

	@Test
	public void shouldNotRemoveCartridgeFromApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications"))).thenReturn(
				GET_APPLICATIONS_WITH2APPS_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobar/applications/sample"))).thenReturn(
				GET_APPLICATION_WITH1CARTRIDGE1ALIAS_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobar/applications/sample/cartridges"))).thenReturn(
				GET_APPLICATION_CARTRIDGES_WITH2ELEMENTS_JSON.getContentAsString());
		when(mockClient.delete(anyForm(), urlEndsWith("/domains/foobar/applications/sample/cartridges/mysql-5.1")))
				.thenThrow(new SocketTimeoutException("mock..."));
		final IApplication application = domain.getApplicationByName("sample");
		assertThat(application.getEmbeddedCartridges()).hasSize(2);
		// operation
		final IEmbeddedCartridge embeddedCartridge = application.getEmbeddedCartridge("mysql-5.1");
		try {
			embeddedCartridge.destroy();
			fail("Expected an exception here..");
		} catch (OpenShiftTimeoutException e) {
			// ok
		}
		// verifications
		verify(mockClient, times(1)).delete(anyForm(),
				urlEndsWith("/domains/foobar/applications/sample/cartridges/mysql-5.1"));
		assertThat(embeddedCartridge).isNotNull();
		assertThat(application.getEmbeddedCartridges()).hasSize(2).contains(embeddedCartridge);
	}

	@Test
	@Ignore
	public void shouldNotLoadApplicationTwice() throws Throwable {
		fail("not implemented yet");
	}

	@Test
	public void shouldWaitUntilTimeout() throws HttpClientException, Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications")))
				.thenReturn(GET_APPLICATIONS_WITH2APPS_1LOCALHOST_JSON.getContentAsString());
		final IApplication app = domain.getApplicationByName("scalable");
		long timeout = 4 * 1024;
		long startTime = System.currentTimeMillis();

		// operation
		boolean successfull = app.waitForAccessible(timeout);

		assertFalse(successfull);
		assertTrue(System.currentTimeMillis() >= (startTime + timeout));
	}

	@Test
	public void shouldEndBeforeTimeout() throws HttpClientException, Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobar/applications")))
				.thenReturn(GET_APPLICATIONS_WITH2APPS_1LOCALHOST_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/health")))
				.thenReturn("1"); // return health ok
		long startTime = System.currentTimeMillis();
		long timeout = 10 * 1024;
		final IApplication app = domain.getApplicationByName("sample");

		// operation
		boolean successfull = app.waitForAccessible(timeout);

		assertTrue(successfull);
		assertTrue(System.currentTimeMillis() < (startTime + timeout));
	}

}
