/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.express.internal.client.test;

import static com.openshift.express.internal.client.test.utils.ApplicationAsserts.assertApplication;
import static com.openshift.express.internal.client.test.utils.ApplicationAsserts.assertApplicationUrl;
import static com.openshift.express.internal.client.test.utils.ApplicationAsserts.assertGitUri;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Arrays;

import org.junit.Test;

import com.openshift.express.client.ApplicationLogReader;
import com.openshift.express.client.IApplication;
import com.openshift.express.client.ICartridge;
import com.openshift.express.client.IDomain;
import com.openshift.express.client.IHttpClient;
import com.openshift.express.client.IOpenShiftService;
import com.openshift.express.client.IUser;
import com.openshift.express.client.InvalidNameOpenShiftException;
import com.openshift.express.client.OpenShiftEndpointException;
import com.openshift.express.client.OpenShiftException;
import com.openshift.express.client.OpenShiftService;
import com.openshift.express.client.configuration.DefaultConfiguration;
import com.openshift.express.client.configuration.SystemConfiguration;
import com.openshift.express.client.configuration.UserConfiguration;
import com.openshift.express.internal.client.Application;
import com.openshift.express.internal.client.ApplicationInfo;
import com.openshift.express.internal.client.Domain;
import com.openshift.express.internal.client.InternalUser;
import com.openshift.express.internal.client.UserInfo;
import com.openshift.express.internal.client.httpclient.HttpClientException;
import com.openshift.express.internal.client.request.ApplicationAction;
import com.openshift.express.internal.client.request.ApplicationRequest;
import com.openshift.express.internal.client.request.OpenShiftEnvelopeFactory;
import com.openshift.express.internal.client.request.marshalling.ApplicationRequestJsonMarshaller;
import com.openshift.express.internal.client.response.OpenShiftResponse;
import com.openshift.express.internal.client.response.unmarshalling.ApplicationResponseUnmarshaller;
import com.openshift.express.internal.client.response.unmarshalling.ApplicationStatusResponseUnmarshaller;
import com.openshift.express.internal.client.response.unmarshalling.JsonSanitizer;
import com.openshift.express.internal.client.test.fakes.ApplicationResponseFake;
import com.openshift.express.internal.client.test.fakes.NoopOpenShiftServiceFake;
import com.openshift.express.internal.client.test.fakes.TestUser;
import com.openshift.express.internal.client.test.fakes.UserFake;

/**
 * @author André Dietisheim
 */
public class ApplicationTest {

	private InternalUser user = new InternalUser(ApplicationResponseFake.RHLOGIN, ApplicationResponseFake.PASSWORD,
			new NoopOpenShiftServiceFake()) {
		public IDomain getDomain() throws OpenShiftException {
			return new Domain(
					"testNamespace"
					, "testRhcDomain"
					, this
					, getService());
		}
	};

	@Test
	public void canMarshallApplicationCreateRequest() throws Exception {
		String expectedRequestString =
				"password="
						+ URLEncoder.encode(ApplicationResponseFake.PASSWORD, "UTF-8")
						+ "&json_data=%7B"
						+ "%22rhlogin%22+%3A+%22"
						+ URLEncoder.encode(ApplicationResponseFake.RHLOGIN, "UTF-8")
						+ "%22"
						+ "%2C+%22debug%22+%3A+%22true%22"
						+ "%2C+%22cartridge%22+%3A+%22jbossas-7.0%22"
						+ "%2C+%22action%22+%3A+%22"
						+ "configure"
						+ "%22%2C+%22app_name%22+%3A+%22test-application%22"
						+ "%7D";

		String createApplicationRequest = new ApplicationRequestJsonMarshaller().marshall(
				new ApplicationRequest(
						"test-application", ICartridge.JBOSSAS_7, ApplicationAction.CONFIGURE,
						ApplicationResponseFake.RHLOGIN, true));
		String effectiveRequest = new OpenShiftEnvelopeFactory(ApplicationResponseFake.PASSWORD, null, null,
				createApplicationRequest).createString();

		assertEquals(expectedRequestString, effectiveRequest);
	}

	@Test
	public void canMarshallApplicationDestroyRequest() throws Exception {
		String expectedRequestString =
				"password="
						+ URLEncoder.encode(ApplicationResponseFake.PASSWORD, "UTF-8")
						+ "&json_data=%7B"
						+ "%22rhlogin%22+%3A+"
						+ "%22" + URLEncoder.encode(ApplicationResponseFake.RHLOGIN, "UTF-8") + "%22"
						+ "%2C+%22debug%22+%3A+%22true%22"
						+ "%2C+%22cartridge%22+%3A+%22jbossas-7.0%22"
						+ "%2C+%22action%22+%3A+%22"
						+ "deconfigure"
						+ "%22%2C+%22app_name%22+%3A+%22test-application%22"
						+ "%7D";

		String createApplicationRequest = new ApplicationRequestJsonMarshaller().marshall(
				new ApplicationRequest(
						"test-application", ICartridge.JBOSSAS_7, ApplicationAction.DECONFIGURE,
						ApplicationResponseFake.RHLOGIN, true));
		String effectiveRequest = new OpenShiftEnvelopeFactory(ApplicationResponseFake.PASSWORD, null, null,
				createApplicationRequest).createString();

		assertEquals(expectedRequestString, effectiveRequest);
	}

