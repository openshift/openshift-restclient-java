/*******************************************************************************
 * Copyright (c) 2018-2019 Red Hat, Inc.
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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IOpenShiftWatchListener;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.capability.resources.IBinaryBuildTriggerable;
import com.openshift.restclient.capability.resources.IBuildCancelable;
import com.openshift.restclient.model.IBuild;
import com.openshift.restclient.model.IBuildConfig;
import com.openshift.restclient.model.IImageStream;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.build.IBuildConfigBuilder;

public class BinaryBuildCapabilitiesIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(BinaryBuildCapabilitiesIntegrationTest.class);
    private IntegrationTestHelper helper = new IntegrationTestHelper();
    private IClient client;
    private IProject project;
    private IImageStream is;
    private IBuildConfig bc;
    private IBuild build;

    @Before
    public void setUp() throws Exception {
        client = helper.createClientForBasicAuth();
        project = helper.getOrCreateIntegrationTestProject(client);

        // an output imagestream
        IImageStream is = client.getResourceFactory().stub(ResourceKind.IMAGE_STREAM, "rest-spring-boot",
                project.getName());
        LOG.debug("Creating imagestream {}", is);
        this.is = client.create(is);
        LOG.debug("Generated imagestream {}", is);

        // a buildconfig
        IBuildConfigBuilder builder = client.adapt(IBuildConfigBuilder.class);
        assertNotNull("Exp. the client to be able to use a buildconfigbuilder", builder);
        IBuildConfig bcStup = builder.named(IntegrationTestHelper.appendRandom("rest-spring-boot"))
                .inNamespace(project.getName())
                .fromBinarySource()
                .end()
                .usingSourceStrategy()
                .fromDockerImage("registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift").end()
                .toImageStreamTag("rest-spring-boot:latest")
                .build();
        LOG.debug("Creating BuildConfig {}", bcStup);
        this.bc = client.create(bcStup);
        LOG.debug("Created BuildConfig {}", bc);
        assertNotNull(bc);
    }

    @Test
    public void testBuildActions() throws InterruptedException {
        // trigger the build
        LOG.debug("Triggering build from the buildconfig...");
        IBuild build = bc.accept(new CapabilityVisitor<IBinaryBuildTriggerable, IBuild>() {
            @Override
            public IBuild visit(IBinaryBuildTriggerable capability) {
                return capability.triggerBinary(
                        BinaryBuildCapabilitiesIntegrationTest.class.getResourceAsStream("/rest-spring-boot.zip"));
            }
        }, null);
        assertNotNull("Exp. to be able to trigger a build from a buildconfig", build);
        LOG.debug("Triggered build {}", build);

        LOG.debug("Canceling the build...");
        // cancel the build
        build = build.accept(new CapabilityVisitor<IBuildCancelable, IBuild>() {

            @Override
            public IBuild visit(IBuildCancelable cap) {
                return cap.cancel();
            }
        }, null);
        assertNotNull("Exp. to be able to cancel a build", build);
        LOG.debug("Canceled build {}", build);

        // trigger a new build and wait for completion
        this.build = bc.accept(new CapabilityVisitor<IBinaryBuildTriggerable, IBuild>() {
            @Override
            public IBuild visit(IBinaryBuildTriggerable capability) {
                return capability.triggerBinary(
                        BinaryBuildCapabilitiesIntegrationTest.class.getResourceAsStream("/rest-spring-boot.zip"));
            }
        }, null);
        assertNotNull("Exp. to be able to trigger a build from a buildconfig", build);
        LOG.debug("Triggered build {}", build);
        CountDownLatch latch = new CountDownLatch(1);
        client.watch(project.getNamespaceName(), new IOpenShiftWatchListener.OpenShiftWatchListenerAdapter() {
            @Override
            public void received(IResource resource, ChangeType change) {
                if ("Complete".equals(((IBuild)resource).getStatus())) {
                    latch.countDown();
                }
            }

        }, ResourceKind.BUILD);
        latch.await(10, TimeUnit.MINUTES);
    }

    @After
    public void tearDown() {
        helper.cleanUpResources(client, build, bc, is);
    }

}
