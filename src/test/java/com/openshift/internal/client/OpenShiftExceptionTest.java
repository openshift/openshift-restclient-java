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
import static com.openshift.client.utils.UrlEndsWithMatcher.urlEndsWith;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import com.openshift.client.IDomain;
import com.openshift.client.IHttpClient;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.Message;
import com.openshift.client.Message.Severity;
import com.openshift.client.OpenShiftConnectionFactory;
import com.openshift.client.OpenShiftEndpointException;
import com.openshift.client.OpenShiftException;
import com.openshift.client.utils.MessageAssert;
import com.openshift.client.utils.Samples;
import com.openshift.internal.client.httpclient.BadRequestException;
import com.openshift.internal.client.httpclient.HttpClientException;

/**
 * @author Andre Dietisheim
 */
public class OpenShiftExceptionTest {

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
				.thenReturn(Samples.GET_API.getContentAsString());
		when(mockClient.get(urlEndsWith("/user")))
				.thenReturn(Samples.GET_USER_JSON.getContentAsString());
		when(mockClient.get(urlEndsWith("/domains")))
				.thenReturn(GET_DOMAINS.getContentAsString());
		final IOpenShiftConnection connection = 
				new OpenShiftConnectionFactory().getConnection(
						new RestService("http://mock", "clientId", mockClient), "foo@redhat.com", "bar");
		this.user = connection.getUser();
	}

	@Test
	public void shouldReportServerInTimeoutExceptionMessage() throws HttpClientException, FileNotFoundException,
			OpenShiftException, IOException {
		try {
			IHttpClient mockClient = mock(IHttpClient.class);
			// pre-conditions
			when(mockClient.get(urlEndsWith("/broker/rest/api")))
					.thenThrow(
							new HttpClientException(new ConnectException(
									"java.net.ConnectException: Connection timed out")));
			// operation
			new OpenShiftConnectionFactory().getConnection(
					new RestService("http://mock", "clientId", mockClient), "foo@redhat.com", "bar");
			// verification
			fail("exception expected");
		} catch (OpenShiftEndpointException e) {
			// verification
			assertThat(e.getMessage()).contains("http://mock");
		}
	}

	@Test
	public void shouldThrowWithTextAndExistCode() throws Throwable {
			// pre-conditions
		try {
			when(mockClient.delete(anyMapOf(String.class, Object.class), urlEndsWith("/domains/foobarz")))
					.thenThrow(
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
			Map<String, Message> messageByField = e.getRestResponse().getMessages();
			assertThat(messageByField).isNotEmpty();
			Message firstMessage = messageByField.values().iterator().next(); 
			new MessageAssert(firstMessage)
					.hasExitCode(128)
					.hasSeverity(Severity.ERROR)
					.hasText("Domain contains applications. Delete applications first or set force to true.");
		}
	}

}