	@Test
	public void canUnmarshallApplicationResponse() throws OpenShiftException {
		String response = JsonSanitizer.sanitize(ApplicationResponseFake.appResponse);
		OpenShiftResponse<IApplication> openshiftResponse =
				new ApplicationResponseUnmarshaller(
						ApplicationResponseFake.APPLICATION_NAME
						, ApplicationResponseFake.APPLICATION_CARTRIDGE
						, user
						, new NoopOpenShiftServiceFake())
						.unmarshall(response);
		IApplication application = openshiftResponse.getOpenShiftObject();
		assertApplication(
				ApplicationResponseFake.APPLICATION_NAME
				, ApplicationResponseFake.APPLICATION_UUID
				, ApplicationResponseFake.APPLICATION_CREATION_LOG
				, ApplicationResponseFake.APPLICATION_HEALTH_CHECK_PATH
				, ApplicationResponseFake.APPLICATION_CARTRIDGE.getName()
				, application);
	}

	@Test
	public void returnsValidGitUri() throws OpenShiftException, IOException {
		OpenShiftService userInfoService = createUserInfoService();
		UserFake user = createUser(userInfoService);
		IApplication application = createApplication(userInfoService, user);

		String gitUri = application.getGitUri();
		assertNotNull(gitUri);
		assertGitUri(
				ApplicationResponseFake.APPLICATION_UUID
				, ApplicationResponseFake.APPLICATION_NAME
				, ApplicationResponseFake.NAMESPACE
				, ApplicationResponseFake.RHC_DOMAIN
				, gitUri);
	}

	@Test
	public void returnsValidApplicationUrl() throws OpenShiftException, IOException {
		OpenShiftService userInfoService = createUserInfoService();
		UserFake user = createUser(userInfoService);
		IApplication application = createApplication(userInfoService, user);

		String applicationUrl = application.getApplicationUrl();
		assertNotNull(applicationUrl);
		assertApplicationUrl(
				ApplicationResponseFake.APPLICATION_NAME
				, ApplicationResponseFake.NAMESPACE
				, ApplicationResponseFake.RHC_DOMAIN
				, applicationUrl);
	}

	@Test
	public void canUnmarshallApplicationStatus() throws OpenShiftException {
		String response = JsonSanitizer.sanitize(ApplicationResponseFake.statusResponse);
		OpenShiftResponse<String> openshiftResponse =
				new ApplicationStatusResponseUnmarshaller().unmarshall(response);
		String status = openshiftResponse.getOpenShiftObject();
		assertNotNull(status);
		assertTrue(status.startsWith("tailing "));
	}

	@Test
	public void canReadFromApplicationLogReader() throws IOException {

		OpenShiftService service = new NoopOpenShiftServiceFake() {
			public String getStatus(String applicationName, ICartridge cartridge, IUser user)
					throws OpenShiftException {
				return ApplicationResponseFake.tail;
			}
		};

		Application application =
				new Application(
						ApplicationResponseFake.APPLICATION_NAME,
						ApplicationResponseFake.APPLICATION_UUID,
						ApplicationResponseFake.APPLICATION_CREATION_LOG,
						ApplicationResponseFake.APPLICATION_HEALTH_CHECK_PATH,
						ApplicationResponseFake.APPLICATION_CARTRIDGE,
						user,
						service);
		ApplicationLogReader reader = new ApplicationLogReader(application, user, service);

		int charactersRead = 0;
		int character = -1;
		while (charactersRead < ApplicationResponseFake.log.length()
				&& (character = reader.read()) != -1) {
			char characterToMatch = ApplicationResponseFake.log.charAt(charactersRead++);
			assertEquals(
					"character at position " + charactersRead
							+ " was '" + character + "'"
							+ " but we expected '" + characterToMatch + "'.",
					characterToMatch, character);
		}
	}

	@Test(expected = InvalidNameOpenShiftException.class)
	public void createApplicationWithInvalidName() throws Exception {
		UserConfiguration userConfiguration = new UserConfiguration(new SystemConfiguration(new DefaultConfiguration()));
		IOpenShiftService service = new OpenShiftService(TestUser.ID, userConfiguration.getLibraServer());
		user = new TestUser(service);
		service.createApplication("invalid_name", ICartridge.JBOSSAS_7, user);
	}

