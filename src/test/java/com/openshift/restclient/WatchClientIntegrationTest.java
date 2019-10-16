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

package com.openshift.restclient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.restclient.IOpenShiftWatchListener.ChangeType;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.IService;

public class WatchClientIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(WatchClientIntegrationTest.class);

    private IntegrationTestHelper helper = new IntegrationTestHelper();
    private IClient client;
    private IResource project;
    public static final String[] KINDS = new String[] { 
        ResourceKind.BUILD_CONFIG,
        ResourceKind.DEPLOYMENT_CONFIG,
        ResourceKind.SERVICE,
        ResourceKind.POD,
        ResourceKind.REPLICATION_CONTROLLER, 
        ResourceKind.BUILD,
        ResourceKind.IMAGE_STREAM, 
        ResourceKind.ROUTE
        };

    private ExecutorService executor;
    private boolean isError;

    private IService service;

    @Before
    public void setup() {
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.client = helper.createClientForBasicAuth();
        this.project = helper.getOrCreateIntegrationTestProject(client);
        // kill existing service to avoid name clash
    }

    @After
    public void teardown() {
        executor.shutdownNow();
    }

    @Test(timeout = IntegrationTestHelper.TEST_LONG_TIMEOUT)
    public void test() throws Exception {
        List<ChangeType> results = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(KINDS.length);
        IOpenShiftWatchListener listener = new IOpenShiftWatchListener() {

            @Override
            public void received(IResource resource, ChangeType change) {
                results.add(change);
            }

            @Override
            public void connected(List<IResource> resources) {
                latch.countDown();
            }

            @Override
            public void disconnected() {
                latch.countDown();
            }

            @Override
            public void error(Throwable err) {
                latch.countDown();
                isError = true;
                LOG.error("", err);
            }
        };

        IWatcher watcher = null;
        try {
            watcher = client.watch(project.getName(), listener, KINDS);
            latch.await();
            assertFalse("Expected connection without error", isError);
            IService stub = helper.stubService(client,
                    project.getNamespaceName(), 
                    IntegrationTestHelper.appendRandom("hello-openshift"),
                    8787, 8787, 
                    "");
            this.service = client.create(stub);
            this.service.addLabel("foo", "bar");
            this.service = client.update(service);
            client.delete(stub);
            this.service = null;
            assertThat(results).containsExactly(
                    ChangeType.ADDED, 
                    ChangeType.MODIFIED, 
                    ChangeType.DELETED);
            assertEquals(0, latch.getCount());
        } finally {
            helper.stopWatcher(watcher);
        }
    }
}
