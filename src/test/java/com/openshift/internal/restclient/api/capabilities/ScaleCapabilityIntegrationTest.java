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
    private IReplicationController dc;
    private AtomicBoolean foundFirstPod = new AtomicBoolean(false);
    private PodStatusRunningConditional conditional = new PodStatusRunningConditional();

    @Before
    public void setUp() throws Exception {
        client = helper.createClientForBasicAuth();
        project = helper.generateProject(client);
    }

    @After
    public void teardown() throws Exception {
        if (watch != null) {
            try {
                watch.stop();
            } catch (Exception e) {
                // swallow
            }
        }
        IntegrationTestHelper.cleanUpResource(client, project);
    }

    @Test(timeout = 3 * 1000 * 60)
    public void testScalingReplicationController() throws Exception {
        dc = client.create(IntegrationTestHelper.stubReplicationController(client, project));
        runTest();
    }

    @Test(timeout = 3 * 1000 * 60)
    public void testScalingDeploymentConfig() throws Exception {
        dc = client.create(IntegrationTestHelper.stubDeploymentConfig(client, project));
        runTest();
    }

    private void runTest() throws Exception {
        watch = client.watch(project.getName(), new IOpenShiftWatchListener() {

            @Override
            public void connected(List<IResource> resources) {
                initializationLatch.countDown();
            }

            @Override
            public void disconnected() {

            }

            @Override
            public void received(IResource resource, ChangeType change) {
                if (ChangeType.MODIFIED.equals(change) && !resource.getName().endsWith("deploy")
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
            assertTrue("The pods either did not scale as expected or the test timed out",
                    replicaLatch.await(2, TimeUnit.MINUTES));
        }
        ;

    }

    private void scaleTo(int replicas) {
        IScale result = dc.accept(new CapabilityVisitor<IScalable, IScale>() {

            @Override
            public IScale visit(IScalable capability) {
                return capability.scaleTo(replicas);
            }
        }, null);
        assertNotNull("Exp. to receive a non-null result", result);
    }
}
