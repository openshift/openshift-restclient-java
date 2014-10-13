/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client;

import static com.openshift.client.utils.Samples.DELETE_DOMAINS_FOOBARZ_KO_EXISTINGAPPS;
import static com.openshift.client.utils.Samples.GET_DOMAINS;
import static com.openshift.client.utils.Samples.GET_DOMAINS_FOOBARZ_APPLICATIONS_1EMBEDDED;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import com.openshift.client.IDomain;
import com.openshift.client.IHttpClient;
import com.openshift.client.IUser;
import com.openshift.client.Message;
import com.openshift.client.Messages;
import com.openshift.client.OpenShiftEndpointException;
import com.openshift.client.OpenShiftException;
import com.openshift.client.utils.MessageAssert;
import com.openshift.client.utils.TestConnectionBuilder;
import com.openshift.internal.client.httpclient.BadRequestException;
import com.openshift.internal.client.httpclient.HttpClientException;

/**
 * @author Andre Dietisheim
 */
public class OpenShiftExceptionTest extends TestTimer {

	private IUser user;
	private IHttpClient clientMock;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();
	private HttpClientMockDirector mockDirector;

	@Before
	public void setup() throws Throwable {
		// pre-conditions
		this.mockDirector = new HttpClientMockDirector();
		this.clientMock = mockDirector
				.mockGetDomains(GET_DOMAINS)
				.mockGetApplications("foobarz", GET_DOMAINS_FOOBARZ_APPLICATIONS_1EMBEDDED)
				.client();
		this.user = new TestConnectionBuilder().defaultCredentials().create(clientMock).getUser();
	}

	@Test
	public void shouldReportServerInTimeoutExceptionMessage() throws FileNotFoundException, OpenShiftException, IOException {
		try {
			// pre-conditions
			mockDirector.mockGetAPI(
					new HttpClientException(new ConnectException("java.net.ConnectException: Connection timed out")));
			// operation
			new TestConnectionBuilder().defaultCredentials().create(clientMock);
			// verification
			fail("exception expected");
		} catch (OpenShiftEndpointException e) {
			// verification
			assertThat(e.getMessage()).contains("https://");
		}
	}

	@Test
	public void shouldThrowWithTextAndExistCode() throws Throwable {
			// pre-conditions
		try {
			mockDirector.mockDeleteDomain("foobarz",
							new BadRequestException(
									DELETE_DOMAINS_FOOBARZ_KO_EXISTINGAPPS.getContentAsString(),
									new IOException(
											"IOException message: Server returned HTTP response code: 400 for URL: https://openshift.redhat.com/broker/rest/domains/foobarz")));
			IDomain domain = user.getDefaultDomain();
			// operation
			domain.destroy();
			// verification
			fail("exception expected");
		} catch (OpenShiftEndpointException e) {
			// verification
			assertThat(e.getRestResponse()).isNotNull();
			Messages messages = e.getRestResponse().getMessages();
			assertThat(messages.size()).isGreaterThan(0);
			Message firstMessage = messages.getAll().iterator().next(); 
			new MessageAssert(firstMessage)
					.hasExitCode(128)
					.hasSeverity(Severity.ERROR)
					.hasText("Domain contains applications. Delete applications first or set force to true.");
		}
	}

}
