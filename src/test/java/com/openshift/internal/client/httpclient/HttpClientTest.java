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
package com.openshift.internal.client.httpclient;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.openshift.client.IHttpClient;
import com.openshift.client.OpenShiftException;
import com.openshift.client.fakes.HttpServerFake;
import com.openshift.client.fakes.PayLoadReturningHttpClientFake;
import com.openshift.client.fakes.WaitingHttpServerFake;
import com.openshift.client.utils.Base64Coder;
import com.openshift.internal.client.httpclient.request.FormUrlEncodedMediaType;
import com.openshift.internal.client.httpclient.request.StringParameter;

/**
 * @author Andre Dietisheim
 * @author Nicolas Spano
 */
public class HttpClientTest {

	private static final String ACCEPT_APPLICATION_JSON = "Accept: application/json";
	private static final Pattern AUTHORIZATION_PATTERN = Pattern.compile("Authorization: Basic ([^\n]*)");

	private HttpServerFake serverFake;
	private IHttpClient httpClient;

	@Before
	public void setUp() throws IOException {
		this.serverFake = startHttpServerFake(null);
		this.httpClient = new UrlConnectionHttpClientBuilder()
				.setAcceptMediaType(ACCEPT_APPLICATION_JSON)
				.setUserAgent("com.openshift.client.test")
				.client();
	}

	@After
	public void tearDown() {
		serverFake.stop();
	}

	@Test(expected = HttpClientException.class)
	public void shouldThrowIfNoAcceptedMediaType() throws SocketTimeoutException, HttpClientException, MalformedURLException {
		IHttpClient client = new UrlConnectionHttpClient(
				"username", "password", "useragent", false, null, "42.0");
		client.get(serverFake.getUrl(), IHttpClient.NO_TIMEOUT);
	}

	@Test
	public void canGet() throws Throwable {
		String response = httpClient.get(serverFake.getUrl(), IHttpClient.NO_TIMEOUT);
		assertNotNull(response);
		assertTrue(response.startsWith("GET"));
	}

	@Test
	public void canPost() throws Throwable {
		String response = httpClient.post(serverFake.getUrl(), new FormUrlEncodedMediaType(), IHttpClient.NO_TIMEOUT);
		assertNotNull(response);
		assertTrue(response.startsWith("POST"));
	}

	@Test
	public void canPut() throws SocketTimeoutException, HttpClientException, MalformedURLException,
			EncodingException {
		String response = httpClient.put(serverFake.getUrl(), new FormUrlEncodedMediaType(), IHttpClient.NO_TIMEOUT);
		assertNotNull(response);
		assertTrue(response.startsWith("PUT"));
	}

	@Test
	public void canDelete() throws Throwable {
		String response = httpClient.delete(serverFake.getUrl(), IHttpClient.NO_TIMEOUT);
		assertNotNull(response);
		assertTrue(response.startsWith("DELETE"));
	}

	@Test
	public void canAddAuthorization() throws SocketTimeoutException, HttpClientException, MalformedURLException {
		String username = "andre.dietisheim@redhat.com";
		String password = "dummyPassword";
		IHttpClient httpClient = new UrlConnectionHttpClientBuilder()
				.setAcceptMediaType(ACCEPT_APPLICATION_JSON)
				.setUserAgent("com.openshift.client.test")
				.setCredentials(username, password)
				.client();

		String response = httpClient.get(serverFake.getUrl(), IHttpClient.NO_TIMEOUT);
		assertNotNull(response);
		Matcher matcher = AUTHORIZATION_PATTERN.matcher(response);
		assertTrue(matcher.find());
		assertEquals(1, matcher.groupCount());
		String credentials = matcher.group(1);
		String cleartextCredentials = Base64Coder.decode(credentials);
		assertThat(credentials).describedAs("credentials were not encoded in httpClient").isNotEqualTo(
				cleartextCredentials);
		assertEquals(username + ":" + password, cleartextCredentials);
	}

	@Test
	public void shouldAcceptJson() throws SocketTimeoutException, HttpClientException, MalformedURLException {
		String response = httpClient.get(serverFake.getUrl(), IHttpClient.NO_TIMEOUT);
		assertNotNull(response);
		assertTrue(response.indexOf(ACCEPT_APPLICATION_JSON) > 0);
	}

	@Test
	public void hasProperAgentWhenUsingKeys() throws IOException {
		// pre-conditions
		UserAgentClientFake clientFake = new UserAgentClientFake("com.needskey");
		// operation
		HttpURLConnection connection = clientFake.createConnection();
		// verification
		assertThat(clientFake.getUserAgent(connection)).isEqualTo("OpenShift-com.needskey");
	}

