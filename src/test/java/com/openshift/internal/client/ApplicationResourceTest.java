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

import static com.openshift.client.utils.Samples.GET_DOMAINS;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_2ALIAS;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_1EMBEDDED;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_2EMBEDDED;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATONS_SPRINGEAP6_0ALIAS;
import static com.openshift.client.utils.Samples.POST_MYSQL_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES;
import static com.openshift.client.utils.Samples.POST_STOP_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_EVENT;
import static com.openshift.client.utils.UrlEndsWithMatcher.urlEndsWith;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.openshift.client.IApplication;
import com.openshift.client.IApplicationPortForwarding;
import com.openshift.client.IDomain;
import com.openshift.client.IHttpClient;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.Message;
import com.openshift.client.OpenShiftConnectionFactory;
import com.openshift.client.OpenShiftEndpointException;
import com.openshift.client.OpenShiftTimeoutException;
import com.openshift.client.cartridge.EmbeddableCartridge;
import com.openshift.client.cartridge.IEmbeddedCartridge;
import com.openshift.client.utils.EmbeddedCartridgeAssert;
import com.openshift.client.utils.MessageAssert;
import com.openshift.client.utils.Samples;
import com.openshift.internal.client.httpclient.HttpClientException;
import com.openshift.internal.client.httpclient.InternalServerErrorException;

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
				.thenReturn(Samples.GET_API.getContentAsString());
		when(mockClient.get(urlEndsWith("/user"))).thenReturn(
				Samples.GET_USER_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains")))
				.thenReturn(GET_DOMAINS.getContentAsString());
		final IOpenShiftConnection connection =
				new OpenShiftConnectionFactory().getConnection(
						new RestService("http://mock", "clientId", mockClient), "foo@redhat.com", "bar");
		IUser user = connection.getUser();
		this.domain = user.getDomain("foobarz");
	}

	@Test
	public void shouldDestroyApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		assertThat(domain).isNotNull();
		final IApplication app = domain.getApplicationByName("springeap6");
		assertThat(app).isNotNull();
		// operation
		app.destroy();
		// verifications
		assertThat(domain.getApplications()).hasSize(1).excludes(app);
	}

	@Test
	public void shouldStopApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications"))).thenReturn(
				GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		when(mockClient.post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/honkabons2/applications/springeap6/events"))).thenReturn(
				POST_STOP_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_EVENT.getContentAsString());
		final IApplication app = domain.getApplicationByName("springeap6");
		// operation
		app.stop();
		// verifications
		verify(mockClient, times(1)).post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications/springeap6/events"));
	}

	@Test
	public void shouldForceStopApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications"))).thenReturn(
				GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		when(mockClient.post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications/springeap6/events"))).thenReturn(
				GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6.getContentAsString());
		final IApplication app = domain.getApplicationByName("springeap6");
		// operation
		app.stop(true);
		// verifications
		verify(mockClient, times(1)).post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications/springeap6/events"));
	}

	@Test
	public void shouldStartApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications"))).thenReturn(
				GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		when(mockClient.post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications/springeap6/events"))).thenReturn(
				GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6.getContentAsString());
		final IApplication app = domain.getApplicationByName("springeap6");
		// operation
		app.start();
		// verifications
		verify(mockClient, times(1)).post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications/springeap6/events"));
	}

	@Test
	public void shouldRestartApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications"))).thenReturn(
				GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		when(mockClient.post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications/springeap6/events"))).thenReturn(
				GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6.getContentAsString());
		final IApplication app = domain.getApplicationByName("springeap6");
		// operation
		app.restart();
		// verifications
		verify(mockClient, times(1)).post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications/springeap6/events"));

	}

	@Ignore("Need higher quotas on stg")
	@Test
	public void shouldScaleDownApplication() throws Throwable {
	}

	@Test
	public void shouldNotScaleDownApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications"))).thenReturn(
				GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		when(mockClient.post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications/springeap6/events")))
				.thenThrow(
						new InternalServerErrorException(
								"Failed to add event scale-down to application springeap6 due to: Cannot scale a non-scalable application"));
		final IApplication app = domain.getApplicationByName("springeap6");
		// operation
		try {
			app.scaleDown();
			fail("Expected an exception here..");
		} catch (OpenShiftEndpointException e) {
			assertThat(e.getCause()).isInstanceOf(InternalServerErrorException.class);
		}
		// verifications
		verify(mockClient, times(1)).post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications/springeap6/events"));
	}

	@Ignore("Need higher quotas on stg")
	@Test
	public void shouldScaleUpApplication() throws Throwable {
	}

	@Test
	public void shouldNotScaleUpApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications"))).thenReturn(
				GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		when(mockClient.post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications/springeap6/events")))
				.thenThrow(
						new InternalServerErrorException(
								"Failed to add event scale-up to application springeap6 due to: Cannot scale a non-scalable application"));
		final IApplication app = domain.getApplicationByName("springeap6");
		// operation
		try {
			app.scaleUp();
			fail("Expected an exception here..");
		} catch (OpenShiftEndpointException e) {
			assertThat(e.getCause()).isInstanceOf(InternalServerErrorException.class);
		}
		// verifications
		verify(mockClient, times(1)).post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications/springeap6/events"));
	}

	@Test
	public void shouldAddAliasToApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications/springeap6")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6.getContentAsString());
		when(mockClient.post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications/springeap6/events")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_2ALIAS.getContentAsString());
		final IApplication app = domain.getApplicationByName("springeap6");
		assertThat(app.getAliases()).hasSize(1).contains("jbosstools.org");
		// operation
		app.addAlias("redhat.com");
		// verifications
		verify(mockClient, times(1)).post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications/springeap6/events"));
		assertThat(app.getAliases()).hasSize(2).contains("jbosstools.org", "redhat.com");
	}

	@Test
	public void shouldNotAddExistingAliasToApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications/springeap6")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6.getContentAsString());
		when(mockClient.post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications/springeap6/events")))
				.thenThrow(
						new InternalServerErrorException(
								"Failed to add event add-alias to application springeap6 due to: Alias 'jbosstools.org' already exists for 'springeap6'"));
		final IApplication app = domain.getApplicationByName("springeap6");
		assertThat(app.getAliases()).hasSize(1).contains("jbosstools.org");
		// operation
		try {
			app.addAlias("jbosstools.org");
			fail("Expected an exception..");
		} catch (OpenShiftEndpointException e) {
			assertThat(e.getCause()).isInstanceOf(InternalServerErrorException.class);
		}
		// verifications
		assertThat(app.getAliases()).hasSize(1).contains("jbosstools.org");
		verify(mockClient, times(1)).post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications/springeap6/events"));
	}

	@Test
	public void shouldRemoveAliasFromApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		when(mockClient.post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications/springeap6/events")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATONS_SPRINGEAP6_0ALIAS.getContentAsString());
		final IApplication app = domain.getApplicationByName("springeap6");
		assertThat(app.getAliases()).hasSize(1).contains("jbosstools.org");
		// operation
		app.removeAlias("jbosstools.org");
		// verifications
		verify(mockClient, times(1)).post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications/springeap6/events"));
		assertThat(app.getAliases()).hasSize(0);
	}

	@Test
	public void shouldNotRemoveAliasFromApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications"))).thenReturn(
				GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications/springeap6"))).thenReturn(
				GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6.getContentAsString());
		when(mockClient.post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications/springeap6/events")))
				.thenThrow(
						new InternalServerErrorException(
								"Failed to add event remove-alias to application springeap6 due to: Alias 'openshift-origin.org' does not exist for 'springeap6'"));
		final IApplication app = domain.getApplicationByName("springeap6");
		assertThat(app).isNotNull();
		assertThat(app.getAliases()).hasSize(1).contains("jbosstools.org");
		// operation
		try {
			app.removeAlias("openshift-origin.org");
			fail("Expected an exception..");
		} catch (OpenShiftEndpointException e) {
			assertThat(e.getCause()).isInstanceOf(InternalServerErrorException.class);
		}
		// verifications
		assertThat(app.getAliases()).hasSize(1).contains("jbosstools.org");
		verify(mockClient, times(1)).post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications/springeap6/events"));
	}

	@Test
	public void shouldListExistingCartridges() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications/springeap6")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications/springeap6/cartridges")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_2EMBEDDED.getContentAsString());
		final IApplication app = domain.getApplicationByName("springeap6");
		// operation
		final List<IEmbeddedCartridge> embeddedCartridges = app.getEmbeddedCartridges();
		// verifications
		assertThat(embeddedCartridges).hasSize(2);
	}

	@Test
	public void shouldReloadExistingEmbeddedCartridges() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications/springeap6")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications/springeap6/cartridges")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_1EMBEDDED.getContentAsString());
		final IApplication app = domain.getApplicationByName("springeap6");
		assertThat(app.getEmbeddedCartridges()).hasSize(1);
		// simulate new content on openshift, that should be grabbed while doing
		// a refresh()
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications/springeap6/cartridges")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_2EMBEDDED.getContentAsString());
		// operation
		app.refresh();
		assertThat(app.getEmbeddedCartridges()).hasSize(2);
		verify(mockClient, times(2)).get(urlEndsWith("/domains/foobarz/applications/springeap6/cartridges"));
	}

	@Test
	public void shouldAddCartridgeToApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications/springeap6")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications/springeap6/cartridges")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_1EMBEDDED.getContentAsString());
		when(mockClient.post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications/springeap6/cartridges")))
				.thenReturn(POST_MYSQL_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES.getContentAsString());
		final IApplication app = domain.getApplicationByName("springeap6");
		String mySql51Name = "mysql-5.1";
		
		// operation
		app.addEmbeddableCartridge(new EmbeddableCartridge(mySql51Name));
		
		// verifications
		verify(mockClient, times(1))
				.post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications/springeap6/cartridges"));
		assertThat(app.getEmbeddedCartridges()).hasSize(2);
		IEmbeddedCartridge mySqlCartridge = app.getEmbeddedCartridge(mySql51Name);
		new EmbeddedCartridgeAssert(mySqlCartridge)
				.hasDescription()
				.hasName(mySql51Name);

		new MessageAssert(mySqlCartridge.getMessage(Message.FIELD_DEFAULT))
				.hasExitCode(-1)
				.hasText("Added mysql-5.1 to application springeap6");
		new MessageAssert(mySqlCartridge.getMessage(Message.FIELD_RESULT))
				.hasExitCode(0)
				.hasText(
						"\nMySQL 5.1 database added.  Please make note of these credentials:\n\n"
								+ "       Root User: adminnFC22YQ\n   Root Password: U1IX8AIlrEcl\n   Database Name: springeap6\n\n"
								+ "Connection URL: mysql://$OPENSHIFT_MYSQL_DB_HOST:$OPENSHIFT_MYSQL_DB_PORT/\n\n"
								+ "You can manage your new MySQL database by also embedding phpmyadmin-3.4.\n"
								+ "The phpmyadmin username and password will be the same as the MySQL credentials above.\n");
		new MessageAssert(mySqlCartridge.getMessage(Message.FIELD_APPINFO))
				.hasExitCode(0)
				.hasText("Connection URL: mysql://127.13.125.1:3306/\n");

	}

	@Test
	public void shouldNotAddCartridgeToApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications/springeap6")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications/springeap6/cartridges")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_2EMBEDDED.getContentAsString());
		when(mockClient.post(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications/springeap6/cartridges")))
				.thenThrow(new SocketTimeoutException("mock..."));
		final IApplication app = domain.getApplicationByName("springeap6");
		assertThat(app.getEmbeddedCartridges()).hasSize(2);
		// operation
		try {
			app.addEmbeddableCartridge(new EmbeddableCartridge("postgresql-8.4"));
			fail("Expected an exception here...");
		} catch (OpenShiftTimeoutException e) {
			// ok
		}
		// verifications
		verify(mockClient, times(1)).post(anyMapOf(String.class, Object.class),
				urlEndsWith("/domains/foobarz/applications/springeap6/cartridges"));
		assertThat(app.getEmbeddedCartridge("postgresql-8.4")).isNull();
		assertThat(app.getEmbeddedCartridges()).hasSize(2);
	}

	@Test
	public void shouldRemoveCartridgeFromApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications"))).thenReturn(
				GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications/springeap6"))).thenReturn(
				GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications/springeap6/cartridges"))).thenReturn(
				GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_2EMBEDDED.getContentAsString());
		final IApplication application = domain.getApplicationByName("springeap6");
		assertThat(application.getEmbeddedCartridges()).hasSize(2);
		// operation
		application.getEmbeddedCartridge("mysql-5.1").destroy();
		// verifications
		verify(mockClient, times(1)).delete(anyMapOf(String.class, Object.class),
				urlEndsWith("/domains/foobarz/applications/springeap6/cartridges/mysql-5.1"));
		assertThat(application.getEmbeddedCartridge("mysql-5.1")).isNull();
		assertThat(application.getEmbeddedCartridges()).hasSize(1);
	}

	@Test
	public void shouldNotRemoveCartridgeFromApplication() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications/springeap6")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications/springeap6/cartridges")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_2EMBEDDED.getContentAsString());
		when(mockClient.delete(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz/applications/springeap6/cartridges/mysql-5.1")))
				.thenThrow(new SocketTimeoutException("mock..."));
		final IApplication application = domain.getApplicationByName("springeap6");
		assertThat(application.getEmbeddedCartridges()).hasSize(2);
		// operation
		final IEmbeddedCartridge mysql = application.getEmbeddedCartridge("mysql-5.1");
		try {
			mysql.destroy();
			fail("Expected an exception here..");
		} catch (OpenShiftTimeoutException e) {
			// ok
		}
		// verifications
		verify(mockClient, times(1)).delete(anyMapOf(String.class, Object.class),
				urlEndsWith("/domains/foobarz/applications/springeap6/cartridges/mysql-5.1"));
		assertThat(application.getEmbeddedCartridges()).hasSize(2).contains(mysql);
	}

	@Test
	public void shouldWaitUntilTimeout() throws HttpClientException, Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		final IApplication app = domain.getApplicationByName("springeap6");
		assertThat(app).isNotNull().isInstanceOf(ApplicationResource.class);
		ApplicationResource spy = Mockito.spy(((ApplicationResource) app));
		Mockito.doReturn(false).when(spy).canResolv(Mockito.anyString());
		long timeout = 2 * 1000;
		long startTime = System.currentTimeMillis();

		// operation
		boolean successfull = spy.waitForAccessible(timeout);

		assertFalse(successfull);
		assertTrue(System.currentTimeMillis() >= (startTime + timeout));
	}

	@Test
	public void shouldEndBeforeTimeout() throws HttpClientException, Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		long startTime = System.currentTimeMillis();
		long timeout = 10 * 1000;
		final IApplication app = domain.getApplicationByName("springeap6");
		assertThat(app).isNotNull().isInstanceOf(ApplicationResource.class);
		ApplicationResource spy = Mockito.spy(((ApplicationResource) app));
		Mockito.doReturn(true).when(spy).canResolv(Mockito.anyString());
		
		// operation
		boolean successfull = spy.waitForAccessible(timeout);

		assertTrue(successfull);
		assertTrue(System.currentTimeMillis() < (startTime + timeout));
	}

	@Test
	public void shouldGetForwardablePorts() throws Throwable {
		// pre-conditions
		when(mockClient.get(urlEndsWith("/domains/foobarz/applications")))
				.thenReturn(GET_DOMAINS_FOOBARZ_APPLICATIONS.getContentAsString());
		final IApplication app = domain.getApplicationByName("springeap6");
		assertThat(app).isNotNull().isInstanceOf(ApplicationResource.class);
		String[] rhcListPortsOutput = new String[] { 
				"haproxy -> 127.7.233.2:8080",
				" haproxy -> 127.7.233.3:8080",
				" java -> 127.7.233.1:3528",
				" java -> 127.7.233.1:4447",
				" java -> 127.7.233.1:5445",
				" java -> 127.7.233.1:5455",
				" java -> 127.7.233.1:8080",
				" java -> 127.7.233.1:9990",
				" java -> 127.7.233.1:9999",
				" mysql -> 5190d701500446506a0000e4-foobarz.rhcloud.com:56756" };
		ApplicationResource spy = Mockito.spy(((ApplicationResource) app));
		Mockito.doReturn(Arrays.asList(rhcListPortsOutput)).when(spy).sshExecCmd(Mockito.anyString(), (ApplicationResource.SshStreams) Mockito.any());

		// operation
		List<IApplicationPortForwarding> forwardablePorts = spy.getForwardablePorts();
		
		// verification
		assertThat(forwardablePorts).isNotEmpty().hasSize(10);
		assertThat(forwardablePorts)
			.onProperty("name").containsExactly("haproxy", "haproxy", "java", "java", "java", "java", "java", "java", "java", "mysql");
		assertThat(forwardablePorts)
			.onProperty("remoteAddress").containsExactly("127.7.233.2", "127.7.233.3", "127.7.233.1", "127.7.233.1", "127.7.233.1", "127.7.233.1", "127.7.233.1", "127.7.233.1", "127.7.233.1", "5190d701500446506a0000e4-foobarz.rhcloud.com");
		assertThat(forwardablePorts)
			.onProperty("remotePort").containsExactly(8080, 8080, 3528, 4447, 5445, 5455, 8080, 9990, 9999, 56756);
	}	
}
