package com.openshift.internal.restclient;

import com.openshift.internal.restclient.model.build.BuildConfigBuilder;
import com.openshift.internal.restclient.model.project.OpenshiftProjectRequest;
import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.model.IBuildConfig;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.IResource;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.openshift.internal.restclient.IntegrationTestHelper.cleanUpResource;
import static com.openshift.restclient.ResourceKind.BUILD_CONFIG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AsyncClientIntegrationTest {

	private static final String VERSION = "v1";

	private static final Logger LOG = LoggerFactory.getLogger(AsyncClientIntegrationTest.class);

	private static AsyncClient client;

	private static IResourceFactory factory;

	private static IProject project;

	private static IntegrationTestHelper helper = new IntegrationTestHelper();

	@BeforeClass
	public static void  setup() {
		client = helper.createAsyncClientForBasicAuth();
		factory = new ResourceFactory(client);
		OpenshiftProjectRequest projectRequest = factory.create(VERSION, ResourceKind.PROJECT_REQUEST);
		projectRequest.setName(helper.generateNamespace());
		project = (IProject) client.create(projectRequest);

		createBuildConfigWithLabels(project, "build1", new HashMap<String, String>() {{
			put("foo", "yes");
			put("bar", "no");
			put("baz", "no");
		}});

		createBuildConfigWithLabels(project, "build2", new HashMap<String, String>() {{
			put("foo", "no");
			put("bar", "yes");

		}});

		createBuildConfigWithLabels(project, "build3", new HashMap<String, String>() {{
			put("foo", "yes");
			put("bar", "yes");
		}});

		createBuildConfigWithLabels(project, "build4", new HashMap<>());

	}

	@AfterClass
	public static void cleanup() {
		cleanUpResource(client, project);
	}

	@Test
	public void testGetAllBuildConfigs() throws ExecutionException, InterruptedException, TimeoutException {

		Consumer<OpenShiftException> openShiftExceptionConsumer = e -> Assert.fail("Should not throw exception");
		Consumer<List<IBuildConfig>> listConsumer = bc -> Assert.assertEquals(4,bc.size());
		client.asyncList(BUILD_CONFIG, project.getNamespace(),
				openShiftExceptionConsumer,
				listConsumer);

	}


	private static IBuildConfig createBuildConfigWithLabels(IProject project, String name, HashMap<String, String> labelFilter) {

		IBuildConfig bc = new BuildConfigBuilder(client)
				.named(name)
				.inNamespace(project.getNamespace())
				.usingSourceStrategy()
				.fromDockerImage("centos/ruby-22-centos7:latest")
				.end()
				.toImageStreamTag("ruby-hello-world:latest")
				.withLabels(labelFilter)
				.build();
		return client.create(bc);
	}

}
