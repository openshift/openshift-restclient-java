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

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.jcraft.jsch.JSch;
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
		JSch jsch = new JSch();
		final IApplicationSSHSession ses = new ApplicationSSHSession(spy, jsch.getSession("mockuser", "mockhost", 22));
		ApplicationSSHSession spyses = Mockito.spy(((ApplicationSSHSession) ses));
		Mockito.doReturn(Arrays.asList(rhcListPortsOutput)).when(spyses)
				.sshExecCmd(Mockito.anyString(), (ApplicationSSHSession.SshStreams) Mockito.any());

		// operation
		List<IApplicationPortForwarding> forwardablePorts = spyses.getForwardablePorts();

		// verification
		assertThat(forwardablePorts).isNotEmpty().hasSize(10);
		assertThat(forwardablePorts)
				.onProperty("name").containsExactly("haproxy", "haproxy", "java", "java", "java", "java", "java",
				"java", "java", "mysql");
		assertThat(forwardablePorts)
				.onProperty("remoteAddress").containsExactly("127.7.233.2", "127.7.233.3", "127.7.233.1",
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
		JSch jsch = new JSch();
		final IApplicationSSHSession ses = new ApplicationSSHSession(spy, jsch.getSession("mockuser", "mockhost", 22));
		ApplicationSSHSession spyses = Mockito.spy(((ApplicationSSHSession) ses));
		Mockito.doReturn(Arrays.asList(rhcListPortsOutput)).when(spyses)
				.sshExecCmd(Mockito.anyString(), (ApplicationSSHSession.SshStreams) Mockito.any());

		// operation
		List<IApplicationPortForwarding> forwardablePorts = spyses.getForwardablePorts();

		// verification
		assertThat(forwardablePorts).isNotEmpty().hasSize(10);
		assertThat(forwardablePorts)
				.onProperty("name").containsExactly("haproxy", "haproxy", "java", "java", "java", "java", "java",
				"java", "java", "mysql");
		assertThat(forwardablePorts)
				.onProperty("remoteAddress").containsExactly("127.7.233.2", "127.7.233.3", "127.7.233.1",
				"127.7.233.1", "127.7.233.1", "127.7.233.1", "127.7.233.1", "127.7.233.1", "127.7.233.1",
				"5190d701500446506a0000e4-foobarz.rhcloud.com");
		assertThat(forwardablePorts)
				.onProperty("remotePort").containsExactly(8080, 8080, 3528, 4447, 5445, 5455, 8080, 9990, 9999, 56756);

		String[] rhcListPortsOutputNew = new String[] {
				"haproxy -> 127.7.233.2:8080",
				" haproxy -> 127.7.233.3:8080",
				" java -> 127.7.233.1:3528",
				" java -> 127.7.233.1:4447",
				" java -> 127.7.233.1:5445",
				" mysql -> 5190d701500446506a0000e4-foobarz.rhcloud.com:56756" };

		Mockito.doReturn(Arrays.asList(rhcListPortsOutputNew)).when(spyses)
				.sshExecCmd(Mockito.anyString(), (ApplicationSSHSession.SshStreams) Mockito.any());

		// operation
		List<IApplicationPortForwarding> refreshedForwardablePorts = spyses.refreshForwardablePorts();

		// verification
		assertThat(refreshedForwardablePorts).isNotEmpty().hasSize(6);
		assertThat(refreshedForwardablePorts)
				.onProperty("name").containsExactly("haproxy", "haproxy", "java", "java", "java", "mysql");
		assertThat(refreshedForwardablePorts)
				.onProperty("remoteAddress").containsExactly("127.7.233.2", "127.7.233.3", "127.7.233.1", "127.7.233.1", "127.7.233.1",  "5190d701500446506a0000e4-foobarz.rhcloud.com");
		assertThat(refreshedForwardablePorts)
				.onProperty("remotePort").containsExactly(8080, 8080, 3528, 4447, 5445, 56756);


	}

	@Test
	public void shouldGetEnvironmentProperties() throws Throwable {
		// pre-conditions
		final IApplication app = domain.getApplicationByName("springeap6");
		assertThat(app).isNotNull().isInstanceOf(ApplicationResource.class);
		String[] environmentProperties = {
				"OPENSHIFT_TMP_DIR=/tmp/",
				"HOSTNAME=ex-std-node360.prod.rhcloud.com",
				"BASH=/bin/bash",
				"OPENSHIFT_BROKER_HOST=openshift.redhat.com",
				"OPENSHIFT_APP_NAME=springeap6"
		};
		ApplicationResource spy = Mockito.spy(((ApplicationResource) app));
		JSch jsch = new JSch();
		final IApplicationSSHSession ses = new ApplicationSSHSession(spy, jsch.getSession("mockuser", "mockhost", 22));
		ApplicationSSHSession spyses = Mockito.spy(((ApplicationSSHSession) ses));
		Mockito.doReturn(Arrays.asList(environmentProperties)).when(spyses)
				.sshExecCmd(Mockito.anyString(), (ApplicationSSHSession.SshStreams) Mockito.any());

		// operation
		List<String> environmentProperties2 = spyses.getEnvironmentProperties();

		// verification
		assertThat(environmentProperties2).isNotEmpty().hasSize(5);
		assertThat(Arrays.asList(environmentProperties))
				.containsExactly(environmentProperties2.toArray());
	}
}
