/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.v1beta3;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.junit.BeforeClass;
import org.junit.Test;

import com.openshift.internal.restclient.OpenShiftAPIVersion;
import com.openshift.internal.restclient.ResourceFactory;
import com.openshift.internal.restclient.model.BuildConfig;
import com.openshift.internal.restclient.model.build.GitBuildSource;
import com.openshift.internal.restclient.model.build.ImageChangeTrigger;
import com.openshift.internal.restclient.model.build.STIBuildStrategy;
import com.openshift.internal.restclient.model.build.WebhookTrigger;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IBuildConfig;
import com.openshift.restclient.model.build.BuildSourceType;
import com.openshift.restclient.model.build.BuildStrategyType;
import com.openshift.restclient.model.build.BuildTriggerType;
import com.openshift.restclient.model.build.IBuildSource;
import com.openshift.restclient.model.build.IBuildStrategy;
import com.openshift.restclient.model.build.IBuildTrigger;
import com.openshift.restclient.model.build.IGitBuildSource;
import com.openshift.restclient.model.build.ISTIBuildStrategy;
import com.openshift.restclient.utils.Samples;

/**
 * @author Jeff Cantrill
 */
public class BuildConfigTest {
	
	private static IBuildConfig config;
	private static IClient client;
	
	@BeforeClass
	public static void setup() throws Exception{
		client = mock(IClient.class);
		when(client.getBaseURL()).thenReturn(new URL("https://localhost:8443"));
		when(client.getOpenShiftAPIVersion()).thenReturn("v1beta3");
		ModelNode node = ModelNode.fromJSONString(Samples.V1BETA3_BUILD_CONFIG.getContentAsString());
		config = new BuildConfig(node, client, getPropertyKeys());
	}

	private static Map<String, String[]> getPropertyKeys() {
		return ResourcePropertiesRegistry.getInstance().get("v1beta3", ResourceKind.BUILD_CONFIG);
	}
	
	@Test
	public void getBuildTriggers(){
		assertBuildTriggers(config.getBuildTriggers().toArray(new IBuildTrigger[]{}));
	}

	@Test
	public void addBuildTriggers() {
		BuildConfig writeConfig = new ResourceFactory(client).create(OpenShiftAPIVersion.v1beta3.name(), ResourceKind.BUILD_CONFIG);

		writeConfig.addBuildTrigger(new WebhookTrigger(BuildTriggerType.github, "secret101", null, null, null,null));
		writeConfig.addBuildTrigger(new WebhookTrigger(BuildTriggerType.generic, "secret101", null, null, null, null));
		writeConfig.addBuildTrigger(new ImageChangeTrigger("", "", ""));

		assertBuildTriggers(reCreateBuildConfig(writeConfig).getBuildTriggers().toArray(new IBuildTrigger[]{}));
	}

	@Test
	public void getOutputRespositoryName(){
		assertEquals("origin-ruby-sample:latest", config.getOutputRepositoryName());
	}
	
	@Test
	public void getSourceURI(){
		assertEquals("git://github.com/openshift/ruby-hello-world.git", config.getSourceURI());
	}
	
	@Test
	public void getGitBuildSource(){
		IBuildSource source = config.getBuildSource();
		assertGitBuildSource(source);
	}

	@Test
	public void setGitBuildSource() {
		BuildConfig writeConfig = new ResourceFactory(client).create(OpenShiftAPIVersion.v1beta3.name(), ResourceKind.BUILD_CONFIG);

		Map<String, String> env = new HashMap<String, String>();
		env.put("foo", "bar");
		writeConfig.setBuildSource(new GitBuildSource("git://github.com/openshift/ruby-hello-world.git", ""));

		assertGitBuildSource(reCreateBuildConfig(writeConfig).getBuildSource());
	}

	@Test
	public void getSTIBuildStrategy() {
		IBuildStrategy strategy = config.getBuildStrategy();
		assertSourceBuildStrategy(strategy);
	}

	@Test
	public void setSTIBuildStrategy() {
		BuildConfig writeConfig = new ResourceFactory(client).create(OpenShiftAPIVersion.v1beta3.name(), ResourceKind.BUILD_CONFIG);

		Map<String, String> env = new HashMap<String, String>();
		env.put("foo", "bar");
		writeConfig.setBuildStrategy(new STIBuildStrategy("ruby-20-centos7:latest", "alocation", true, env));

		assertSourceBuildStrategy(reCreateBuildConfig(writeConfig).getBuildStrategy());
	}

	private void assertBuildTriggers(IBuildTrigger[] triggers) {
		IBuildTrigger [] exp = new IBuildTrigger[]{
				new WebhookTrigger(BuildTriggerType.GITHUB, "secret101","ruby-sample-build", "https://localhost:8443", "v1beta3","test"),
				new WebhookTrigger(BuildTriggerType.GENERIC, "secret101","ruby-sample-build", "https://localhost:8443", "v1beta3","test"),
				new ImageChangeTrigger("", "", "")
		};
		assertArrayEquals(exp, triggers);
	}

	private void assertGitBuildSource(IBuildSource source) {
		assertEquals(BuildSourceType.GIT, source.getType());
		assertEquals("git://github.com/openshift/ruby-hello-world.git", source.getURI());
		assertTrue(source instanceof IGitBuildSource);

		IGitBuildSource git = (IGitBuildSource)source;
		assertEquals("Exp. to get the source ref","", git.getRef());
	}

	private void assertSourceBuildStrategy(IBuildStrategy strategy) {
		assertEquals(BuildStrategyType.SOURCE, strategy.getType());
		assertTrue(strategy instanceof ISTIBuildStrategy);

		ISTIBuildStrategy sti = (ISTIBuildStrategy)strategy;
		assertEquals(new DockerImageURI("ruby-20-centos7:latest"), sti.getImage());
		assertEquals("alocation", sti.getScriptsLocation());
		assertEquals(true, sti.incremental());
		assertEquals(1, sti.getEnvironmentVariables().size());
		assertTrue("Exp. to find the environment variable",sti.getEnvironmentVariables().containsKey("foo"));
		assertEquals("bar",sti.getEnvironmentVariables().get("foo"));
	}

	private BuildConfig reCreateBuildConfig(BuildConfig config) {
		return new BuildConfig(config.getNode(), client, getPropertyKeys());
	}
}
