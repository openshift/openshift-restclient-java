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

import static com.openshift.client.utils.ParametersMatcher.eq;
import static com.openshift.client.utils.UrlEndsWithMatcher.urlEndsWith;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.mockito.Matchers;
import org.mockito.Mockito;

import com.openshift.client.IDomain;
import com.openshift.client.IHttpClient;
import com.openshift.client.IUser;
import com.openshift.client.OpenShiftException;
import com.openshift.client.utils.Samples;
import com.openshift.client.utils.TestConnectionFactory;
import com.openshift.internal.client.httpclient.EncodingException;
import com.openshift.internal.client.httpclient.HttpClientException;
import com.openshift.internal.client.httpclient.request.IMediaType;
import com.openshift.internal.client.httpclient.request.Parameter;

/**
 * @author Andre Dietisheim
 * @author Syed Iqbal
 */
public class HttpClientMockDirector {

	private IHttpClient client;

	public HttpClientMockDirector() throws SocketTimeoutException, HttpClientException {
		this.client = Mockito.mock(IHttpClient.class);
		mockGetAPI(Samples.GET_API)
				.mockGetCartridges(Samples.GET_CARTRIDGES)
				.mockGetUser(Samples.GET_USER);
	}
	
	public HttpClientMockDirector mockGetAny(String response) throws SocketTimeoutException, HttpClientException {
		when(client.get(any(URL.class), anyInt())).thenReturn(response);
		return this;
	}

	public HttpClientMockDirector mockGetAny(Exception exception) throws SocketTimeoutException, HttpClientException {
		when(client.get(any(URL.class), anyInt())).thenThrow(exception);
		return this;
	}

	public HttpClientMockDirector mockHeadAny(String response) throws SocketTimeoutException, HttpClientException {
		when(client.head(any(URL.class), anyInt())).thenReturn(response);
		return this;
	}

	public HttpClientMockDirector mockHeadtAny(Exception exception) throws SocketTimeoutException, HttpClientException {
		when(client.head(any(URL.class), anyInt())).thenThrow(exception);
		return this;
	}

