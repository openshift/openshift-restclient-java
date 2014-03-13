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

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.openshift.client.HttpMethod;
import com.openshift.client.IHttpClient;
import com.openshift.client.Message;
import com.openshift.client.OpenShiftEndpointException;
import com.openshift.client.OpenShiftRequestException;
import com.openshift.client.utils.MessageAssert;
import com.openshift.client.utils.OpenShiftTestConfiguration;
import com.openshift.client.utils.Samples;
import com.openshift.internal.client.httpclient.HttpClientException;
import com.openshift.internal.client.httpclient.NotFoundException;
import com.openshift.internal.client.httpclient.request.JsonMediaType;
import com.openshift.internal.client.httpclient.request.Parameter;
import com.openshift.internal.client.httpclient.request.StringParameter;
import com.openshift.internal.client.response.Link;
import com.openshift.internal.client.response.LinkParameter;
import com.openshift.internal.client.response.LinkParameterType;
import com.openshift.internal.client.response.OpenShiftJsonDTOFactory;
import com.openshift.internal.client.response.RestResponse;

/**
 * @author Andre Dietisheim
 */
public class RestServiceTest extends TestTimer {

	protected static final String KEY_PROTOCOL_VERSION = "protocol_version";
	private IRestService service;
	private HttpClientMockDirector mockDirector;
	private IHttpClient clientMock;

	@Before
	public void setUp() throws Exception {
		this.mockDirector = new HttpClientMockDirector();
		String jsonResponse = "{}";
		this.clientMock = mockDirector
				.mockGetAny(jsonResponse)
				.mockPostAny(jsonResponse)
				.mockPutAny(jsonResponse)
				.mockDeleteAny(jsonResponse)
				.client();

		OpenShiftTestConfiguration configuration = new OpenShiftTestConfiguration();
		this.service = new RestService(
				configuration.getStagingServer(),
				configuration.getClientId(),
				new JsonMediaType(),
				IHttpClient.MEDIATYPE_APPLICATION_JSON,
				new OpenShiftJsonDTOFactory(),
				clientMock);
	}

	@Test
	public void shouldNotThrowIfNoReqiredParameter() throws Exception {
		// operation
		Link link = new TestLink("0 required parameter", "/dummy", HttpMethod.GET);
		service.request(link);
	}

	@Test
	public void shouldGetIfGetHttpMethod() throws Exception {
		// operation
		service.request(new TestLink("0 required parameter", "http://www.redhat.com", HttpMethod.GET));
		// verifications
		mockDirector.verifyGetAny(1);
	}

	@Test
	public void shouldPostIfPostHttpMethod() throws Exception {
		// operation
		service.request(new TestLink("0 required parameter", "http://www.redhat.com", HttpMethod.POST));
		// verifications
		mockDirector.verifyPostAny(1);
	}

	@Test
	public void shouldPutIfPutHttpMethod() throws Exception {
		// operation
		service.request(new TestLink("0 required parameter", "http://www.redhat.com", HttpMethod.PUT));
		// verifications
		mockDirector.verifyPutAny(1);
	}

	@Test
	public void shouldDeleteIfDeleteHttpMethod() throws Exception {
		// operation
		service.request(new TestLink("0 required parameter", "http://www.redhat.com", HttpMethod.DELETE));
		// verifications
		mockDirector.verifyDeleteAny(1);
	}

	@Test
	public void shouldPatchIfPatchHttpMethod() throws Exception {
		// operation
		service.request(new TestLink("0 required parameter", "http://www.redhat.com", HttpMethod.PATCH));
		// verifications
		mockDirector.verifyPatchAny(1);
	}

	@Test
	public void shouldHeadIfHeadHttpMethod() throws Exception {
		// operation
		service.request(new TestLink("0 required parameter", "http://www.redhat.com", HttpMethod.HEAD));
		// verifications
		mockDirector.verifyHeadAny(1);
	}

	@Test
	public void shouldNotAddServerToAbsUrl() throws Exception {
		// operation
		String url = "http://www.redhat.com";
		service.request(new TestLink("0 required parameter", url, HttpMethod.GET));
		// verifications
		mockDirector.verifyGet(url, 1);
	}

	@Test
	public void shouldAddServerToPath() throws Exception {
		// operation
		String url = "/adietisheim-redhat";
		service.request(new TestLink("0 require parameter", url, HttpMethod.GET));
		// verifications
		String targetUrl = service.getServiceUrl() + url.substring(1, url.length());
		mockDirector.verifyGet(targetUrl, 1);
	}

	@Test
	public void shouldNotAddBrokerPathIfPresent() throws Exception {
		// operation
		String url = "/broker/rest/adietisheim-redhat";
		service.request(new TestLink("0 require parameter", url, HttpMethod.GET));
		// verifications
		String targetUrl = service.getPlatformUrl() + url;
		mockDirector.verifyGet(targetUrl, 1);
	}