	@Test
	public void hasProperAgentWhenUsingKeysAndNoAgent() throws IOException {
		// pre-conditions
		UserAgentClientFake clientFake = new UserAgentClientFake(null);
		// operation
		HttpURLConnection connection = clientFake.createConnection();
		// verification
		assertThat(clientFake.getUserAgent(connection)).isEqualTo("OpenShift");
	}

	@Test
	public void shouldEncodeParametersCorrectly() throws HttpClientException, FileNotFoundException, IOException,
			OpenShiftException {
		// pre-conditions
		IHttpClient httpClient = new PayLoadReturningHttpClientFake(IHttpClient.MEDIATYPE_APPLICATION_JSON, "1.0");
		// operation
		String response = httpClient.post(serverFake.getUrl(),
				new FormUrlEncodedMediaType(),
				IHttpClient.NO_TIMEOUT,
				new StringParameter("adietish", "redhat"),
				new StringParameter("xcoulon", "redhat"));

		// verification
		String[] entries = response.split(String.valueOf(IHttpClient.AMPERSAND));
		assertEquals(2, entries.length);
		String[] keyValuePair = entries[0].split(String.valueOf(IHttpClient.EQUALS));
		assertEquals(2, keyValuePair.length);
		assertEquals("adietish", keyValuePair[0]);
		assertEquals("redhat", keyValuePair[1]);
		keyValuePair = entries[1].split(String.valueOf(IHttpClient.EQUALS));
		assertEquals(2, keyValuePair.length);
		assertEquals("xcoulon", keyValuePair[0]);
		assertEquals("redhat", keyValuePair[1]);
	}

	@Test
	public void shouldAddServiceVersionToAcceptHeader() throws FileNotFoundException, IOException, OpenShiftException,
			HttpClientException {
		// pre-conditions
		String version = "42.0";
		AcceptVersionClientFake clientFake = new AcceptVersionClientFake(version);
		// operation
		HttpURLConnection connection = clientFake.createConnection();
		// verification
		assertThat(clientFake.getAcceptHeader(connection)).endsWith("; version=" + version);
	}

	@Test(expected = NotFoundException.class)
	public void shouldThrowNotFoundException() throws IOException {
		HttpServerFake server = null;
		try {
			// precondition
			this.serverFake.stop();
			server = startHttpServerFake("HTTP/1.0 404 Not Found");

			// operation
			httpClient.get(server.getUrl(), IHttpClient.NO_TIMEOUT);
		} finally {
			server.stop();
		}
	}

	/**
	 * 
	 * RFC 1945 6.1.1 / Reason Phrase is optional
	 * <p>
	 * 'HTTP/1.1 404 ' is equivalent to HTTP/1.1 404 Not Found'
	 * https://bugzilla.redhat.com/show_bug.cgi?id=913796
	 * 
	 * @throws IOException
	 */
	@Test(expected = NotFoundException.class)
	public void shouldReasonPhraseIsOptional() throws IOException {
		HttpServerFake server = null;
		try {
			// precondition
			this.serverFake.stop();
			// RFC 1945 6.1.1 / Reason Phrase is optional
			server = startHttpServerFake("HTTP/1.0 404 ");

			// operation
			httpClient.get(server.getUrl(), IHttpClient.NO_TIMEOUT);
		} finally {
			server.stop();
		}
	}

	@Test
	public void shouldHaveURLInExceptionMessage() throws IOException {
		HttpServerFake server = null;
		try {
			// precondition
			this.serverFake.stop();
			// RFC 1945 6.1.1 / Reason Phrase is optional
			server = startHttpServerFake("HTTP/1.0 404 Not Found");

			// operation
			httpClient.get(server.getUrl(), IHttpClient.NO_TIMEOUT);
			fail("Expected NotFoundException not thrown");
		} catch (NotFoundException e) {
			assertTrue(e.getMessage().contains(server.getUrl().toString()));
		} finally {
			server.stop();
		}
	}

	@Test
	public void shouldRespectGivenTimeoutPOST() throws Throwable {
		// pre-conditions
		final int timeout = 1000;
		final int serverDelay = timeout * 4;
		assertThat(timeout).isLessThan(IHttpClient.DEFAULT_READ_TIMEOUT);
		WaitingHttpServerFake serverFake = startWaitingHttpServerFake(serverDelay);
		long startTime = System.currentTimeMillis();
		// operations
		try {
			httpClient.post(serverFake.getUrl(), new FormUrlEncodedMediaType(), timeout);
			fail("Timeout expected.");
		} catch (SocketTimeoutException e) {
			// assert
			assertThat(System.currentTimeMillis() - startTime).isGreaterThan(timeout)
					.isLessThan(serverDelay)
					.isLessThan(IHttpClient.DEFAULT_READ_TIMEOUT);
		} finally {
			serverFake.stop();
		}
	}