	@Test(expected = InvalidNameOpenShiftException.class)
	public void createDomainWithInvalidNameThrowsOpenShiftException() throws Exception {
		UserConfiguration userConfiguration = new UserConfiguration(new SystemConfiguration(new DefaultConfiguration()));
		IOpenShiftService service = new OpenShiftService(TestUser.ID, userConfiguration.getLibraServer());
		user = new TestUser(service);
		service.createDomain("invalid_name", null, user);
	}

	@Test
	public void responseWithErrorIsReported() throws Exception {
		final String result = "user@redhat.com has already reached the application limit of 5";
		final int exitCode = 1;
		try {
			OpenShiftService service = new OpenShiftService(TestUser.ID, "dummy") {

				protected IHttpClient createHttpClient(final String id, final String url, final boolean verifyHostnames)
						throws MalformedURLException {
					return new IHttpClient() {

						public String post(String data) throws HttpClientException {
							String quotaReachedMessage = "{"
									+ "	\"data\":\"\","
									+ "	\"result\":\"" + result + "\","
									+ "	\"exit_code\":" + exitCode + ","
									+ "	\"debug\":\"\","
									+ "	\"broker_c\":[\"namespace\","
									+ "	\"rhlogin\","
									+ "	\"ssh\","
									+ "	\"app_uuid\","
									+ "	\"debug\","
									+ "	\"alter\","
									+ "	\"cartridge\","
									+ "	\"cart_type\","
									+ "	\"action\","
									+ "	\"app_name\","
									+ "	\"api\"],"
									+ "	\"api\":\"1.1.2\","
									+ "	\"messages\":\"\","
									+ "	\"api_c\":[\"placeholder\"]"
									+ "}";
							throw new HttpClientException(quotaReachedMessage);
						}

						public String get() throws HttpClientException {
							throw new UnsupportedOperationException();
						}
					};
				}
			};

			user = new TestUser("dummyRhLogin", "dummyPassword", service);
			service.createApplication("dummyName", ICartridge.JBOSSAS_7, user);
			fail("No exception thrown");
		} catch (OpenShiftEndpointException e) {
			String responseResult = e.getResponseResult();
			assertNotNull(responseResult);
			assertTrue(responseResult.indexOf("user@redhat.com has already reached the application limit of 5") >= 0);
			assertEquals(exitCode, e.getResponseExitCode());
		}
	}

	private IApplication createApplication(OpenShiftService userInfoService, UserFake user) {
		Application application = new Application(
				ApplicationResponseFake.APPLICATION_NAME
				, ApplicationResponseFake.APPLICATION_UUID
				, ApplicationResponseFake.APPLICATION_CARTRIDGE
				, new ApplicationInfo(
						ApplicationResponseFake.APPLICATION_NAME
						, ApplicationResponseFake.APPLICATION_UUID
						, ApplicationResponseFake.APPLICATION_EMBEDDED
						, ApplicationResponseFake.APPLICATION_CARTRIDGE
						, ApplicationResponseFake.APPLICATION_CREATIONTIME)
				, user
				, userInfoService);
		/**
		 * we have to add it manually since we dont create the application with
		 * the user class
		 * 
		 * @see user#createApplication
		 */
		user.add(application);
		return application;
	}

	private UserFake createUser(OpenShiftService userInfoService) throws OpenShiftException, IOException {
		return new UserFake(ApplicationResponseFake.RHLOGIN, ApplicationResponseFake.PASSWORD, userInfoService);
	}

	private OpenShiftService createUserInfoService() {
		OpenShiftService userInfoService = new NoopOpenShiftServiceFake() {
			public UserInfo getUserInfo(IUser user) throws OpenShiftException {
				ApplicationInfo applicationInfo = new ApplicationInfo(
						ApplicationResponseFake.APPLICATION_NAME,
						ApplicationResponseFake.APPLICATION_UUID,
						ApplicationResponseFake.APPLICATION_EMBEDDED,
						ApplicationResponseFake.APPLICATION_CARTRIDGE,
						ApplicationResponseFake.APPLICATION_CREATIONTIME);
				return new UserInfo(
						ApplicationResponseFake.RHLOGIN,
						ApplicationResponseFake.UUID,
						ApplicationResponseFake.SSHPUBLICKEY,
						ApplicationResponseFake.RHC_DOMAIN,
						ApplicationResponseFake.NAMESPACE,
						Arrays.asList(new ApplicationInfo[] { applicationInfo }),
						ApplicationResponseFake.SSHKEYTYPE,
						ApplicationResponseFake.MAX_GEARS,
						ApplicationResponseFake.CONSUMED_GEARS);
			}
		};
		return userInfoService;
	}
}
