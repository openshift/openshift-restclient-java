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

import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.IntegrationTestHelper;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.capability.resources.IBuildCancelable;
import com.openshift.restclient.capability.resources.IBuildTriggerable;
import com.openshift.restclient.model.IBuild;
import com.openshift.restclient.model.IBuildConfig;
import com.openshift.restclient.model.IImageStream;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.build.IBuildConfigBuilder;

public class BuildCapabilitiesIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(BuildCapabilitiesIntegrationTest.class);
    private IBuildConfig bc;
    private IImageStream is;
    private IntegrationTestHelper helper = new IntegrationTestHelper();
    private IProject project;
    private IClient client;

    @Before
    public void setUp() throws Exception {
        this.client = helper.createClientForBasicAuth();
        this.project = helper.getOrCreateIntegrationTestProject(client);

        // an output imagestream
        IImageStream is = client.getResourceFactory().stub(
                ResourceKind.IMAGE_STREAM, IntegrationTestHelper.appendRandom("ruby-hello-world"), project.getName());
        LOG.debug("Creating imagestream {}", is);
        this.is = client.create(is);
        LOG.debug("Generated imagestream {}", is);

        // a buildconfig
        IBuildConfigBuilder builder = client.adapt(IBuildConfigBuilder.class);
        assertNotNull("Exp. the client to be able to use a buildconfigbuilder", builder);
        LOG.debug("Creating BuildConfig {}", bc);
        this.bc = client.create(
                helper.stubBuildConfig(client, 
                        project.getNamespaceName(),
                        IntegrationTestHelper.appendRandom("test-bc"), 
                        "https://github.com/openshift/ruby-hello-world.git", 
                        Collections.emptyMap()));
        LOG.debug("Created BuildConfig {}", bc);
        assertNotNull(bc);
    }

    @After
    public void tearDown() {
        helper.cleanUpResources(client, bc, is);
    }

    @Test
    public void shouldTriggerBuild() {
        IBuild build = triggerBuild(bc);
        assertNotNull("Exp. to be able to trigger a build from a buildconfig", build);        
    }

    @Test
    public void shouldCancelBuild() {
        // given
        final IBuild build = triggerBuild(bc);
        // when
        IBuild cancelledBuild = build.accept(new CapabilityVisitor<IBuildCancelable, IBuild>() {

            @Override
            public IBuild visit(IBuildCancelable cap) {
                // bug in 4.1? build needs to be refreshed
                IBuild refreshedBuild = refresh(build);
                refreshedBuild.cancel();
                return refreshedBuild;
            }
        }, null);
        assertNotNull("Exp. to be able to cancel a build", cancelledBuild);
    }

    @Test
    public void shouldTriggerBuildFromBuild() {
        // given
        IBuild build = triggerBuild(bc);
        // when
        build = build.accept(new CapabilityVisitor<IBuildTriggerable, IBuild>() {
            @Override
            public IBuild visit(IBuildTriggerable capability) {
                return capability.trigger();
            }
        }, null);
        // when
        assertNotNull("Exp. to be able to trigger a build from a build", build);
    }

    @Test
    public void shouldTriggerBuildWithCause() {
        IBuild build = bc.accept(new CapabilityVisitor<IBuildTriggerable, IBuild>() {
            @Override
            public IBuild visit(IBuildTriggerable capability) {
                capability.addBuildCause("test cause");
                return capability.trigger();
            }
        }, null);
        assertNotNull("Exp. to be able to add a build cause for a build", build);
    }

    private IBuild refresh(IBuild build) {
        return client.get(ResourceKind.BUILD, build.getName(), build.getNamespaceName());
    }

    private IBuild triggerBuild(IBuildConfig bc) {
        IBuild build = bc.accept(new CapabilityVisitor<IBuildTriggerable, IBuild>() {
            @Override
            public IBuild visit(IBuildTriggerable capability) {
                return capability.trigger();
            }
        }, null);
        return build;
    }

}
