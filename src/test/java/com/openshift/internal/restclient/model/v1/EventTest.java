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

package com.openshift.internal.restclient.model.v1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.openshift.internal.restclient.model.KubernetesEvent;
import com.openshift.restclient.IClient;
import com.openshift.restclient.model.IEvent;
import com.openshift.restclient.model.IEvent.IEventSource;
import com.openshift.restclient.model.IObjectReference;
import com.openshift.restclient.utils.Samples;

@RunWith(MockitoJUnitRunner.class)
public class EventTest {

    private static String JSON = Samples.V1_EVENT.getContentAsString();

    @Mock
    private IClient client;
    private IEvent event;

    @Before
    public void setUp() throws Exception {
        ModelNode node = ModelNode.fromJSONString(JSON);
        event = new KubernetesEvent(node, client, new HashMap<>());
    }

    @Test
    public void testGetEventSource() {
        IEventSource source = event.getEventSource();
        assertNotNull(source);
        assertEquals("deploymentconfig-controller", source.getComponent());
        assertEquals("", source.getHost());
    }

    @Test
    public void testGetType() {
        assertEquals("Normal", event.getType());
    }

    @Test
    public void testGetCount() {
        assertEquals(1, event.getCount());
    }

    @Test
    public void testGetFirstSeen() {
        assertEquals("2016-08-08T01:49:26Z", event.getFirstSeenTimestamp());
    }

    @Test
    public void testGetLastSeen() {
        assertEquals("2016-08-08T01:49:26Z", event.getLastSeenTimestamp());
    }

    @Test
    public void testGetReason() {
        assertEquals("DeploymentCreated", event.getReason());
    }

    @Test
    public void testGetInvolvedObject() {
        IObjectReference ref = event.getInvolvedObject();
        assertNotNull(ref);
    }

    @Test
    public void testGetMessage() {
        assertEquals("Created new deployment \"nodejs-1\" for version 1", event.getMessage());
    }

}
