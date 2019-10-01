/*******************************************************************************
 * Copyright (c) 2019 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.capability.server;

import static org.fest.assertions.Assertions.assertThat;

import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.restclient.IClient;
import com.openshift.restclient.capability.server.IConsole;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ConsoleIntegrationTest {
    private IntegrationTestHelper helper = new IntegrationTestHelper();
    private IClient client;
    private IConsole console;
    private OkHttpClient httpClient;

    @Before
    public void before() throws MalformedURLException {
        this.client = helper.createClientForBasicAuth();
        this.console = client.adapt(IConsole.class);
        this.httpClient = helper.createTrustAllOkHttpClient();
    }

    @Test
    public void shouldRetrieveValidConsoleUrl() throws Exception {
        // given
        // when
        String consoleUrl = console.getConsoleUrl();
        // then
        assertThat(consoleUrl).isNotNull();
        Request request = new Request.Builder().url(consoleUrl).build();
        Response response = httpClient.newCall(request).execute();
        assertThat(response.isSuccessful()).isTrue();
    }
}
