/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.restclient;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.net.URL;

import org.junit.Before;
import org.mockito.Mock;

import com.openshift.restclient.IApiTypeMapper;
import com.openshift.restclient.http.IHttpClient;
import com.openshift.restclient.utils.Samples;

public class TypeMapperFixture {
	
	protected static final String VERSIONS = "{ \"versions\": [\"v1\"]}";
	protected static final String base = "https://localhost:8443";
	
	@Mock
	private IHttpClient client;
	protected IApiTypeMapper mapper;
	
	protected IApiTypeMapper getApiTypeMapper() {
		return mapper;
	}
	
	protected IHttpClient getHttpClient() {
		return client;
	}
	
	@Before
	public void setUp() throws Exception {
		when(client.get(eq(new URL(base + "/api")), anyInt())).thenReturn(VERSIONS);
		when(client.get(eq(new URL(base + "/oapi")), anyInt())).thenReturn(VERSIONS);
		when(client.get(eq(new URL(base + "/apis")), anyInt())).thenReturn(Samples.GROUP_ENDPONT_APIS.getContentAsString());
		when(client.get(eq(new URL(base + "/api/v1")), anyInt())).thenReturn(Samples.GROUP_ENDPONT_API_V1.getContentAsString());
		when(client.get(eq(new URL(base + "/oapi/v1")), anyInt())).thenReturn(Samples.GROUP_ENDPONT_OAPI_V1.getContentAsString());
		when(client.get(eq(new URL(base + "/apis/extensions/v1beta1")), anyInt())).thenReturn(Samples.GROUP_ENDPONT_APIS_EXTENSIONS.getContentAsString());
		mapper = new ApiTypeMapper(base, client);
	}
}