	@Test
	public void shouldNotAddEmptyServerAndEmptyServicePath() throws Exception {
		// pre-conditions
		String href = "/adietisheim-redhat";
		Link link = new TestLink("0 require parameter", href , HttpMethod.GET);
		String url = link.getHref(null, null);
		// verifications
		assertThat(url).isEqualTo(href);
	}

	@Test
	public void shouldAddParameters() throws Exception {
		// pre-conditions
		String href = "/broker/rest/adietisheim-redhat";
		Link link = new TestLink("0 require parameter", href , HttpMethod.GET);
		// operation
		String url = link.getHref(null, null, new Parameter("include", "cartridges"), new Parameter("exclude", "fridges"));
		// verifications
		assertThat(url).isEqualTo(href + "?include=cartridges&exclude=fridges");
	}

	@Test
	public void shouldSubstituteUrlVariables() throws Exception {
		// pre-conditions
		String href = "/broker/rest/:company/adietisheim/:language/openshift-java-client";
		Link link = new TestLink("", href , HttpMethod.GET);
		// operation
		String url = link.getHref(null, null, Arrays.<Parameter>asList(
				new StringParameter("company", "redhat"), 
				new StringParameter("language", "java")),
				null);
		// verifications
		assertThat(url).isEqualTo("/broker/rest/redhat/adietisheim/java/openshift-java-client");
	}

	@Test
	public void shouldNotSubstituteUrlVariables() throws Exception {
		// pre-conditions
		String href = "https://openshift.redhat.com/broker/rest/:company/adietisheim/:oss/openshift-java-client";
		Link link = new TestLink("", href , HttpMethod.GET);
		// operation
		String url = link.getHref(null, null, Arrays.<Parameter>asList(
				new StringParameter("company", "redhat"), 
				new StringParameter("cloud", "redhat")),
				null);
		// verifications
		assertThat(url).isEqualTo("https://openshift.redhat.com/broker/rest/redhat/adietisheim/:oss/openshift-java-client");
	}

	@Test
	public void shouldSubstituteUrlVariablesTwice() throws Exception {
		// pre-conditions
		String href = "https://openshift.redhat.com/broker/rest/:cloud_company/adietisheim/:cloud_company/openshift-java-client";
		Link link = new TestLink("", href , HttpMethod.GET);
		// operation
		String url = link.getHref(null, null, 
				Arrays.<Parameter>asList(new StringParameter("cloud_company", "redhat")),
				null);
		// verifications
		assertThat(url).isEqualTo("https://openshift.redhat.com/broker/rest/redhat/adietisheim/redhat/openshift-java-client");
	}

	@Test
	public void shouldThrowExceptionWithResponseOnNotFound() throws Exception {
		try {
			// pre-conditions
			mockDirector.mockGetAny(new NotFoundException(Samples.GET_DOMAINS_FOOBAR_KO_NOTFOUND.getContentAsString()));
			// operation
			service.request(new TestLink("0 require parameter", "/broker/rest/adietisheim", HttpMethod.GET));
			// verifications
			fail("OpenShiftEndPointException expected, did not occurr");
		} catch (OpenShiftEndpointException e) {
			assertThat(e.getRestResponse()).isNotNull();
		}
	}

	@Test
	public void shouldHaveMessageIfErrors() throws Exception {
		try {
			// pre-conditions
			mockDirector
					.mockPostAny(new HttpClientException(Samples.POST_FOOBAR_DOMAINS_KO_INUSE.getContentAsString()));
			// operation
			service.request(new TestLink("0 require parameter", "/broker/rest/domains", HttpMethod.POST));
			// verifications
			fail("OpenShiftEndPointException expected, did not occurr");
		} catch (OpenShiftEndpointException e) {
			RestResponse restResponse = e.getRestResponse();
			assertThat(restResponse).isNotNull();
			assertThat(restResponse.getMessages().size()).isEqualTo(1);
			Message message = restResponse.getMessages().getAll().iterator().next();
			assertThat(new MessageAssert(message))
					.hasText("Namespace 'foobar' is already in use. Please choose another.")
					.hasSeverity(Severity.ERROR)
					.hasExitCode(103)
					.hasField(new Field("id"));
		}
	}

	@Test
	public void shouldReportPlatformUrlInException() throws Exception {
		try {
			// pre-conditions
			mockDirector
					.mockPostAny(new HttpClientException(Samples.POST_FOOBAR_DOMAINS_KO_INUSE.getContentAsString()));
			// operation
			service.request(new TestLink("0 require parameter", "/broker/rest/domains", HttpMethod.POST));
			// verifications
			fail("OpenShiftEndPointException expected, did not occurr");
		} catch (OpenShiftEndpointException e) {
			assertThat(e.getMessage()).contains(service.getPlatformUrl());
		}
	}

