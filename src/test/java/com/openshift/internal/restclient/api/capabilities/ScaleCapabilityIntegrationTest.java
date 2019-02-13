/******************************************************************************* 
 * Copyright (c) 2016-2019 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package com.openshift.internal.restclient.api.capabilities;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.internal.restclient.PodStatusRunningConditional;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IOpenShiftWatchListener;
import com.openshift.restclient.IWatcher;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.api.capabilities.IScalable;
import com.openshift.restclient.apis.autoscaling.models.IScale;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.model.IPod;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.IReplicationController;
import com.openshift.restclient.model.IResource;

public class ScaleCapabilityIntegrationTest {

    private static final int REPLICAS = 2;
    private IntegrationTestHelper helper = new IntegrationTestHelper();
    private IClient client;
    private IProject project;
    private IWatcher watch;
    private CountDownLatch initializationLatch = new CountDownLatch(2);
    private CountDownLatch replicaLatch = new CountDownLatch(1);
    private IReplicationController rc;
    private AtomicBoolean foundFirstPod = new AtomicBoolean(false);
    private PodStatusRunningConditional conditional = new PodStatusRunningConditional();

    @Before
    public void setUp() throws Exception {
        this.client = helper.createClientForBasicAuth();
        this.project = helper.getOrCreateIntegrationTestProject(client);
    }

    @After
    public void teardown() throws Exception {
        helper.stopWatcher(watch);
        helper.cleanUpResource(client, rc);
        // remove all pods that were created by the rc/dc
        helper.cleanUpResource(client, client.get(ResourceKind.POD, project.getNamespaceName()));
    }

    @Test(timeout = IntegrationTestHelper.TEST_LONG_TIMEOUT)
    public void testScalingReplicationController() throws Exception {
        this.rc = client.create(helper.stubReplicationController(client, 
                project.getNamespaceName(), IntegrationTestHelper.appendRandom("test-rc")));
        runTest();
    }

    @Test(timeout = IntegrationTestHelper.TEST_LONG_TIMEOUT)
    public void testScalingDeploymentConfig() throws Exception {
        this.rc = client.create(helper.stubDeploymentConfig(client,
                project.getNamespaceName(), IntegrationTestHelper.appendRandom("test-dc")));
        runTest();
    }

    private void runTest() throws Exception {
        this.watch = client.watch(project.getName(), new IOpenShiftWatchListener() {

            @Override
            public void connected(List<IResource> resources) {
                initializationLatch.countDown();
            }

            @Override
            public void disconnected() {
            }

            @Override
            public void received(IResource resource, ChangeType change) {
                if (!(resource instanceof IPod)
                        || helper.isDeployPod(((IPod) resource))) {
                    return;
                }
                if (ChangeType.MODIFIED.equals(change) 
                        && conditional.isReady(resource)) {
                    if (foundFirstPod.get()) {
                        replicaLatch.countDown();
                    } else {
                        foundFirstPod.set(true);
                        initializationLatch.countDown();
                    }
                }
            }

            @Override
            public void error(Throwable err) {
            }

        }, ResourceKind.POD);

        if (initializationLatch.await(1, TimeUnit.MINUTES)) {
            scaleTo(REPLICAS);
            assertTrue("The pods either did not scale to " + REPLICAS + " replicas or the test timed out",
                    replicaLatch.await(2, TimeUnit.MINUTES));
        }
    }

    private void scaleTo(int replicas) {
        IScale result = rc.accept(new CapabilityVisitor<IScalable, IScale>() {

            @Override
            public IScale visit(IScalable capability) {
                return capability.scaleTo(replicas);
            }
        }, null);
        assertNotNull("Exp. to receive a non-null result", result);
    }
}
