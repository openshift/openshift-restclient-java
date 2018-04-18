/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.internal.restclient.okhttp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.DefaultClient;
import com.openshift.restclient.ClientBuilder;

import okhttp3.OkHttpClient;

/**
 * @author Vlad Slepukhin
 */
public class ClientProxyConnectionTest {

    private static final Logger LOG = LoggerFactory.getLogger(ClientProxyConnectionTest.class);


    private static final String OPENSHIFT_URL = "https://openshift.test.com";
    private static final String PROXY_HOST = "127.0.0.1";
    private static final int PROXY_PORT = 8080;

    private class TestProxySelector extends ProxySelector {

        @Override
        public List<Proxy> select(URI uri) {
            List<Proxy> result = new ArrayList<>();
            result.add(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT)));
            return result;
        }

        @Override
        public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        }
    }

    @Test
    public void testProxySetInOkHttp() throws NoSuchFieldException, IllegalAccessException {
        Proxy expectedProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));
        DefaultClient client = (DefaultClient) new ClientBuilder(OPENSHIFT_URL)
                .proxy(expectedProxy)
                .build();

        OkHttpClient okClient = getOkHttpClient(client);
        Proxy proxyInOkClient = okClient.proxy();

        LOG.info(proxyInOkClient.toString());

        assertEquals(expectedProxy.type(), proxyInOkClient.type());
        assertEquals(expectedProxy.address(), proxyInOkClient.address());
    }

    @Test
    public void testProxyNotSetInOkHttp() throws NoSuchFieldException, IllegalAccessException {
        DefaultClient client = (DefaultClient) new ClientBuilder(OPENSHIFT_URL)
                .build();

        OkHttpClient okClient = getOkHttpClient(client);
        Proxy proxyInOkClient = okClient.proxy();

        assertNull(proxyInOkClient);
    }

    @Test
    public void testProxySelectorSetInOkHttp() throws NoSuchFieldException, IllegalAccessException {
        ProxySelector expectedSelector = new TestProxySelector();
        DefaultClient client = (DefaultClient) new ClientBuilder(OPENSHIFT_URL)
                .proxySelector(expectedSelector)
                .build();


        OkHttpClient okClient = getOkHttpClient(client);
        ProxySelector proxySelector = okClient.proxySelector();

        assertNotSame(proxySelector, ProxySelector.getDefault());
        assertEquals(expectedSelector, proxySelector);

        Proxy expectedProxyFromList = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));
        Proxy actualProxyFromList = proxySelector.select(null).get(0);
        assertEquals(expectedProxyFromList.type(), actualProxyFromList.type());
        assertEquals(expectedProxyFromList.address(), actualProxyFromList.address());
    }

    @Test
    public void testProxySelectorNotSetInOkHttp() throws NoSuchFieldException, IllegalAccessException {
        DefaultClient client = (DefaultClient) new ClientBuilder(OPENSHIFT_URL)
                .build();

        OkHttpClient okClient = getOkHttpClient(client);
        ProxySelector proxySelector = okClient.proxySelector();

        assertEquals(proxySelector, ProxySelector.getDefault());
    }


    private OkHttpClient getOkHttpClient(DefaultClient client) throws NoSuchFieldException, IllegalAccessException {
        Field clientField = client.getClass().getDeclaredField("client");
        clientField.setAccessible(true);
        return (OkHttpClient) clientField.get(client);
    }
}
