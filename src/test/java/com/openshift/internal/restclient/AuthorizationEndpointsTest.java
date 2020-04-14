/*******************************************************************************
 * Copyright (c) 2019-2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 *******************************************************************************/

package com.openshift.internal.restclient;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;

import org.junit.Test;

import com.openshift.internal.restclient.authorization.AuthorizationContext;
import com.openshift.restclient.IApiTypeMapper;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.utils.Samples;

import okhttp3.OkHttpClient;

public class AuthorizationEndpointsTest extends TypeMapperFixture {

    @Test
    public void shouldRetrieveAuthorizationEndpoint() throws Exception {
        getHttpClient().whenRequestTo(base + '/' + AuthorizationEndpoints.PATH_OAUTH_AUTHORIZATION_SERVER,
                responseOf(Samples.WELL_KNOW_OAUTH_AUTHORIZATION_SERVER.getContentAsString()));

        URL authorizationEndpoint = getIClient().getAuthorizationEndpoint();
        assertThat(authorizationEndpoint).isNotNull();
        assertThat(authorizationEndpoint.toExternalForm()).isEqualTo(
                "https://api.rh-us-east-1.openshift.com/oauth/authorize");
    }

    @Test
    public void shouldRetrieveTokenEndpoint() throws Exception {
        getHttpClient().whenRequestTo(base + '/' + AuthorizationEndpoints.PATH_OAUTH_AUTHORIZATION_SERVER,
                responseOf(Samples.WELL_KNOW_OAUTH_AUTHORIZATION_SERVER.getContentAsString()));

        URL tokenEndpoint = getIClient().getTokenEndpoint();
        assertThat(tokenEndpoint).isNotNull();
        assertThat(tokenEndpoint.toExternalForm()).isEqualTo(
                "https://api.rh-us-east-1.openshift.com/oauth/token");
    }

    @Test
    public void shouldReturnNullifAuthorizationEndpoint404AndNoDefault() throws Exception {
        // given
        getHttpClient().whenRequestTo(base + '/' + AuthorizationEndpoints.PATH_OAUTH_AUTHORIZATION_SERVER,
                responseOf(404, "something wrong"));
        DefaultClient client = new DefaultableEndpointsClient(null, null, getIClient());
        // when
        URL authEndpoint = client.getAuthorizationEndpoint();
        URL tokenEndpoint = client.getTokenEndpoint();
        // then
        assertThat(authEndpoint).isEqualTo(null);
        assertThat(tokenEndpoint).isEqualTo(null);
    }

    @Test
    public void shouldReturnDefaultsIfAuthorizationEndpoint404() throws Exception {
        // given
        getHttpClient().whenRequestTo(base + '/' + AuthorizationEndpoints.PATH_OAUTH_AUTHORIZATION_SERVER,
                responseOf(404, "something wrong"));
        URL defaultAuthEndpoint = new URL("https://www.redhat.com");
        URL defaultTokenEndpoint = new URL("https://www.openshift.com");
        DefaultClient client = new DefaultableEndpointsClient(defaultAuthEndpoint, defaultTokenEndpoint, getIClient());
        // when
        URL authEndpoint = client.getAuthorizationEndpoint();
        URL tokenEndpoint = client.getTokenEndpoint();
        // then
        assertThat(authEndpoint).isEqualTo(defaultAuthEndpoint);
        assertThat(tokenEndpoint).isEqualTo(defaultTokenEndpoint);
    }

    @Test
    public void shouldReturnNullifAuthorizationEndpointInvalidJsonAndNoDefault() throws Exception {
        // given
        getHttpClient().whenRequestTo(base + '/' + AuthorizationEndpoints.PATH_OAUTH_AUTHORIZATION_SERVER,
                responseOf("{bogus"));
        DefaultClient client = new DefaultableEndpointsClient(null, null, getIClient());
        // when
        URL authEndpoint = client.getAuthorizationEndpoint();
        URL tokenEndpoint = client.getTokenEndpoint();
        // then
        assertThat(authEndpoint).isEqualTo(null);
        assertThat(tokenEndpoint).isEqualTo(null);
    }

    @Test
    public void shouldReturnDefaultsifAuthorizationEndpointInvalidJson() throws Exception {
        // given
        getHttpClient().whenRequestTo(base + '/' + AuthorizationEndpoints.PATH_OAUTH_AUTHORIZATION_SERVER,
                responseOf("{bogus"));
        URL defaultAuthEndpoint = new URL("https://www.redhat.com");
        URL defaultTokenEndpoint = new URL("https://www.openshift.com");
        DefaultClient client = new DefaultableEndpointsClient(defaultAuthEndpoint, defaultTokenEndpoint, getIClient());
        // when
        URL authEndpoint = client.getAuthorizationEndpoint();
        URL tokenEndpoint = client.getTokenEndpoint();
        // then
        assertThat(authEndpoint).isEqualTo(defaultAuthEndpoint);
        assertThat(tokenEndpoint).isEqualTo(defaultTokenEndpoint);
    }

    private static class DefaultableEndpointsClient extends DefaultClient {

        private URL defaultAuthorizationEndpoint;
        private URL defaultTokenEndpoint;

        public DefaultableEndpointsClient(URL defaultAuthorizationEndpoint, URL defaultTokenEndpoint, IClient client) {
            super(client.getBaseURL(),
                    client.adapt(OkHttpClient.class),
                    client.adapt(IResourceFactory.class),
                    client.adapt(IApiTypeMapper.class),
                    (AuthorizationContext) client.getAuthorizationContext());
            this.defaultAuthorizationEndpoint = defaultAuthorizationEndpoint;
            this.defaultTokenEndpoint = defaultTokenEndpoint;
        }

        @Override
        public URL getDefaultAuthorizationEndpoint() {
            return defaultAuthorizationEndpoint;
        }
        
        @Override
        public URL getDefaultTokenEndpoint() {
            return defaultTokenEndpoint;
        }

    }

}
