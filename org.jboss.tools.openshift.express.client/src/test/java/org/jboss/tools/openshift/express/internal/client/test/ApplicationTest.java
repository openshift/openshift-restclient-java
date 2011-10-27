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
package org.jboss.tools.openshift.express.internal.client.test;

import static org.jboss.tools.openshift.express.internal.client.test.utils.ApplicationAsserts.assertAppliactionUrl;
import static org.jboss.tools.openshift.express.internal.client.test.utils.ApplicationAsserts.assertGitUri;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;

import org.jboss.tools.openshift.express.client.ApplicationLogReader;
import org.jboss.tools.openshift.express.client.IApplication;
import org.jboss.tools.openshift.express.client.ICartridge;
import org.jboss.tools.openshift.express.client.OpenShiftException;
import org.jboss.tools.openshift.express.client.OpenShiftService;
import org.jboss.tools.openshift.express.internal.client.Application;
import org.jboss.tools.openshift.express.internal.client.ApplicationInfo;
import org.jboss.tools.openshift.express.internal.client.InternalUser;
import org.jboss.tools.openshift.express.internal.client.UserInfo;
import org.jboss.tools.openshift.express.internal.client.request.ApplicationAction;
import org.jboss.tools.openshift.express.internal.client.request.ApplicationRequest;
import org.jboss.tools.openshift.express.internal.client.request.OpenShiftEnvelopeFactory;
import org.jboss.tools.openshift.express.internal.client.request.marshalling.ApplicationRequestJsonMarshaller;
import org.jboss.tools.openshift.express.internal.client.response.OpenShiftResponse;
import org.jboss.tools.openshift.express.internal.client.response.unmarshalling.ApplicationResponseUnmarshaller;
import org.jboss.tools.openshift.express.internal.client.response.unmarshalling.ApplicationStatusResponseUnmarshaller;
import org.jboss.tools.openshift.express.internal.client.response.unmarshalling.JsonSanitizer;
import org.jboss.tools.openshift.express.internal.client.test.fakes.ApplicationResponseFake;
import org.jboss.tools.openshift.express.internal.client.test.fakes.NoopOpenShiftServiceFake;
import org.junit.Test;

/**
 * @author André Dietisheim
 */
public class ApplicationTest {

	private InternalUser user = new InternalUser(ApplicationResponseFake.RHLOGIN, ApplicationResponseFake.PASSWORD,
			new NoopOpenShiftServiceFake());

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
		String effectiveRequest = new OpenShiftEnvelopeFactory(ApplicationResponseFake.PASSWORD,
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
		String effectiveRequest = new OpenShiftEnvelopeFactory(ApplicationResponseFake.PASSWORD,
				createApplicationRequest).createString();

		assertEquals(expectedRequestString, effectiveRequest);
	}

	@Test
	public void canUnmarshallApplicationResponse() throws OpenShiftException {
		String response = JsonSanitizer.sanitize(ApplicationResponseFake.appResponse);
		OpenShiftResponse<Application> openshiftResponse =
				new ApplicationResponseUnmarshaller(
						ApplicationResponseFake.APPLICATION_NAME, ApplicationResponseFake.APPLICATION_CARTRIDGE,
						user, new NoopOpenShiftServiceFake())
						.unmarshall(response);
		Application application = openshiftResponse.getOpenShiftObject();
		assertNotNull(application);
		assertEquals(ApplicationResponseFake.APPLICATION_NAME, application.getName());
		assertEquals(ApplicationResponseFake.APPLICATION_CARTRIDGE, application.getCartridge());
	}

	@Test
	public void returnsValidGitUri() throws OpenShiftException {
		OpenShiftService userInfoService = createUserInfoService();
		InternalUser user = createUser(userInfoService);
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
	public void returnsValidApplicationUrl() throws OpenShiftException {
		OpenShiftService userInfoService = createUserInfoService();
		InternalUser user = createUser(userInfoService);
		IApplication application = createApplication(userInfoService, user);

		String applicationUrl = application.getApplicationUrl();
		assertNotNull(applicationUrl);
		assertAppliactionUrl(
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
			@Override
			public String getStatus(String applicationName, ICartridge cartridge, InternalUser user) throws OpenShiftException {
				return ApplicationResponseFake.tail;
			}
		};

		Application application =
				new Application(ApplicationResponseFake.APPLICATION_NAME,
						ApplicationResponseFake.APPLICATION_CARTRIDGE, user, service);
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

	private IApplication createApplication(OpenShiftService userInfoService, InternalUser user) {
		Application application = new Application(
				ApplicationResponseFake.APPLICATION_NAME
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

	private InternalUser createUser(OpenShiftService userInfoService) {
		return new InternalUser(ApplicationResponseFake.RHLOGIN, ApplicationResponseFake.PASSWORD, userInfoService);
	}

	private OpenShiftService createUserInfoService() {
		OpenShiftService userInfoService = new NoopOpenShiftServiceFake() {
			@Override
			public UserInfo getUserInfo(InternalUser user) throws OpenShiftException {
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
						Arrays.asList(new ApplicationInfo[] { applicationInfo }));
			}
		};
		return userInfoService;
	}
}