	@Test
	public void shouldRespectGivenTimeoutDELETE() throws Throwable {
		// pre-conditions
		final int timeout = 1000;
		final int serverDelay = timeout * 4;
		assertThat(timeout).isLessThan(IHttpClient.DEFAULT_READ_TIMEOUT);
		WaitingHttpServerFake serverFake = startWaitingHttpServerFake(serverDelay);
		long startTime = System.currentTimeMillis();
		// operations
		try {
			httpClient.delete(serverFake.getUrl(), timeout);
			fail("Timeout expected.");
		} catch (SocketTimeoutException e) {
			// assert
			assertThat(System.currentTimeMillis() - startTime).isGreaterThan(timeout)
					.isLessThan(serverDelay)
					.isLessThan(IHttpClient.DEFAULT_READ_TIMEOUT);
		} finally {
			serverFake.stop();
		}
	}

	@Test
	public void shouldRespectGivenTimeoutPUT() throws Throwable {
		// pre-conditions
		final int timeout = 1000;
		final int serverDelay = timeout * 4;
		assertThat(timeout).isLessThan(IHttpClient.DEFAULT_READ_TIMEOUT);
		WaitingHttpServerFake serverFake = startWaitingHttpServerFake(serverDelay);
		long startTime = System.currentTimeMillis();
		// operations
		try {
			httpClient.put(serverFake.getUrl(), new FormUrlEncodedMediaType(), timeout);
			fail("Timeout expected.");
		} catch (SocketTimeoutException e) {
			// assert
			assertThat(System.currentTimeMillis() - startTime).isGreaterThan(timeout)
					.isLessThan(serverDelay)
					.isLessThan(IHttpClient.DEFAULT_READ_TIMEOUT);
		} finally {
			serverFake.stop();
		}
	}

	@Test
	public void shouldRespectGivenTimeoutGET() throws Throwable {
		// pre-conditions
		final int timeout = 1000;
		final int serverDelay = timeout * 4;
		assertThat(timeout).isLessThan(IHttpClient.DEFAULT_READ_TIMEOUT);
		WaitingHttpServerFake serverFake = this.startWaitingHttpServerFake(serverDelay);

		long startTime = System.currentTimeMillis();
		// operations
		try {
			httpClient.get(serverFake.getUrl(), timeout);
			fail("Timeout expected.");
		} catch (SocketTimeoutException e) {
			// assert
			assertThat(System.currentTimeMillis() - startTime).isGreaterThan(timeout)
					.isLessThan(serverDelay)
					.isLessThan(IHttpClient.DEFAULT_READ_TIMEOUT);
		} finally {
			serverFake.stop();
		}
	}

	private HttpServerFake startHttpServerFake(String statusLine) throws IOException {
		int port = new Random().nextInt(9 * 1024) + 1024;
		HttpServerFake serverFake = null;
		if (statusLine == null) {
			serverFake = new HttpServerFake(port);
		} else {
			serverFake = new HttpServerFake(port, null, statusLine);
		}
		serverFake.start();
		return serverFake;
	}

	private WaitingHttpServerFake startWaitingHttpServerFake(int delay) throws IOException {
		WaitingHttpServerFake serverFake = new WaitingHttpServerFake(delay);
		serverFake.start();
		return serverFake;
	}

	private class UserAgentClientFake extends UrlConnectionHttpClientFake {

		public UserAgentClientFake(String userAgent) {
			super(userAgent, null);
		}

		public String getUserAgent(HttpURLConnection connection) {
			return connection.getRequestProperty(PROPERTY_USER_AGENT);
		}

	}

	private class AcceptVersionClientFake extends UrlConnectionHttpClientFake {

		public AcceptVersionClientFake(String acceptVersion) {
			super(null, acceptVersion);
		}

		public String getAcceptHeader(HttpURLConnection connection) {
			return connection.getRequestProperty(PROPERTY_ACCEPT);
		}
	}

	private abstract class UrlConnectionHttpClientFake extends UrlConnectionHttpClient {
		private UrlConnectionHttpClientFake(String userAgent, String acceptVersion) {
			super("username", "password", userAgent, false, IHttpClient.MEDIATYPE_APPLICATION_JSON, acceptVersion,
					"authkey", "authiv");
		}

		public HttpURLConnection createConnection() throws IOException {
			return super.createConnection(new URL("http://localhost"), username, password, authKey, authIV,
					userAgent, acceptedVersion, acceptedMediaType, NO_TIMEOUT);
		}
	};

}
