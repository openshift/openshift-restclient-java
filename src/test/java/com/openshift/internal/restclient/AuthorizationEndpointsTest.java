/*******************************************************************************
 * Copyright (c) 2019 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************
 */

package com.openshift.internal.restclient;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;

import org.junit.Test;

import com.openshift.restclient.utils.Samples;

public class AuthorizationEndpointsTest extends TypeMapperFixture {

    @Test
    public void shouldRetrieveAuthorizationEndpoint() throws Exception {
        getHttpClient().whenRequestTo(base + "/.well-known/oauth-authorization-server",
                responseOf(Samples.WELL_KNOW_OAUTH_AUTHORIZATION_SERVER.getContentAsString()));

        URL authorizationEndpoint = getIClient().getAuthorizationEndpoint();
        assertThat(authorizationEndpoint).isNotNull();
        assertThat(authorizationEndpoint.toExternalForm()).isEqualTo(
                "https://api.rh-us-east-1.openshift.com/oauth/authorize");
    }

    @Test
    public void shouldRetrieveTokenEndpoint() throws Exception {
        getHttpClient().whenRequestTo(base + "/.well-known/oauth-authorization-server",
                responseOf(Samples.WELL_KNOW_OAUTH_AUTHORIZATION_SERVER.getContentAsString()));

        URL tokenEndpoint = getIClient().getTokenEndpoint();
        assertThat(tokenEndpoint).isNotNull();
        assertThat(tokenEndpoint.toExternalForm()).isEqualTo(
                "https://api.rh-us-east-1.openshift.com/oauth/token");
    }

    @Test
    public void shouldReturnNullifAuthorizationEndpoint404() throws Exception {
        getHttpClient().whenRequestTo(base + "/.well-known/oauth-authorization-server",
                responseOf(404, "something wrong"));

        assertThat(getIClient().getAuthorizationEndpoint()).isEqualTo(null);
        assertThat(getIClient().getTokenEndpoint()).isEqualTo(null);
    }

    @Test
    public void shouldReturnNullifAuthorizationEndpointInvalidJson() throws Exception {
        getHttpClient().whenRequestTo(base + "/.well-known/oauth-authorization-server",
                responseOf("{bogus"));

        assertThat(getIClient().getAuthorizationEndpoint()).isEqualTo(null);
        assertThat(getIClient().getTokenEndpoint()).isEqualTo(null);
    }

}