	@Test
	public void shouldReturnPlatformWithSchema() throws Exception {
		// pre-conditions
		final String serverUrl = "nonHttpUrl";
		IRestService service = new RestService(
				serverUrl,
				new OpenShiftTestConfiguration().getClientId(),
				new JsonMediaType(),
				IHttpClient.MEDIATYPE_APPLICATION_JSON,
				new OpenShiftJsonDTOFactory(),
				clientMock);
		// operation
		String platformUrl = service.getPlatformUrl();
		// verifications
		assertThat(platformUrl).startsWith("https://");
		assertThat(platformUrl).isEqualTo("https://" + serverUrl);
	}

	@Test
	public void shouldReturnUnchangedPlatformUrl() throws Exception {
		// pre-conditions
		final String serverUrl = "http://fakeUrl";
		IRestService service = new RestService(
				serverUrl,
				new OpenShiftTestConfiguration().getClientId(),
				new JsonMediaType(),
				IHttpClient.MEDIATYPE_APPLICATION_JSON,
				new OpenShiftJsonDTOFactory(),
				clientMock);
		// operation
		String platformUrl = service.getPlatformUrl();
		// verifications
		assertThat(platformUrl).isEqualTo(serverUrl);
	}

	@Test
	public void shouldSetUserAgentToHttpClient() throws Exception {
		// pre-condition
		RestServiceProperties properties = new RestServiceProperties() {
			protected Properties getProperties() throws IOException {
				Properties properties = new Properties();
				properties.put(KEY_USERAGENTPATTERN, "{0} {1}");
				return properties;
			}
		};
		IHttpClient httpClientMock = mock(IHttpClient.class);

		// operation
		String clientId = "unit-test";
		new RestService("jboss.org",
				clientId,
				new JsonMediaType(),
				IHttpClient.MEDIATYPE_APPLICATION_JSON,
				new OpenShiftJsonDTOFactory(),
				httpClientMock,
				properties);

		// verifications
		String userAgent = properties.getUseragent(clientId);
		verify(httpClientMock, times(1)).setUserAgent(userAgent);
	}

	@Test
	public void shouldDefaultTo12ProtocolVersion() throws Exception {
		// pre-condition
		RestServiceProperties properties = new RestServiceProperties();
		IHttpClient httpClientMock = mock(IHttpClient.class);

		// operation
		String clientId = "unit-test";
		new RestService(
				"jboss.org",
				clientId,
				new JsonMediaType(),
				IHttpClient.MEDIATYPE_APPLICATION_JSON,
				new OpenShiftJsonDTOFactory(),
				httpClientMock,
				properties);

		// verifications
		verify(clientMock, times(1)).setAcceptVersion("1.2");
	}

	@Test
	public void shouldSetAcceptedMediaTypeJsonToHttpClient() throws Exception {
		// pre-condition
		IHttpClient httpClientMock = mock(IHttpClient.class);
		RestServiceProperties properties = new RestServiceProperties();

		// operation
		new RestService(
				"jboss.org",
				"unit-test",
				new JsonMediaType(),
				IHttpClient.MEDIATYPE_APPLICATION_JSON,
				new OpenShiftJsonDTOFactory(),
				httpClientMock,
				properties);

		// verifications
		verify(httpClientMock, times(1)).setAcceptedMediaType(IHttpClient.MEDIATYPE_APPLICATION_JSON);
	}

	@Test
	public void shouldUseGivenProtocolVersion() throws Exception {
		// pre-condition
		RestServiceProperties properties = new RestServiceProperties();
		IHttpClient httpClientMock = mock(IHttpClient.class);

		// operation
		String clientId = "unit-test";
		new RestService(
				"jboss.org", 
				clientId, 
				"4.2", 				
				new JsonMediaType(),
				IHttpClient.MEDIATYPE_APPLICATION_JSON,
				new OpenShiftJsonDTOFactory(),
				httpClientMock,
				properties);

		// verifications
		verify(httpClientMock, times(1)).setAcceptVersion("4.2");
	}

	public class TestLink extends Link {

		public TestLink(String rel, String href, HttpMethod httpMethod, List<LinkParameter> requiredParams,
				List<LinkParameter> optionalParams) {
			super(rel, href, httpMethod, requiredParams, optionalParams);
		}

		public TestLink(String rel, String href, HttpMethod httpMethod) {
			super(rel, href, httpMethod);
		}

		public TestLink(String rel, String href, String httpMethod, List<LinkParameter> requiredParams,
				List<LinkParameter> optionalParams) {
			super(rel, href, httpMethod, requiredParams, optionalParams);
		}
	}

	public class TestLinkParameter extends LinkParameter {

		public TestLinkParameter(String name, LinkParameterType type, String defaultValue, String description,
				List<String> validOptions) throws OpenShiftRequestException {
			super(name, type, defaultValue, description, validOptions);
		}

		public TestLinkParameter(String name, String type, String defaultValue, String description,
				List<String> validOptions) throws OpenShiftRequestException {
			super(name, type, defaultValue, description, validOptions);
		}
	}

}
