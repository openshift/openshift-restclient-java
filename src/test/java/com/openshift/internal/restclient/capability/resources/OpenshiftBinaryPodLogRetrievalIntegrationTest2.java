/*******************************************************************************
 * Copyright (c) 2015-2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.internal.restclient.capability.resources;

import static org.junit.Assert.assertNull;

import java.io.BufferedInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.internal.restclient.PodStatusRunningConditional;
import com.openshift.restclient.IClient;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.capability.IBinaryCapability.SkipTlsVerify;
import com.openshift.restclient.capability.resources.IPodLogRetrieval;
import com.openshift.restclient.model.IPod;
import com.openshift.restclient.model.IProject;

public class OpenshiftBinaryPodLogRetrievalIntegrationTest2 {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftBinaryPodLogRetrievalIntegrationTest2.class);
    private IntegrationTestHelper helper = new IntegrationTestHelper();
    private IClient client;
    private IProject project;
    private IPod pod;

    @Before
    public void before() {
        helper.setOpenShiftBinarySystemProperty();
        this.client = helper.createClientForBasicAuth();
        this.project = helper.getOrCreateIntegrationTestProject(client);
        IPod pod = helper.createPod(client, project.getNamespaceName(), IntegrationTestHelper.appendRandom("test-pod"));
        this.pod = helper.waitForResource(
                client, 
                pod,
                5 * IntegrationTestHelper.MILLISECONDS_PER_MIN, 
                new PodStatusRunningConditional());
    }

    @After
    public void after() {
        helper.cleanUpResource(client, pod);
    }

    @Test
    public void testLogRetrieval() {
        Exception ex = pod.accept(new CapabilityVisitor<IPodLogRetrieval, Exception>() {

            @Override
            public Exception visit(IPodLogRetrieval cap) {
                StringBuilder builder = new StringBuilder();
                try {
                    BufferedInputStream os = new BufferedInputStream(cap.getLogs(false, new SkipTlsVerify()));
                    int c;
                    while ((c = os.read()) != -1) {
                        builder.append((char) c);
                    }
                } catch (Exception e) {
                    LOG.error("There was an error: ", e);
                    return e;
                } finally {
                    LOG.info(builder.toString());
                    cap.stop();
                }
                return null;
            }

        }, null);

        assertNull("Expected no exception", ex);
    }
}
