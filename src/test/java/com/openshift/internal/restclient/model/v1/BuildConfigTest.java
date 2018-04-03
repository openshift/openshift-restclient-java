/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.v1;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.openshift.restclient.PredefinedResourceKind;
import org.jboss.dmr.ModelNode;
import org.junit.BeforeClass;
import org.junit.Test;

import com.openshift.internal.restclient.ResourceFactory;
import com.openshift.internal.restclient.model.BuildConfig;
import com.openshift.internal.restclient.model.ModelNodeBuilder;
import com.openshift.internal.restclient.model.build.GitBuildSource;
import com.openshift.internal.restclient.model.build.ImageChangeTrigger;
import com.openshift.internal.restclient.model.build.SourceBuildStrategy;
import com.openshift.internal.restclient.model.build.WebhookTrigger;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IBuildConfig;
import com.openshift.restclient.model.IEnvironmentVariable;
import com.openshift.restclient.model.build.BuildSourceType;
import com.openshift.restclient.model.build.BuildStrategyType;
import com.openshift.restclient.model.build.BuildTriggerType;
import com.openshift.restclient.model.build.IBuildSource;
import com.openshift.restclient.model.build.IBuildStrategy;
import com.openshift.restclient.model.build.IBuildTrigger;
import com.openshift.restclient.model.build.IGitBuildSource;
import com.openshift.restclient.model.build.ISourceBuildStrategy;
import com.openshift.restclient.utils.Samples;

/**
 * @author Jeff Cantrill
 */
public class BuildConfigTest {
	
	private static final String VERSION = "v1";
	private static IBuildConfig config;
	private static IClient client;
	
	@BeforeClass
	public static void setup() throws Exception{
		client = mock(IClient.class);
		when(client.getBaseURL()).thenReturn(new URL("https://localhost:8443"));
		when(client.getOpenShiftAPIVersion()).thenReturn(VERSION);
		ModelNode node = ModelNode.fromJSONString(Samples.V1_BUILD_CONFIG.getContentAsString());
		config = new BuildConfig(node, client, null);
	}

	@Test
	public void getBuildTriggers(){
		assertBuildTriggers(config.getBuildTriggers().toArray(new IBuildTrigger[]{}));
	}

	@Test
	public void addBuildTriggers() {
		BuildConfig writeConfig = new ResourceFactory(client){}.create(VERSION, PredefinedResourceKind.BUILD_CONFIG.getIdentifier());

		writeConfig.addBuildTrigger(new WebhookTrigger(BuildTriggerType.GITHUB, "secret101", "https://localhost:8443"));
		writeConfig.addBuildTrigger(new WebhookTrigger(BuildTriggerType.GENERIC, "secret101", "https://localhost:8443"));
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
		BuildConfig writeConfig = new ResourceFactory(client){}.create(VERSION, PredefinedResourceKind.BUILD_CONFIG.getIdentifier());

		Map<String, String> env = new HashMap<String, String>();
		env.put("foo", "bar");
		writeConfig.setBuildSource(new GitBuildSource("git://github.com/openshift/ruby-hello-world.git", "", "foobar"));

		assertGitBuildSource(reCreateBuildConfig(writeConfig).getBuildSource());
	}

	@Test
	public void getSourceBuildStrategy() {
		IBuildStrategy strategy = config.getBuildStrategy();
		assertSourceBuildStrategy(strategy);
	}

	@Test
	public void setSourceBuildStrategy() {
		BuildConfig writeConfig = new ResourceFactory(client){}.create(VERSION, PredefinedResourceKind.BUILD_CONFIG.getIdentifier());

		ModelNode node = new ModelNodeBuilder()
			.set("type", BuildStrategyType.SOURCE)
			.set(SourceBuildStrategy.FROM_IMAGE, "ruby-20-centos7:latest")
			.set(SourceBuildStrategy.SCRIPTS, "alocation")
			.set(SourceBuildStrategy.INCREMENTAL, true)
			.add(SourceBuildStrategy.ENV, new ModelNodeBuilder()
				.set("name", "foo")
				.set("value", "bar"))
			.build();
		
		writeConfig.setBuildStrategy(new SourceBuildStrategy(node, new HashMap<>()));

		assertSourceBuildStrategy(reCreateBuildConfig(writeConfig).getBuildStrategy());
	}

	private void assertBuildTriggers(IBuildTrigger[] triggers) {
		IBuildTrigger [] exp = new IBuildTrigger[]{
				new WebhookTrigger(BuildTriggerType.GITHUB, "secret101","https://localhost:8443"),
				new WebhookTrigger(BuildTriggerType.GENERIC, "secret101","https://localhost:8443"),
				new ImageChangeTrigger("", "", "")
		};
		assertArrayEquals(exp, triggers);
	}

	private void assertGitBuildSource(IBuildSource source) {
		assertEquals(BuildSourceType.GIT, source.getType());
		assertEquals("git://github.com/openshift/ruby-hello-world.git", source.getURI());
		assertEquals("foobar", source.getContextDir());
		assertTrue(source instanceof IGitBuildSource);

		IGitBuildSource git = (IGitBuildSource)source;
		assertEquals("Exp. to get the source ref","", git.getRef());
	}

	private void assertSourceBuildStrategy(IBuildStrategy strategy) {
		assertEquals(BuildStrategyType.SOURCE, strategy.getType());
		assertTrue(strategy instanceof ISourceBuildStrategy);

		ISourceBuildStrategy source = (ISourceBuildStrategy)strategy;
		assertEquals(new DockerImageURI("ruby-20-centos7:latest"), source.getImage());
		assertEquals("alocation", source.getScriptsLocation());
		assertEquals(true, source.incremental());
		
		Map<String, String> envVars = source.getEnvironmentVariables();
		assertEquals(1, envVars.size());
		assertTrue("Exp. to find the environment variable",envVars.containsKey("foo"));
		assertEquals("bar",envVars.get("foo"));
		
		envVars.put("newKey", "newValue");
		source.setEnvironmentVariables(envVars);
		envVars = source.getEnvironmentVariables();
		assertEquals(2, envVars.size());
		assertTrue("Exp. to find the environment variable",envVars.containsKey("newKey"));
		assertEquals("newValue",envVars.get("newKey"));
		
		Collection<IEnvironmentVariable> vars = source.getEnvVars();
		assertTrue(vars.stream().filter(e->"newKey".equals(e.getName())).findFirst().isPresent());
		
		vars.remove(vars.toArray()[0]);
		source.setEnvVars(vars);
		vars = source.getEnvVars();
		assertEquals(1, vars.size());
	}

	private BuildConfig reCreateBuildConfig(BuildConfig config) {
		return new BuildConfig(config.getNode(), client, null);
	}
}
