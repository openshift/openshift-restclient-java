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

import static org.junit.Assert.*;

import com.openshift.restclient.PredefinedResourceKind;
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

/**
 * 
 * @author Jeff Cantrill
 *
 */
public class BuildCapabilitiesIntegrationTest {

	private static final Logger LOG = LoggerFactory.getLogger(BuildCapabilitiesIntegrationTest.class);
	private IBuildConfig config;
	private IntegrationTestHelper helper = new IntegrationTestHelper();
	private IProject project;
	private IClient client;
	
	@Before
	public void setUp() throws Exception {
		client = helper.createClientForBasicAuth();
		project = helper.generateProject(client);
		
		//an output imagestream
		IImageStream is = client.getResourceFactory().stub(PredefinedResourceKind.IMAGE_STREAM.getIdentifier(), "ruby-hello-world", project.getName());
		LOG.debug("Creating imagestream {}", is);
		is = client.create(is);
		LOG.debug("Generated imagestream {}", is);

		//a buildconfig
		IBuildConfigBuilder builder = client.adapt(IBuildConfigBuilder.class);
		assertNotNull("Exp. the client to be able to use a buildconfigbuilder", builder);
		config = builder.named("hello-openshift")
				.inNamespace(project.getName())
				.fromGitSource()
				.fromGitUrl("https://github.com/openshift/ruby-hello-world.git")
				.end()
				.usingSourceStrategy()
				.fromDockerImage("centos/ruby-22-centos7:latest")
				.end()
				.toImageStreamTag("ruby-hello-world:latest")
				.build();
		LOG.debug("Creating BuildConfig {}", config);
		config = client.create(config);
		LOG.debug("Created BuildConfig {}", config);
		assertNotNull(config);
	}

	@Test
	public void testBuildActions() {
		
		//trigger the build
		LOG.debug("Triggering build from the buildconfig...");
		IBuild build = config.accept(new CapabilityVisitor<IBuildTriggerable, IBuild>() {
			@Override
			public IBuild visit(IBuildTriggerable capability) {
				return capability.trigger();
			}
		}, null);
		assertNotNull("Exp. to be able to trigger a build from a buildconfig", build);
		LOG.debug("Triggered build {}", build);
		
		LOG.debug("Canceling the build...");
		//cancel the build
		build = build.accept(new CapabilityVisitor<IBuildCancelable, IBuild>() {

			@Override
			public IBuild visit(IBuildCancelable cap) {
				return cap.cancel();
			}
		}, null);
		assertNotNull("Exp. to be able to cancel a build", build);
		LOG.debug("Canceled build {}", build);
		
		//trigger the build from a build
		LOG.debug("Triggering build from a build...");
		build = build.accept(new CapabilityVisitor<IBuildTriggerable, IBuild>() {
			@Override
			public IBuild visit(IBuildTriggerable capability) {
				return capability.trigger();
			}
		}, null);
		assertNotNull("Exp. to be able to trigger a build from a build", build);
		LOG.debug("Triggered build {}", build);
		
		//add a build cause
		LOG.debug("Triggering build with build cause...");
		build = build.accept(new CapabilityVisitor<IBuildTriggerable, IBuild>() {
			@Override
			public IBuild visit(IBuildTriggerable capability) {
				capability.addBuildCause("test cause");
				return capability.trigger();
			}
		}, null);
		assertNotNull("Exp. to be able to add a build cause for a build", build);
		LOG.debug("Triggered build {}", build);
	}
	
	@After
	public void tearDown() {
		IntegrationTestHelper.cleanUpResource(client, project);
	}

}