	public HttpClientMockDirector mockPostAny(Samples postRequestResponse)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		return mockPostAny(postRequestResponse.getContentAsString());
	}

	public HttpClientMockDirector mockPostAny(String jsonResponse)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		when(client.post(any(URL.class), any(IMediaType.class), anyInt(), Matchers.<Parameter[]>anyVararg()))
				.thenReturn(jsonResponse);
		return this;
	}

	public HttpClientMockDirector mockPostAny(Exception exception)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		when(client.post(any(URL.class), any(IMediaType.class), anyInt(), Matchers.<Parameter[]>anyVararg()))
				.thenThrow(exception);
		return this;
	}

	public HttpClientMockDirector mockPatchAny(Samples postRequestResponse)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		return mockPatchAny(postRequestResponse.getContentAsString());
	}

	public HttpClientMockDirector mockPatchAny(String jsonResponse)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		when(client.patch(any(URL.class), any(IMediaType.class), anyInt(), Matchers.<Parameter[]>anyVararg()))
				.thenReturn(jsonResponse);
		return this;
	}

	public HttpClientMockDirector mockPatchAny(Exception exception)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		when(client.patch(any(URL.class), any(IMediaType.class), anyInt(), Matchers.<Parameter[]>anyVararg()))
				.thenThrow(exception);
		return this;
	}

	public HttpClientMockDirector mockPutAny(String jsonResponse)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		when(client.put(any(URL.class), any(IMediaType.class), anyInt(), Matchers.<Parameter[]>anyVararg()))
				.thenReturn(jsonResponse);
		return this;
	}

	public HttpClientMockDirector mockDeleteAny(String jsonResponse)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		when(client.delete(any(URL.class), any(IMediaType.class), anyInt(), Matchers.<Parameter[]>anyVararg()))
				.thenReturn(jsonResponse);
		return this;
	}

	public HttpClientMockDirector mockGetAPI(Samples getApiResourceResponse) 
			throws HttpClientException, SocketTimeoutException {
		when(client.get(urlEndsWith("/api"), anyInt()))
				.thenReturn(getApiResourceResponse.getContentAsString());
		return this;
	}

	public HttpClientMockDirector mockGetAPI(Exception exception) throws SocketTimeoutException {
		when(client.get(urlEndsWith("/api"), anyInt()))
				.thenThrow(exception);
		return this;
	}

	public HttpClientMockDirector mockGetCartridges(Samples cartridgesResourceResponse) throws HttpClientException,
			SocketTimeoutException {
		when(client.get(urlEndsWith("/cartridges"), anyInt()))
				.thenReturn(cartridgesResourceResponse.getContentAsString());
		return this;
	}

	public HttpClientMockDirector mockGetUser(Samples userResourceResponse) throws HttpClientException,
			SocketTimeoutException {
		when(client.get(urlEndsWith("/user"), anyInt()))
				.thenReturn(userResourceResponse.getContentAsString());
		return this;
	}

	public HttpClientMockDirector mockGetKeys(Samples keysRequestResponse) throws SocketTimeoutException, HttpClientException {
		when(client.get(urlEndsWith("/user/keys"), anyInt()))
				.thenReturn(keysRequestResponse.getContentAsString());
		return this;
	}
	
	public HttpClientMockDirector mockCreateKey(Samples createKeyRequestResponse) 
			throws SocketTimeoutException, HttpClientException, EncodingException {
		when(client.post(
				urlEndsWith("/user/keys"),
				any(IMediaType.class), anyInt(),
				Matchers.<Parameter[]>anyVararg()))
				.thenReturn(createKeyRequestResponse.getContentAsString());
		return this;
	}

	public HttpClientMockDirector mockUpdateKey(String keyName, Samples updateKeyRequestResponse, Parameter... parameters) 
			throws SocketTimeoutException, HttpClientException, EncodingException {
		when(client.put(
				urlEndsWith("/user/keys/" + keyName),
				any(IMediaType.class), anyInt(),
				Matchers.<Parameter[]>anyVararg()))
				.thenReturn(updateKeyRequestResponse.getContentAsString());
		return this;
	}

	public HttpClientMockDirector mockGetDomains(Samples domainsResourceResponse)
			throws SocketTimeoutException, HttpClientException {
		when(client.get(urlEndsWith("/domains"), anyInt()))
				.thenReturn(domainsResourceResponse.getContentAsString());
		return this;
	}

	public HttpClientMockDirector mockCreateDomain(Samples domainResourceResponse)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		when(client.post(urlEndsWith("/domains"), any(IMediaType.class), anyInt(), Matchers.<Parameter[]>anyVararg()))
				.thenReturn(domainResourceResponse.getContentAsString());
		return this;
	}

	public HttpClientMockDirector mockDeleteDomain(String domainId, Samples deleteDomainResourceResponse)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		when(client.delete(urlEndsWith("/domains/" + domainId), any(IMediaType.class), anyInt(), Matchers.<Parameter[]>anyVararg()))
				.thenReturn(deleteDomainResourceResponse.getContentAsString());
		return this;
	}

	public HttpClientMockDirector mockDeleteDomain(String domainId, Exception exception)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		when(client.delete(urlEndsWith("/domains/" + domainId), any(IMediaType.class), anyInt(), Matchers.<Parameter[]>anyVararg()))
				.thenThrow(exception);
		return this;
	}

	public HttpClientMockDirector mockRenameDomain(String domainId, Samples getDomainsResourceResponse)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		when(client.put(urlEndsWith("/domains/" + domainId), any(IMediaType.class), anyInt(), Matchers.<Parameter[]>anyVararg()))
				.thenReturn(getDomainsResourceResponse.getContentAsString());
		return this;
	}

	public HttpClientMockDirector mockGetDomain(String domainId, Samples domainResourceResponse)
			throws SocketTimeoutException, HttpClientException {
		when(client.get(urlEndsWith("/domains/" + domainId), anyInt()))
				.thenReturn(domainResourceResponse.getContentAsString());
		return this;

	}

	public HttpClientMockDirector mockGetApplications(String domainId, Samples applicationsResourceResponse)
			throws SocketTimeoutException, HttpClientException {
		when(client.get(urlEndsWith("/domains/" + domainId + "/applications?include=cartridges"), anyInt()))
				.thenReturn(applicationsResourceResponse.getContentAsString());
		return this;
	}

	public HttpClientMockDirector mockGetApplications(String domainId, Exception exception)
			throws SocketTimeoutException, HttpClientException {
		when(client.get(urlEndsWith("/domains/" + domainId + "/applications?include=cartridges"), anyInt()))
				.thenThrow(exception);
		return this;
	}

	public HttpClientMockDirector mockCreateApplication(String domainId, Samples postDomainsResourceResponse)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		when(client.post(
				urlEndsWith("/domains/" + domainId + "/applications?include=cartridges"), any(IMediaType.class), anyInt(), Matchers.<Parameter[]>anyVararg()))
				.thenReturn(postDomainsResourceResponse.getContentAsString());
		return this;
	}

	public HttpClientMockDirector mockPostApplicationEvent(String domainId, String applicationName,
			Samples postApplicationEvent)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		when(client.post(
				urlEndsWith("/domains/" + domainId + "/applications/" + applicationName + "/events"),
				any(IMediaType.class), anyInt(),
				Matchers.<Parameter[]>anyVararg()))
				.thenReturn(postApplicationEvent.getContentAsString());
		return this;
	}

	public HttpClientMockDirector mockGetApplication(String domainId, String applicationName,
			Samples applicationResourceResponse)
			throws SocketTimeoutException, HttpClientException {
		when(client.get(urlEndsWith("/domains/" + domainId + "/applications/" + applicationName), anyInt()))
				.thenReturn(applicationResourceResponse.getContentAsString());
		return this;
	}

	public HttpClientMockDirector mockGetApplicationCartridges(String domainId, String applicationName,
			Samples cartridgesResourceResponse)
			throws SocketTimeoutException, HttpClientException {
		when(client.get(
				urlEndsWith("/domains/" + domainId + "/applications/" + applicationName + "/cartridges"),
				anyInt()))
				.thenReturn(cartridgesResourceResponse.getContentAsString());
		return this;
	}

	public HttpClientMockDirector mockGetGearGroups(String domainId, String applicationName,
			Samples gearGroupsResourceResponse)
			throws SocketTimeoutException, HttpClientException {
		when(client.get(
				urlEndsWith("/domains/" + domainId + "/applications/" + applicationName + "/gear_groups"),
				anyInt()))
				.thenReturn(gearGroupsResourceResponse.getContentAsString());
		return this;
	}

	public HttpClientMockDirector mockPostApplicationEvent(String domainId, String applicationName, Exception exception)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		when(client.post(
				urlEndsWith("/domains/" + domainId + "/applications/" + applicationName + "/events"),
				any(IMediaType.class), anyInt(),
				Matchers.<Parameter[]>anyVararg()))
				.thenThrow(exception);
		return this;
	}

	public HttpClientMockDirector mockAddEmbeddableCartridge(String domainId, String applicationName,
			Samples addEmbeddedCartridgeResponse)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		when(client.post(
				urlEndsWith("/domains/" + domainId + "/applications/" + applicationName + "/cartridges"),
				any(IMediaType.class), anyInt(),
				Matchers.<Parameter[]>anyVararg()))
				.thenReturn(addEmbeddedCartridgeResponse.getContentAsString());
		return this;
	}

	public HttpClientMockDirector mockAddEmbeddableCartridge(String domainId, String applicationName,
			Exception exception)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		when(client.post(
				urlEndsWith("/domains/" + domainId + "/applications/" + applicationName + "/cartridges"),
				any(IMediaType.class), anyInt(), 
				Matchers.<Parameter[]>anyVararg()))
				.thenThrow(exception);
		return this;
	}

	public HttpClientMockDirector mockRemoveEmbeddableCartridge(String domainId, String applicationName,
			String cartridgeName,
			Exception exception)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		when(client.delete(
				urlEndsWith(
				"/domains/" + domainId + "/applications/" + applicationName + "/cartridges/" + cartridgeName),
				any(IMediaType.class), anyInt(),
				Matchers.<Parameter[]>anyVararg()))
				.thenThrow(exception);
		return this;
	}

	public HttpClientMockDirector verifyPostApplicationEvent(String domainId, String applicationName)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		verify(client, times(1)).post(
				urlEndsWith("/domains/" + domainId + "/applications/" + applicationName + "/events"),
				any(IMediaType.class), anyInt(),
				Matchers.<Parameter[]>anyVararg());
		return this;
	}

	public HttpClientMockDirector verifyListEmbeddableCartridges(int times, String domainId, String applicationName)
			throws SocketTimeoutException, HttpClientException {
		verify(client, times(times)).get(
				urlEndsWith("/domains/" + domainId + "/applications/" + applicationName + "/cartridges"),
				anyInt());
		return this;

	}

	public HttpClientMockDirector verifyGetApplicationCartridges(int times, String domainId, String applicationName)
			throws SocketTimeoutException, HttpClientException {
		verify(client, times(times)).get(
				urlEndsWith("/domains/" + domainId + "/applications/" + applicationName + "/cartridges"),
				anyInt());
		return this;
	}

	public HttpClientMockDirector verifyAddEmbeddableCartridge(String domainId, String applicationName)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		verify(client, times(1)).post(
				urlEndsWith("/domains/" + domainId + "/applications/" + applicationName + "/cartridges"),
				any(IMediaType.class), anyInt(),
				Matchers.<Parameter[]>anyVararg());
		return this;
	}

	public HttpClientMockDirector verifyDeleteEmbeddableCartridge(String domainId, String applicationName,
			String cartridgeName)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		verify(client, times(1)).delete(
				urlEndsWith("/domains/" + domainId + "/applications/" + applicationName + "/cartridges/"
						+ cartridgeName),
				any(IMediaType.class), anyInt(),
				Matchers.<Parameter[]>anyVararg());
		return this;
	}

	public HttpClientMockDirector verifyGetAny(int times) throws SocketTimeoutException, HttpClientException {
		verify(client, times(times)).get(any(URL.class), anyInt());
		return this;
	}

	public HttpClientMockDirector verifyGet(String url, int times)
			throws SocketTimeoutException, HttpClientException, MalformedURLException {
		verify(client, times(times)).get(eq(new URL(url)), anyInt());
		return this;
	}

	public HttpClientMockDirector verifyPostAny(int times)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		verify(client, times(times)).post(any(URL.class), any(IMediaType.class), anyInt(),Matchers.<Parameter[]>anyVararg());
		return this;
	}

	public HttpClientMockDirector verifyPost(Parameter... parameters)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		verify(client).post(any(URL.class), any(IMediaType.class), anyInt(), eq(parameters));
		return this;
	}

	public HttpClientMockDirector verifyPost(String url, int times)
			throws SocketTimeoutException, HttpClientException, EncodingException, MalformedURLException {
		verify(client, times(times)).post(new URL(url), any(IMediaType.class), anyInt(), Matchers.<Parameter[]>anyVararg());
		return this;
	}

	public HttpClientMockDirector verifyPutAny(int times)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		verify(client, times(times)).put(any(URL.class), any(IMediaType.class), anyInt(),Matchers.<Parameter[]>anyVararg());
		return this;
	}

	public HttpClientMockDirector verifyPut(String url, int times)
			throws SocketTimeoutException, HttpClientException, EncodingException, MalformedURLException {
		verify(client, times(times)).put(new URL(url), any(IMediaType.class), anyInt(),Matchers.<Parameter[]>anyVararg());
		return this;
	}

	public HttpClientMockDirector verifyDeleteAny(int times)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		verify(client, times(times)).delete(any(URL.class), any(IMediaType.class), anyInt(),Matchers.<Parameter[]>anyVararg());
		return this;
	}

	public HttpClientMockDirector verifyDelete(String url, int times)
			throws SocketTimeoutException, HttpClientException, EncodingException, MalformedURLException {
		verify(client, times(times)).delete(new URL(url), any(IMediaType.class), anyInt(), Matchers.<Parameter[]>anyVararg());
		return this;
	}

	public HttpClientMockDirector verifyPatchAny(int times)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		verify(client, times(times)).patch(any(URL.class), any(IMediaType.class), anyInt(),Matchers.<Parameter[]>anyVararg());
		return this;
	}

	public HttpClientMockDirector verifyHeadAny(int times)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		verify(client, times(times)).head(any(URL.class), anyInt());
		return this;
	}

	public HttpClientMockDirector verifyGetDomains() throws SocketTimeoutException, HttpClientException {
		verify(client, times(1)).get(urlEndsWith("/domains"), anyInt());
		return this;
	}

	public HttpClientMockDirector verifyRenameDomain(String domainId)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		verify(client, times(1)).put(urlEndsWith(domainId), any(IMediaType.class), anyInt(),Matchers.<Parameter[]>anyVararg());
		return this;
	}

	public HttpClientMockDirector verifyGetDomain(String domainId) throws SocketTimeoutException, HttpClientException {
		verify(client, times(1)).get(urlEndsWith("/domains/" + domainId), anyInt());
		return this;
	}

	public HttpClientMockDirector verifyGetApplications(String domainId, int times)
			throws SocketTimeoutException, HttpClientException {
		verify(client, times(times)).get(urlEndsWith("/domains/" + domainId + "/applications?include=cartridges"), anyInt());
		return this;
	}

	public HttpClientMockDirector verifyGetAPI() throws SocketTimeoutException, HttpClientException {
		verify(client, times(1)).get(urlEndsWith("/broker/rest/api"), anyInt());
		return this;
	}

	public HttpClientMockDirector verifyGetUser() throws SocketTimeoutException, HttpClientException {
		verify(client, times(1)).get(urlEndsWith("/broker/rest/user"), anyInt());
		return this;
	}

	public HttpClientMockDirector verifyCreateKey(Parameter... parameters)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		verify(client).post(
				urlEndsWith("/user/keys"), any(IMediaType.class), anyInt(), eq(parameters));
		return this;
	}

	public HttpClientMockDirector verifyUpdateKey(String keyName, Parameter... parameters)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		verify(client).put(
				urlEndsWith("/user/keys/" + keyName), any(IMediaType.class), anyInt(), eq(parameters));
		return this;
	}

	public HttpClientMockDirector verifyCreateApplication(String domainId, int timeout, Parameter... parameters)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		verify(client).post(
				urlEndsWith("/domains/" + domainId + "/applications?include=cartridges"), 
				any(IMediaType.class), eq(timeout), eq(parameters));
		return this;
	}

	public IHttpClient client() {
		return client;
	}

	public IDomain getDomain(String string) throws OpenShiftException, FileNotFoundException, IOException {
		IUser user = new TestConnectionFactory().getConnection(client).getUser();
		return user.getDomain("foobarz");
	}
	
	public HttpClientMockDirector mockAddEnvironmentVariable(String domainId, String applicationName,
			Samples addEnvironmentVariableRequestResponse)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		when(client.post(urlEndsWith("/domains/" + domainId + "/applications/" + applicationName + "/environment-variables"), 
				any(IMediaType.class), anyInt(), Matchers.<Parameter[]>anyVararg()))
				.thenReturn(addEnvironmentVariableRequestResponse.getContentAsString());
		return this;
	}
	
	public HttpClientMockDirector mockGetEnvironmentVariables(String domainId, String applicationName,
			Samples getZeroEnvironmentVariableRequestResponse, Samples getOneEnvironmentVariableRequestResponse)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		when(client.get(urlEndsWith("/domains/" + domainId + "/applications/" + applicationName + "/environment-variables"), 
				anyInt()))
		.thenReturn(getZeroEnvironmentVariableRequestResponse.getContentAsString(), getOneEnvironmentVariableRequestResponse.getContentAsString());
		return this;
	}

	public HttpClientMockDirector mockGetEnvironmentVariables(String domainId, String applicationName,
			Samples getOneEnvironmentVariableRequestResponse)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		when(client.get(urlEndsWith("/domains/" + domainId + "/applications/" + applicationName + "/environment-variables"), 
				anyInt()))
		.thenReturn(getOneEnvironmentVariableRequestResponse.getContentAsString(),getOneEnvironmentVariableRequestResponse.getContentAsString());
		return this;
	}
	
	public HttpClientMockDirector mockUpdateEnvironmentVariableValue(String domainId, String applicationName,
			String environmentName, Samples updateEnvironmentVariableValue)
			throws SocketTimeoutException, HttpClientException, EncodingException {
		when(client.put(urlEndsWith("/domains/" + domainId + "/applications/" + applicationName + "/environment-variables/"+ environmentName),
						any(IMediaType.class), anyInt(), Matchers.<Parameter[]> anyVararg()))
		.thenReturn(updateEnvironmentVariableValue.getContentAsString());
		return this;
	}

	public HttpClientMockDirector mockGetQuickstarts(Samples getQuickstartsResponse) 
			throws SocketTimeoutException, HttpClientException {
		when(client.get(urlEndsWith("api/v1/quickstarts/promoted.json"), anyInt()))
		.thenReturn(getQuickstartsResponse.getContentAsString());
		return this;
	}

}
