/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.v1;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.jboss.dmr.ModelNode;
import org.junit.Before;
import org.junit.Test;

import com.openshift.internal.restclient.model.BuildConfig;
import com.openshift.internal.restclient.model.EnvironmentVariable;
import com.openshift.internal.restclient.model.ModelNodeBuilder;
import com.openshift.internal.restclient.model.build.BuildConfigBuilder;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IBuildConfig;
import com.openshift.restclient.model.IEnvironmentVariable;
import com.openshift.restclient.model.build.BuildStrategyType;
import com.openshift.restclient.model.build.IBuildConfigBuilder;
import com.openshift.restclient.model.build.IBuildStrategy;
import com.openshift.restclient.model.build.IJenkinsPipelineStrategy;
import com.openshift.restclient.utils.Samples;

/**
 * @author Andre Dietisheim
 */
public class PipelineBuildConfigTest {

	private static final String VERSION = "v1";

	private IBuildConfig pipelineBc;
	private IBuildConfigBuilder builder;
	
	@Before
	public void setup() throws Exception{
		this.pipelineBc = createBuildConfig(Samples.V1_BUILDCONFIG_PIPELINE.getContentAsString());
		this.builder = createBuilder();
	}

	private IBuildConfig createBuildConfig(String content) throws MalformedURLException {
		IClient client = createClient();
		BuildConfig bc = new BuildConfig(ModelNode.fromJSONString(content), client, null);
		IResourceFactory factory = createResourceFactoryFor(bc);
		when(client.getResourceFactory()).thenReturn(factory);
		return bc;
	}

	private IBuildConfigBuilder createBuilder() throws MalformedURLException {
		IClient client = createClient();
		IBuildConfig bc = new BuildConfig(new ModelNode(), client, null);
		IResourceFactory factory = createResourceFactoryFor(bc);
		when(client.getResourceFactory()).thenReturn(factory);

		return new BuildConfigBuilder(client)
				.named("foor")
				.inNamespace("bar");
	}

	private IClient createClient() throws MalformedURLException {
		IClient client = mock(IClient.class); 
		doReturn(new URL("https://localhost:8443")).when(client).getBaseURL();
		doReturn(VERSION).when(client).getOpenShiftAPIVersion();
		return client;
	}

	private IResourceFactory createResourceFactoryFor(IBuildConfig bc) {
		IResourceFactory factory = mock(IResourceFactory.class);
		when(factory.stub(eq(ResourceKind.BUILD_CONFIG), anyString(), anyString())).thenReturn(bc);		
		return factory;
	}

	@Test
	public void shouldHaveJenkinsPipelineBuildStrategy() {
		// given
		// when
		IBuildStrategy strategy = pipelineBc.getBuildStrategy();
		// then
		assertThat(strategy).isNotNull();
		assertThat(strategy.getType()).isEqualTo(BuildStrategyType.JENKINS_PIPELINE);
	}

	@Test
	public void shouldHaveJenkinsfile() {
		// given
		// when
		IJenkinsPipelineStrategy strategy = pipelineBc.getBuildStrategy();
		// then
		assertThat(strategy.getJenkinsfile()).isNotEmpty();
	}

	@Test
	public void shouldHaveJenkinsfileGivenItWasSet() {
		// given
		IJenkinsPipelineStrategy strategy = pipelineBc.getBuildStrategy();
		// when
		strategy.setJenkinsfile("fooBar");
		// then
		assertThat(strategy.getJenkinsfile()).isEqualTo("fooBar");
	}

	@Test
	public void shouldHaveJenkinsfilePath() {
		// given
		// when
		IJenkinsPipelineStrategy strategy = pipelineBc.getBuildStrategy();
		// then
		assertThat(strategy.getJenkinsfilePath()).isNotEmpty();
	}

	@Test
	public void shouldHaveJenkinsfilePathGivenItWasSet() {
		// given
		IJenkinsPipelineStrategy strategy = pipelineBc.getBuildStrategy();
		// when
		strategy.setJenkinsfilePath("/foo/bar");
		// then
		assertThat(strategy.getJenkinsfilePath()).isEqualTo("/foo/bar");
	}

	@Test
	public void shouldHaveEnvVars() {
		// given
		// when
		IJenkinsPipelineStrategy strategy = pipelineBc.getBuildStrategy();
		// then
		assertThat(strategy.getEnvVars()).containsOnly(
				new EnvironmentVariable(new ModelNodeBuilder()
					.set("name", "foo")
					.set("value", "bar")
					.build(), null),
				new EnvironmentVariable(new ModelNodeBuilder()
					.set("name", "kung")
					.set("value", "foo")
					.build(), null)
				);
	}

	@Test
	public void shouldHaveEnvVarsThatWereSet() {
		// given
		IJenkinsPipelineStrategy strategy = pipelineBc.getBuildStrategy();
		IEnvironmentVariable envVar = new EnvironmentVariable(new ModelNodeBuilder()
				.set("name", "gargamel")
				.set("value", "crazyLad")
				.build(), null);
		List<IEnvironmentVariable> envVars = Arrays.asList(envVar);
		// when
		strategy.setEnvVars(envVars);
		// then
		assertThat(strategy.getEnvVars()).containsOnly(envVar);
	}
	
	@Test
	public void shouldBuildJenkinsPipelineStrategyBuildConfig() {
		// given
		// when
		IBuildConfig bc = builder
				.usingJenkinsPipelineStrategy()
					.end()
				.build();
		// then
		IBuildStrategy strategy = bc.getBuildStrategy();
		assertThat(strategy).isInstanceOf(IJenkinsPipelineStrategy.class);
	}

	@Test
	public void shouldBuildJenkinsPipelineStrategyWithJenkinsfileAndPath() {
		// given
		// when
		IBuildConfig bc = builder
				.usingJenkinsPipelineStrategy()
					.usingFile("node('aNode') {}")
					.usingFilePath("some/path/to/some/location")
					.end()
				.build();
		// then
		IJenkinsPipelineStrategy strategy = bc.getBuildStrategy();
		assertThat(strategy.getJenkinsfile()).isEqualTo("node('aNode') {}");
		assertThat(strategy.getJenkinsfilePath()).isEqualTo("some/path/to/some/location");
	}
}
