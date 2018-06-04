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

package com.openshift.internal.restclient.capability.resources;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.BufferedInputStream;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.restclient.IClient;
import com.openshift.restclient.PredefinedResourceKind;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.capability.IBinaryCapability;
import com.openshift.restclient.capability.IBinaryCapability.SkipTlsVerify;
import com.openshift.restclient.capability.resources.IPodLogRetrieval;
import com.openshift.restclient.model.IPod;
import com.openshift.restclient.model.IResource;

public class OpenshiftBinaryPodLogRetrievalIntegrationTest {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftBinaryPodLogRetrievalIntegrationTest.class);
    private IntegrationTestHelper helper = new IntegrationTestHelper();
    private Exception ex;

    @Test
    public void testLogRetrieval() {
        System.setProperty(IBinaryCapability.OPENSHIFT_BINARY_LOCATION, helper.getOpenShiftLocation());
        IClient client = helper.createClientForBasicAuth();
        List<IResource> pods = client.list(PredefinedResourceKind.POD.getIdentifier(), "default");
        IPod pod = (IPod) pods.stream().filter(p -> p.getName().startsWith("docker-registry")).findFirst().orElse(null);
        assertNotNull("Need a pod to continue the test. Expected to find the registry", pod);

        ex = pod.accept(new CapabilityVisitor<IPodLogRetrieval, Exception>() {

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
                    LOG.error("There was an error:", e);
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
