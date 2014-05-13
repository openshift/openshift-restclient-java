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
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_1EMBEDDED;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_1EMBEDDED;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_1EMBEDDED;
import static org.fest.assertions.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.openshift.client.IApplication;
import com.openshift.client.IApplicationPortForwarding;
import com.openshift.client.IApplicationSSHSession;
import com.openshift.client.IDomain;

/**
 * @author Corey Daley
 */
public class ApplicationSSHSessionTest extends TestTimer {

	private IDomain domain;
	private HttpClientMockDirector mockDirector;

	@Before
	public void setup() throws Throwable {
		this.mockDirector = new HttpClientMockDirector()
				.mockGetDomains(GET_DOMAINS)
				.mockGetApplications("foobarz", GET_DOMAINS_FOOBARZ_APPLICATIONS_1EMBEDDED)
				.mockGetApplication("foobarz", "springeap6", GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_1EMBEDDED)
				.mockGetApplicationCartridges("foobarz", "springeap6",
						GET_DOMAINS_FOOBARZ_APPLICATIONS_SPRINGEAP6_CARTRIDGES_1EMBEDDED);
		this.domain = mockDirector.getDomain("foobarz");
		assertThat(domain).isNotNull();
	}

	@Test
	public void shouldGetForwardablePorts() throws Throwable {
		// pre-conditions
		final IApplication app = domain.getApplicationByName("springeap6");
		assertThat(app).isNotNull().isInstanceOf(ApplicationResource.class);
		String rhcListPortsOutput =
				"haproxy -> 127.7.233.2:8080\n"
						+ " haproxy -> 127.7.233.3:8080\n"
						+ " java -> 127.7.233.1:3528\n"
						+ " java -> 127.7.233.1:4447\n"
						+ " java -> 127.7.233.1:5445\n"
						+ " java -> 127.7.233.1:5455\n"
						+ " java -> 127.7.233.1:8080\n"
						+ " java -> 127.7.233.1:9990\n"
						+ " java -> 127.7.233.1:9999\n"
						+ " mysql -> 5190d701500446506a0000e4-foobarz.rhcloud.com:56756";
		ApplicationResource spy = Mockito.spy(((ApplicationResource) app));
		final IApplicationSSHSession session =
				new ApplicationSSHSession(spy, new JSch().getSession("mockuser", "mockhost", 22));
		ApplicationSSHSession spyedSession = Mockito.spy(((ApplicationSSHSession) session));
		Mockito.doReturn(new ByteArrayInputStream(rhcListPortsOutput.getBytes()))
				.when(spyedSession)
				.execCommand(Mockito.anyString(), (ApplicationSSHSession.ChannelInputStreams) Mockito.any(),
						(Session) Mockito.any());
		Mockito.doReturn(true)
				.when(spyedSession)
				.isConnected();

		// operation
		List<IApplicationPortForwarding> forwardablePorts = spyedSession.getForwardablePorts();

		// verification
		assertThat(forwardablePorts).isNotEmpty().hasSize(10);
		assertThat(forwardablePorts)
				.onProperty("name")
				.containsExactly("haproxy", "haproxy", "java", "java", "java", "java", "java", "java", "java", "mysql");
		assertThat(forwardablePorts)
				.onProperty("remoteAddress").containsExactly(
						"127.7.233.2", "127.7.233.3", "127.7.233.1",
						"127.7.233.1", "127.7.233.1", "127.7.233.1", "127.7.233.1", "127.7.233.1", "127.7.233.1",
						"5190d701500446506a0000e4-foobarz.rhcloud.com");
		assertThat(forwardablePorts)
				.onProperty("remotePort").containsExactly(8080, 8080, 3528, 4447, 5445, 5455, 8080, 9990, 9999, 56756);
	}

