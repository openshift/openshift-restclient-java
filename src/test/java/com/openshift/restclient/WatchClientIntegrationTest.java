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

package com.openshift.restclient;

import static com.openshift.internal.restclient.IntegrationTestHelper.cleanUpResource;
import static org.junit.Assert.assertArrayEquals;
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
    public static final String[] KINDS = new String[] { PredefinedResourceKind.BUILD_CONFIG.getIdentifier(),
            PredefinedResourceKind.DEPLOYMENT_CONFIG.getIdentifier(), PredefinedResourceKind.SERVICE.getIdentifier(),
            PredefinedResourceKind.POD.getIdentifier(), PredefinedResourceKind.REPLICATION_CONTROLLER.getIdentifier(),
            PredefinedResourceKind.BUILD.getIdentifier(), PredefinedResourceKind.IMAGE_STREAM.getIdentifier(),
            PredefinedResourceKind.ROUTE.getIdentifier() };

    private ExecutorService service;
    private boolean isError;

    @Before
    public void setup() {
        service = Executors.newSingleThreadScheduledExecutor();
        client = helper.createClientForBasicAuth();
        IResource projRequest = client.getResourceFactory().stub(PredefinedResourceKind.PROJECT_REQUEST.getIdentifier(),
                helper.generateNamespace());
        project = client.create(projRequest);
    }

    @After
    public void teardown() {
        cleanUpResource(client, project);
        service.shutdownNow();
    }

    @SuppressWarnings("rawtypes")
    @Test(timeout = 60000)
    public void test() throws Exception {
        List results = new ArrayList();
        CountDownLatch latch = new CountDownLatch(KINDS.length);
        IOpenShiftWatchListener listener = new IOpenShiftWatchListener() {

            @SuppressWarnings("unchecked")
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
            IService service = client.getResourceFactory().stub(PredefinedResourceKind.SERVICE.getIdentifier(), "hello-world",
                    project.getName());
            service.addPort(8080, 8080);
            service = client.create(service);
            service.addLabel("foo", "bar");
            service = client.update(service);
            client.delete(service);
            assertArrayEquals(new ChangeType[] { ChangeType.ADDED, ChangeType.MODIFIED, ChangeType.DELETED },
                    results.toArray());
            assertEquals(0, latch.getCount());
        } finally {
            if (watcher != null) {
                watcher.stop();
            }
        }
    }

}
