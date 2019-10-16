/*******************************************************************************
 * Copyright (c) 2015-2019 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.internal.restclient.capability.resources;

import static org.junit.Assert.assertNotNull;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.jboss.dmr.ModelNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.internal.restclient.PodStatusRunningConditional;
import com.openshift.internal.restclient.model.Port;
import com.openshift.internal.restclient.model.properties.ResourcePropertyKeys;
import com.openshift.restclient.IClient;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.capability.IBinaryCapability;
import com.openshift.restclient.capability.IBinaryCapability.SkipTlsVerify;
import com.openshift.restclient.capability.resources.IPortForwardable;
import com.openshift.restclient.capability.resources.IPortForwardable.PortPair;
import com.openshift.restclient.model.IPod;
import com.openshift.restclient.model.IProject;

public class OpenShiftBinaryPortForwardingIntegrationTest2 implements ResourcePropertyKeys {

    private IntegrationTestHelper helper = new IntegrationTestHelper();
    private IClient client;
    private IProject project;
    private IPod pod;

    @Before
    public void setUp() throws Exception {
        System.setProperty(IBinaryCapability.OPENSHIFT_BINARY_LOCATION, helper.getOpenShiftLocation());
        this.client = helper.createClientForBasicAuth();
        this.project = helper.getOrCreateIntegrationTestProject(client);
        IPod pod = helper.getOrCreatePod(client, project.getNamespaceName(), IntegrationTestHelper.IMAGE_HELLO_OPENSHIFT);
        this.pod = helper.waitForResource(
                client, 
                pod,
                5 * IntegrationTestHelper.MILLISECONDS_PER_MIN, 
                new PodStatusRunningConditional());

    }

    @After
    public void teardown() throws Exception {
        helper.cleanUpResource(client, pod);
    }

    @Test
    public void testPortForwarding() {
        assertNotNull("The test timed out before the pod was in a running state", pod);

        final Port port = new Port(new ModelNode());
        port.setProtocol("tcp");
        port.setContainerPort(8080);
        pod.accept(new CapabilityVisitor<IPortForwardable, Object>() {

            @Override
            public Object visit(IPortForwardable capability) {
                capability.forwardPorts(Arrays.asList(new PortPair(8181, port)), new SkipTlsVerify());
                try {
                    Thread.sleep(5 * IntegrationTestHelper.MILLISECONDS_PER_SECOND);
                    curl();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                capability.stop();
                return null;
            }
        }, new Object());
    }

    private void curl() throws Exception {
        URL url = new URL("http://localhost:8181");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setDoInput(true);
        con.connect();
        System.out.println(IOUtils.toString(con.getInputStream()));
        con.disconnect();
    }
}
