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

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.URL;

import org.junit.Before;
import org.mockito.ArgumentMatcher;
import org.mockito.stubbing.OngoingStubbing;

import com.openshift.restclient.IApiTypeMapper;
import com.openshift.restclient.IClient;
import com.openshift.restclient.http.IHttpConstants;
import com.openshift.restclient.utils.Samples;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TypeMapperFixture {
	
	protected static final String VERSIONS = "{ \"versions\": [\"v1\"]}";
	protected static final String base = "https://localhost:8443";
	private static final String ANY = "--any--";
	private TestOkHttpClient client = spy(new TestOkHttpClient());
	
	protected IApiTypeMapper mapper;
	
	protected IApiTypeMapper getApiTypeMapper() {
		return mapper;
	}
	
	protected TestOkHttpClient getHttpClient() {
		return client;
	}
	
	protected IClient getIClient() throws Exception {
	    return new DefaultClient(new URL(base), client, new ResourceFactory(null), mapper, null);
	}
	
	@Before
	public void setUp() throws Exception {
		client.whenRequestTo(ANY).thenReturn(responseOf(""));
		client.whenRequestTo(base + "/api").thenReturn(responseOf(VERSIONS));
		client.whenRequestTo(base + "/oapi").thenReturn(responseOf(VERSIONS));
		client.whenRequestTo(base + "/apis").thenReturn(responseOf(Samples.GROUP_ENDPONT_APIS.getContentAsString()));
		client.whenRequestTo(base + "/api/v1").thenReturn(responseOf(Samples.GROUP_ENDPONT_API_V1.getContentAsString()));
		client.whenRequestTo(base + "/oapi/v1").thenReturn(responseOf(Samples.GROUP_ENDPONT_OAPI_V1.getContentAsString()));
		client.whenRequestTo(base + "/apis/extensions/v1beta1").thenReturn(responseOf(Samples.GROUP_ENDPONT_APIS_EXTENSIONS.getContentAsString()));
		mapper = new ApiTypeMapper(base, client);
	}
	
	static class TestOkHttpClient extends OkHttpClient{
		
		OngoingStubbing<Response> whenRequestTo(String url) throws IOException{
			Call call = mock(Call.class);
			doReturn(call).when(this).newCall(requestTo(url));
			return when(call.execute());
		}
		
	}
	
	static Request requestTo(String url) {
		return argThat(new RequestMatcher(url));
	}
	
	protected static Response responseOf(String response) {
		return new Response.Builder()
				.request(new Request.Builder().url("https://someurlfortesting").build())
				.protocol(Protocol.HTTP_1_1)
				.code(IHttpConstants.STATUS_OK)
				.body(ResponseBody.create(null, response))
				.build();
	}
	
	static class RequestMatcher extends ArgumentMatcher<Request>{
		
		private final String url;

		public RequestMatcher(String url) {
			this.url = url;
		}

		@Override
		public boolean matches(Object argument) {
			if(ANY.equals(this.url)) return true;
			if(argument == null || !(argument instanceof Request))
				return false;
			return ((Request)argument).url().toString().equals(url);
		}
		
	}
}
