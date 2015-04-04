/******************************************************************************* 
 * Copyright (c) 2012-2014 Red Hat, Inc. 
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
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;

import org.fest.assertions.Condition;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.openshift.client.IHttpClient;
import com.openshift.client.IHttpClient.ISSLCertificateCallback;
import com.openshift.client.fakes.HttpServerFake;
import com.openshift.client.fakes.HttpsServerFake;
import com.openshift.client.fakes.WaitingHttpServerFake;
import com.openshift.client.utils.Base64Coder;
import com.openshift.client.utils.ExceptionCauseMatcher;
import com.openshift.client.utils.SSLUtils;
import com.openshift.internal.client.TestTimer;

/**
 * @author Andre Dietisheim
 * @author Nicolas Spano
 * @author Corey Daley
 * @author Sean Kavanagh
 */
public class HttpClientTest extends TestTimer {

	private static final String ACCEPT_APPLICATION_JSON = "Accept: application/json";
	private static final Pattern AUTHORIZATION_PATTERN = Pattern.compile("Authorization: Basic ([^\n]*)");

	private HttpServerFake serverFake;
	private IHttpClient httpClient;
	private HttpsServerFake httpsServerFake;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		this.serverFake = startHttpServerFake(null);
		this.httpsServerFake = startHttpsServerFake(null);
		this.httpClient = new UrlConnectionHttpClientBuilder()
				.setAcceptMediaType(ACCEPT_APPLICATION_JSON)
				.setUserAgent("com.openshift.client.test")
				.setConfigTimeout(new Integer("10000"))
				.client();
	}

	@After
	public void tearDown() {
		serverFake.stop();
		httpsServerFake.stop();
	}

	@Test(expected = HttpClientException.class)
	public void shouldThrowIfNoAcceptedMediaType() throws SocketTimeoutException, HttpClientException,
			MalformedURLException {
		IHttpClient client = new UrlConnectionHttpClient( "useragent", null, "42.0");
		client.get(serverFake.getUrl(), IHttpClient.NO_TIMEOUT);
	}

	@Test
	public void canGet() throws Throwable {
		String response = httpClient.get(serverFake.getUrl(), IHttpClient.NO_TIMEOUT);
		assertThat(response).startsWith("GET");
	}

	
	@Test
	public void canHead() throws Throwable {
		String response = httpClient.head(serverFake.getUrl(), IHttpClient.NO_TIMEOUT);
		assertThat(response).isEqualTo("");
	}

	@Test
	public void canDelete() throws Throwable {
		String response = httpClient.delete(serverFake.getUrl(), IHttpClient.NO_TIMEOUT);
		assertThat(response).startsWith("DELETE");
	}

	/**
	 * Assumes that the server is sending a certificate that is not known to the
	 * client. The test does this by having a custom ssl server with a self-signed certificate.
	 *
	 * @see HttpsServerFake
	 */
	@Test
	public void shouldFailHttpsSelfsignedCertificateNoCallback() throws Throwable {
		// pre-condition
		expectedException.expect(new ExceptionCauseMatcher(instanceOf(SSLHandshakeException.class)));

		// operation
		httpClient.get(httpsServerFake.getUrl(), IHttpClient.NO_TIMEOUT);
	}
	
	/**
	 * Assumes that the server is sending a certificate that is not known to the
	 * client. The test does this by having a custom ssl server with a self-signed certificate.
	 *
	 * @see HttpsServerFake
	 */
	@Test
	public void shouldSucceedHttpsSelfsignedCertificate() throws Throwable {
		IHttpClient client = new UrlConnectionHttpClientBuilder()
		.setAcceptMediaType(ACCEPT_APPLICATION_JSON)
		.setUserAgent("com.openshift.client.test")
		.setSSLCertificateCallback(new ISSLCertificateCallback() {
			
			@Override
			public boolean allowHostname(String hostname, SSLSession session) {
				return true;
			}
			
			@Override
			public boolean allowCertificate(X509Certificate[] chain) {
				return true;
			}
		})
		.client();
		client.get(httpsServerFake.getUrl(), IHttpClient.NO_TIMEOUT);
	}
	
	/**
	 * Assumes that the server is sending a certificate that is not known to the
	 * client. The test does this by having a custom ssl server with a self-signed certificate.
	 *
	 * @see HttpsServerFake
	 */
	@Test
	public void shouldFailHttpsSelfsignedCertificate() throws Throwable {
		// pre-condition
		expectedException.expect(new ExceptionCauseMatcher(instanceOf(SSLHandshakeException.class)));

		IHttpClient client = new UrlConnectionHttpClientBuilder()
		.setAcceptMediaType(ACCEPT_APPLICATION_JSON)
		.setUserAgent("com.openshift.client.test")
		.setSSLCertificateCallback(new ISSLCertificateCallback() {

			@Override
			public boolean allowHostname(String hostname, SSLSession session) {
				return true;
			}

			@Override
			public boolean allowCertificate(X509Certificate[] chain) {
				return false;
			}
		})
		.client();

		// operation
		client.get(httpsServerFake.getUrl(), IHttpClient.NO_TIMEOUT);
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
		assertThat(credentials)
				.describedAs("credentials were not encoded in httpClient").isNotEqualTo(cleartextCredentials);
		assertEquals(username + ":" + password, cleartextCredentials);
	}

	@Test
	public void shouldAcceptJson() throws SocketTimeoutException, HttpClientException, MalformedURLException {
		String response = httpClient.get(serverFake.getUrl(), IHttpClient.NO_TIMEOUT);
		assertNotNull(response);
		assertTrue(response.indexOf(ACCEPT_APPLICATION_JSON) > 0);
	}

	@Test
	public void shouldAddServiceVersionToAcceptHeader() throws Exception {
		// pre-conditions
		String version = "42.0";
		AcceptVersionClientFake clientFake = new AcceptVersionClientFake(version);
		// operation
		HttpURLConnection connection = clientFake.createConnection();
		// verification
		assertThat(clientFake.getAcceptHeader(connection)).endsWith("; version=" + version);
	}

	@Test(expected = NotFoundException.class)
	public void shouldThrowNotFoundException() throws Exception {
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
	public void shouldReasonPhraseIsOptional() throws Exception {
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
	public void shouldHaveURLInExceptionMessage() throws Exception {
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
//TODO delete me?  after moving to apache.httpclient
//	@Test
//	public void shouldRespectGivenTimeoutPOST() throws Throwable {
//		// pre-conditions
//		final int timeout = 1000;
//		final int serverDelay = timeout * 4;
//		assertThat(timeout).isLessThan(IHttpClient.DEFAULT_READ_TIMEOUT);
//		WaitingHttpServerFake serverFake = startWaitingHttpServerFake(serverDelay);
//		long startTime = System.currentTimeMillis();
//		// operations
//		try {
//			httpClient.post(serverFake.getUrl(), new FormUrlEncodedMediaType(), timeout);
//			fail("Timeout expected.");
//		} catch (SocketTimeoutException e) {
//			// assert
//			assertThat(System.currentTimeMillis() - startTime).isGreaterThan(timeout)
//					.isLessThan(serverDelay)
//					.isLessThan(IHttpClient.DEFAULT_READ_TIMEOUT);
//		} finally {
//			serverFake.stop();
//		}
//	}

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

//	@Test
//	public void shouldRespectGivenTimeoutPUT() throws Throwable {
//		// pre-conditions
//		final int timeout = 1000;
//		final int serverDelay = timeout * 4;
//		assertThat(timeout).isLessThan(IHttpClient.DEFAULT_READ_TIMEOUT);
//		WaitingHttpServerFake serverFake = startWaitingHttpServerFake(serverDelay);
//		long startTime = System.currentTimeMillis();
//		// operations
//		try {
//			httpClient.put(serverFake.getUrl(), new FormUrlEncodedMediaType(), timeout);
//			fail("Timeout expected.");
//		} catch (SocketTimeoutException e) {
//			// assert
//			assertThat(System.currentTimeMillis() - startTime).isGreaterThan(timeout)
//					.isLessThan(serverDelay)
//					.isLessThan(IHttpClient.DEFAULT_READ_TIMEOUT);
//		} finally {
//			serverFake.stop();
//		}
//	}

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

	@Test
	public void shouldFallbackToOpenShiftTimeout() throws Throwable {
		// pre-conditions
		final int timeout = 1000;
		final int serverDelay = timeout * 15;
		assertThat(timeout).isLessThan(IHttpClient.DEFAULT_READ_TIMEOUT);
		System.setProperty(IHttpClient.SYSPROP_OPENSHIFT_READ_TIMEOUT, String.valueOf(timeout));
		WaitingHttpServerFake serverFake = startWaitingHttpServerFake(serverDelay);
		long startTime = System.currentTimeMillis();
		// operations
		try {
			httpClient.get(serverFake.getUrl(), IHttpClient.NO_TIMEOUT);
			fail("Timeout expected.");
		} catch (SocketTimeoutException e) {
			// assert
			assertThat(System.currentTimeMillis() - startTime).isGreaterThan(timeout)
					.isLessThan(serverDelay)
					.isLessThan(IHttpClient.DEFAULT_READ_TIMEOUT);
		} finally {
			serverFake.stop();
			System.clearProperty(IHttpClient.SYSPROP_OPENSHIFT_READ_TIMEOUT);
		}
	}

//	@Test
//	public void shouldRespectDefaultTimeout() throws Throwable {
//		// pre-conditions
//		final int timeout = 1000;
//		final int serverDelay = timeout * 10000;
//		IOpenShiftConfiguration configuration = new OpenShiftConfigurationFake(null,null,null,"5000");
//		IHttpClient httpClient = new UrlConnectionHttpClientBuilder()
//				.setAcceptMediaType(ACCEPT_APPLICATION_JSON)
//				.setUserAgent("com.openshift.client.test")
//				.setConfigTimeout(configuration.getTimeout())
//				.client();
//		WaitingHttpServerFake serverFake = startWaitingHttpServerFake(serverDelay);
//		long startTime = System.currentTimeMillis();
//		// operations
//		try {
//			httpClient.get(serverFake.getUrl(), IHttpClient.NO_TIMEOUT);
//			fail("Timeout expected.");
//		} catch (SocketTimeoutException e) {
//			// assert
//			assertThat(System.currentTimeMillis() - startTime)
//					.isGreaterThan(configuration.getTimeout() - 20)
//					.isLessThan(configuration.getTimeout() + 20);
//		} finally {
//			serverFake.stop();
//		}
//	}

	
	private HttpServerFake startHttpServerFake(String statusLine) throws Exception {
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

	private HttpsServerFake startHttpsServerFake(String statusLine) throws Exception {
		int port = new Random().nextInt(9 * 1024) + 1024;
		HttpsServerFake serverFake = null;
		if (statusLine == null) {
			serverFake = new HttpsServerFake(port);
		} else {
			serverFake = new HttpsServerFake(port, null, statusLine);
		}
		serverFake.start();
		return serverFake;
	}
	
	private WaitingHttpServerFake startWaitingHttpServerFake(int delay) throws Exception {
		WaitingHttpServerFake serverFake = new WaitingHttpServerFake(delay);
		serverFake.start();
		return serverFake;
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
			super(userAgent, IHttpClient.MEDIATYPE_APPLICATION_JSON, acceptVersion,
					null, IHttpClient.NO_TIMEOUT, null);
		}

		private UrlConnectionHttpClientFake(String userAgent, String acceptVersion, ISSLCertificateCallback callback) {
			super(userAgent, IHttpClient.MEDIATYPE_APPLICATION_JSON, acceptVersion,
					callback,IHttpClient.NO_TIMEOUT, null);
		}
		
		public HttpURLConnection createConnection() throws IOException, KeyStoreException {
			return super.createConnection(new URL("http://localhost"), userAgent, acceptedVersion, acceptedMediaType, sslAuthorizationCallback, NO_TIMEOUT);
		}
	};

}
