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

package com.openshift.internal.restclient.capability.resources;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.openshift.internal.restclient.DefaultClient;
import com.openshift.internal.restclient.TypeMapperFixture;
import com.openshift.internal.restclient.capability.resources.PodLogRetrievalAsync.PodLogListenerAdapter;
import com.openshift.restclient.IApiTypeMapper;
import com.openshift.restclient.capability.resources.IPodLogRetrievalAsync.IPodLogListener;
import com.openshift.restclient.http.IHttpConstants;
import com.openshift.restclient.model.IPod;
import com.openshift.restclient.model.MocksFactory;

import okhttp3.WebSocket;

@RunWith(MockitoJUnitRunner.class)
public class PodLogRetrievalAsyncTest extends TypeMapperFixture {

    private DefaultClient client;
    @Mock
    private IApiTypeMapper mapper;
    private PodLogRetrievalAsync capability;
    private IPod pod;
    private PodLogListenerAdapter adapter;

    @Mock
    private IPodLogListener listener;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        client = new DefaultClient(new URL("http://localhost"), getHttpClient(), null, getApiTypeMapper(), null);
        pod = new MocksFactory().mock(IPod.class);
        capability = new PodLogRetrievalAsync(pod, client);

        adapter = new PodLogListenerAdapter(listener);
    }

    @Test
    public void testIsSupported() {
        assertTrue("Exp. capability to be supported because the pod endpoint exists", capability.isSupported());
    }

    @Test
    public void testIsNotSupportedWhenEndpointDoesNotExist() {
        when(pod.getApiVersion()).thenReturn("somenoneexitentversion");
        assertFalse("Exp. capability to not be supported because the pod endpoint does not exist exists",
                capability.isSupported());
    }

    @Test
    public void testAdapterCallsListenerCycle() throws Exception {
        adapter.onOpen(null, null);
        adapter.onOpen(null, null);
        verify(listener).onOpen();

        adapter.onMessage(null, "a body");
        verify(listener).onMessage("a body");

        adapter.onClosing(null, 1986, "the reason");
        adapter.onClosing(null, 1986, "the reason");
        verify(listener).onClose(1986, "the reason");
    }

    @Test
    public void testStopWhenConnected() throws Exception {
        WebSocket socket = mock(WebSocket.class);
        adapter.onOpen(socket, null);

        adapter.stop();
        verify(socket).close(eq(IHttpConstants.STATUS_NORMAL_STOP), anyString());
    }

    @Test
    public void testStopSwallowsException() throws Exception {
        WebSocket socket = mock(WebSocket.class);
        adapter.onOpen(socket, null);

        adapter.stop();
        verify(socket).close(eq(IHttpConstants.STATUS_NORMAL_STOP), anyString());
    }

}