	@Test
	public void shouldRefreshForwardablePorts() throws Throwable {
		// pre-conditions
		final IApplication app = domain.getApplicationByName("springeap6");
		assertThat(app).isNotNull().isInstanceOf(ApplicationResource.class);
		String rhcListPortsOutput =
				"haproxy -> 127.7.233.2:8080\n"
						+ " haproxy -> 127.7.233.3:8080\n"
						+ " java -> 127.7.233.1:3528\n"
						+ " java -> 127.7.233.1:4447\n"
						+ " java -> 127.7.233.1:5445\n"
						+ " java -> 127.7.233.1:5455\n"
						+ " java -> 127.7.233.1:8080\n"
						+ " java -> 127.7.233.1:9990\n"
						+ " java -> 127.7.233.1:9999\n"
						+ " mysql -> 5190d701500446506a0000e4-foobarz.rhcloud.com:56756";
		ApplicationSSHSessionMockDirector mockDirector =
				new ApplicationSSHSessionMockDirector(app).mockGetForwardablePorts(rhcListPortsOutput);

		// operation
		ApplicationSSHSession applicationSession = mockDirector.getMock();
		List<IApplicationPortForwarding> forwardablePorts = applicationSession.getForwardablePorts();

		// verification
		assertThat(forwardablePorts).isNotEmpty().hasSize(10);
		assertThat(forwardablePorts)
				.onProperty("name").containsExactly(
						"haproxy", "haproxy", "java", "java", "java", "java", "java", "java", "java", "mysql");
		assertThat(forwardablePorts)
				.onProperty("remoteAddress").containsExactly(
						"127.7.233.2", "127.7.233.3", "127.7.233.1",
						"127.7.233.1", "127.7.233.1", "127.7.233.1", "127.7.233.1", "127.7.233.1", "127.7.233.1",
						"5190d701500446506a0000e4-foobarz.rhcloud.com");
		assertThat(forwardablePorts)
				.onProperty("remotePort")
				.containsExactly(8080, 8080, 3528, 4447, 5445, 5455, 8080, 9990, 9999, 56756);

		// pre-conditions
		String rhcListPortsOutputNew =
				"haproxy -> 127.7.233.2:8080\n" +
						" haproxy -> 127.7.233.3:8080\n" +
						" java -> 127.7.233.1:3528\n" +
						" java -> 127.7.233.1:4447\n" +
						" java -> 127.7.233.1:5445\n" +
						" mysql -> 5190d701500446506a0000e4-foobarz.rhcloud.com:56756";

		mockDirector.mockGetForwardablePorts(rhcListPortsOutputNew);

		// operation
		List<IApplicationPortForwarding> refreshedForwardablePorts = applicationSession.refreshForwardablePorts();

		// verification
		assertThat(refreshedForwardablePorts).isNotEmpty().hasSize(6);
		assertThat(refreshedForwardablePorts)
				.onProperty("name").containsExactly("haproxy", "haproxy", "java", "java", "java", "mysql");
		assertThat(refreshedForwardablePorts)
				.onProperty("remoteAddress")
				.containsExactly(
						"127.7.233.2", "127.7.233.3", "127.7.233.1", "127.7.233.1", "127.7.233.1",
						"5190d701500446506a0000e4-foobarz.rhcloud.com");
		assertThat(refreshedForwardablePorts)
				.onProperty("remotePort").containsExactly(8080, 8080, 3528, 4447, 5445, 56756);

	}

	@Test
	public void shouldGetEnvironmentProperties() throws Throwable {
		// pre-conditions
		final IApplication app = domain.getApplicationByName("springeap6");
		assertThat(app).isNotNull().isInstanceOf(ApplicationResource.class);
		String environmentProperties =
				"OPENSHIFT_TMP_DIR=/tmp/\n" +
						"HOSTNAME=ex-std-node360.prod.rhcloud.com\n" +
						"BASH=/bin/bash\n" +
						"OPENSHIFT_BROKER_HOST=openshift.redhat.com\n" +
						"OPENSHIFT_APP_NAME=springeap6";
		ApplicationSSHSession applicationSession =
				new ApplicationSSHSessionMockDirector(app).mockGetEnvironmentProperties(environmentProperties)
						.getMock();

		// operation
		List<String> environmentProperties2 = applicationSession.getEnvironmentProperties();

		// verification
		assertThat(environmentProperties2).isNotEmpty().hasSize(5);
		assertThat(environmentProperties2)
				.containsExactly(
						"OPENSHIFT_TMP_DIR=/tmp/",
						"HOSTNAME=ex-std-node360.prod.rhcloud.com",
						"BASH=/bin/bash",
						"OPENSHIFT_BROKER_HOST=openshift.redhat.com",
						"OPENSHIFT_APP_NAME=springeap6");
	}
}
