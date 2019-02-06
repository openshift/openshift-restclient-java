/*******************************************************************************
 * Copyright (c) 2014-2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.internal.restclient;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;

import com.openshift.internal.restclient.DefaultClient.HttpMethod;
import com.openshift.internal.restclient.authorization.AuthorizationContext;
import com.openshift.internal.restclient.model.Pod;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.api.ITypeFactory;
import com.openshift.restclient.model.JSONSerializeable;

import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okio.Buffer;

/**
 * @author Jeff Cantrill
 * @author Andre Dietisheim
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultClientTest extends TypeMapperFixture {

    private static final String VERSION = "v1";

    private DefaultClient client;
    private ModelNode response;
    private Pod podFrontEnd;
    private Pod podBackEnd;
    private IResourceFactory factory;
    private URL baseUrl;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.baseUrl = new URL("http://myopenshift");
        givenAClient();
        givenAPodList();
        getHttpClient().whenRequestTo(TypeMapperFixture.base + "/api/v1/namespaces/aNamespace/pods")
                .thenReturn(responseOf(response.toJSONString(false)));
    }

    private void givenAClient() throws Exception {
        factory = new ResourceFactory(null);
        client = (DefaultClient) getIClient();
        factory = client.getResourceFactory();
    }

    private void givenAPodList() {
        this.podFrontEnd = factory.create(VERSION, ResourceKind.POD);
        podFrontEnd.setName("frontend");
        podFrontEnd.setNamespace("aNamespace");
        podFrontEnd.addLabel("name", "frontend");
        podFrontEnd.addLabel("env", "production");

        this.podBackEnd = factory.create(VERSION, ResourceKind.POD);
        podBackEnd.setName("backend");
        podBackEnd.setNamespace("aNamespace");
        podBackEnd.addLabel("name", "backend");
        podBackEnd.addLabel("env", "production");

        Pod otherPod = factory.create(VERSION, ResourceKind.POD);
        otherPod.setName("other");
        otherPod.setNamespace("aNamespace");
        otherPod.addLabel("env", "production");

        this.response = new ModelNode();
        response.get("apiVersion").set(VERSION);
        response.get("kind").set("PodList");
        ModelNode items = response.get("items");
        items.add(podFrontEnd.getNode());
        items.add(otherPod.getNode());
        items.add(podBackEnd.getNode());
    }

    private DefaultClient givenClient(URL baseUrl, String token, String user) {
        DefaultClient client = new DefaultClient(baseUrl, getHttpClient(), null, null,
                new AuthorizationContext(token, user, null));
        return client;
    }

    private ITypeFactory givenTypeFactory() {
        return mock(ITypeFactory.class);
    }

    private JSONSerializeable givenJsonPayload(String string) {
        JSONSerializeable json = mock(JSONSerializeable.class);
        when(json.toJson(anyBoolean())).thenReturn(string);
        return json;
    }

    @Test
    public void clientShouldEqualClientWithSameUrl() throws Exception {
        assertThat(givenClient(baseUrl, null, null)).isEqualTo(givenClient(baseUrl, null, null));
    }

    @Test
    public void clientShouldNotEqualClientWithDifferentUrl() throws Exception {
        assertThat(givenClient(baseUrl, null, null))
                .isNotEqualTo(givenClient(new URL("http://localhost:8443"), null, null));
    }

    @Test
    public void client_should_equal_client_with_same_user_with_different_token() throws Exception {
        DefaultClient tokenClientOne = givenClient(baseUrl, "tokenOne", "aUser");

        DefaultClient tokenClientTwo = givenClient(baseUrl, "tokenTwo", "aUser");

        assertThat(tokenClientOne).isEqualTo(tokenClientTwo);
    }

    @Test
    public void client_should_not_equal_client_with_different_username() throws Exception {
        DefaultClient tokenClientOne = givenClient(baseUrl, "aToken", "aUser");

        DefaultClient tokenClientTwo = givenClient(baseUrl, "aToken", "differentUser");

        assertThat(tokenClientOne).isNotEqualTo(tokenClientTwo);
    }
    
    @Test
    public void should_use_paylod_in_delete_request() throws IOException {
        // given
        DefaultClient client = spy(this.client);

        Builder builder = givenRequestBuilder(client);
        ArgumentCaptor<RequestBody> builderCaptor = ArgumentCaptor.forClass(RequestBody.class);
        String payload = "{prop1:\"val1\"}";

        // when
        client.execute(givenTypeFactory(), HttpMethod.DELETE.toString(), 
                ResourceKind.BUILD, null, null, null, null, 
                givenJsonPayload(payload), null);

        // then
        String requestBodyPayload = getPayload(builder, builderCaptor);
        assertThat(requestBodyPayload).isEqualTo(payload);
    }

    @Test
    public void should_use_paylod_in_post_request() throws IOException {
        // given
        DefaultClient client = spy(this.client);

        Builder builder = givenRequestBuilder(client);
        ArgumentCaptor<RequestBody> builderCaptor = ArgumentCaptor.forClass(RequestBody.class);
        String payload = "{prop1:\"val1\"}";

        // when
        client.execute(givenTypeFactory(), HttpMethod.POST.toString(), 
                ResourceKind.BUILD, null, null, null, null, 
                givenJsonPayload(payload), null);

        // then
        String requestBodyPayload = getPayload(builder, builderCaptor);
        assertThat(requestBodyPayload).isEqualTo(payload);
    }

    @Test
    public void should_use_paylod_in_put_request() throws IOException {
        // given
        DefaultClient client = spy(this.client);

        Builder builder = givenRequestBuilder(client);
        ArgumentCaptor<RequestBody> builderCaptor = ArgumentCaptor.forClass(RequestBody.class);
        String payload = "{prop1:\"val1\"}";

        // when
        client.execute(givenTypeFactory(), HttpMethod.PUT.toString(), 
                ResourceKind.BUILD, null, null, null, null, 
                givenJsonPayload(payload), null);

        // then
        String requestBodyPayload = getPayload(builder, builderCaptor);
        assertThat(requestBodyPayload).isEqualTo(payload);
    }

    @Test
    public void should_not_use_paylod_in_get_request() throws IOException {
        // given
        DefaultClient client = spy(this.client);

        Builder builder = givenRequestBuilder(client);
        ArgumentCaptor<RequestBody> builderCaptor = ArgumentCaptor.forClass(RequestBody.class);

        // when
        client.execute(givenTypeFactory(), HttpMethod.GET.toString(), 
                ResourceKind.BUILD, null, null, null, null, 
                givenJsonPayload("{prop1:\"val1\"}"), null);

        // then
        verify(builder).method(anyString(), builderCaptor.capture());
        assertThat(builderCaptor.getValue()).isNull();
    }

    @Test
    public void should_not_use_paylod_in_head_request() throws IOException {
        // given
        DefaultClient client = spy(this.client);

        Builder builder = givenRequestBuilder(client);
        ArgumentCaptor<RequestBody> builderCaptor = ArgumentCaptor.forClass(RequestBody.class);

        // when
        client.execute(givenTypeFactory(), HttpMethod.HEAD.toString(), 
                ResourceKind.BUILD, null, null, null, null, 
                givenJsonPayload("{prop1:\"val1\"}"), null);

        // then
        verify(builder).method(anyString(), builderCaptor.capture());
        assertThat(builderCaptor.getValue()).isNull();
    }

    private String getPayload(Builder builder, ArgumentCaptor<RequestBody> builderCaptor) throws IOException {
        verify(builder).method(anyString(), builderCaptor.capture());
        RequestBody requestBody = builderCaptor.getValue();
        assertThat(requestBody).isNotNull();
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        buffer.copyTo(out);
        String requestBodyPayload = new String(out.toByteArray());
        return requestBodyPayload;
    }

    private Builder givenRequestBuilder(DefaultClient client) {
        Builder builder = spy(new Request.Builder().url(this.baseUrl));
        doReturn(builder).when(client).newRequestBuilderTo(anyString(), anyString());
        return builder;
    }

}
