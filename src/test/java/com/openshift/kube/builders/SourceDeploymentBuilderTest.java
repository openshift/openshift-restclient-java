package com.openshift.kube.builders;

import static org.junit.Assert.*;

import java.util.List;

import org.jboss.dmr.ModelNode;
import org.junit.Test;

import com.openshift.client.utils.Samples;
import com.openshift.internal.kube.Resource;
import com.openshift.internal.kube.builders.SourceDeploymentBuilder;
import com.openshift.kube.BuildConfig;
import com.openshift.kube.DeploymentConfig;
import com.openshift.kube.ImageRepository;
import com.openshift.kube.ResourceKind;
import com.openshift.kube.images.ImageUri;

public class SourceDeploymentBuilderTest {

	@Test
	public void test() {
		ImageUri baseImage = new ImageUri("openshift/wildfly-8-centos");
		SourceDeploymentBuilder builder = new SourceDeploymentBuilder("hello-openshift-project", "git@github.com:jcantrill/javaparks.git", "jcantrill",baseImage, "172.30.17.59:5001");
		List<Resource> resources = builder.build();
		assertEquals("Exp. multiple resources", 3, resources.size());
		for (Resource resource : resources) {
			switch (resource.getKind()) {
			case BuildConfig:	
				assertBuildConfig(resource);
				break;
			case ImageRepository:
				assertImageRepository(resource);
				break;
			case DeploymentConfig:
				assertDeploymentConfig(resource);
				break;
			default:
			}
		}

	}
	
	private void assertDeploymentConfig(Resource resource) {
		DeploymentConfig config = (DeploymentConfig) resource;
		assertEquals(ModelNode.fromJSONString(Samples.DEPLOYMENT_CONFIG_MINIMAL.getContentAsString()).toJSONString(false), ModelNode.fromJSONString(config.toPrettyString()).toJSONString(false));
//		assertEquals(ModelNode.fromJSONString(Samples.DEPLOYMENT_CONFIG_MINIMAL.getContentAsString()), ModelNode.fromJSONString(config.toPrettyString()));
	}

	private void assertImageRepository(Resource resource) {
		ImageRepository repo = (ImageRepository) resource;
		assertEquals(ModelNode.fromJSONString(Samples.IMAGE_REPOSITORY_MINIMAL.getContentAsString()), ModelNode.fromJSONString(repo.toPrettyString()));
	}

	private void assertBuildConfig(Resource r){
		BuildConfig config = (BuildConfig)r;
		assertEquals(ModelNode.fromJSONString(Samples.BUILD_CONFIG_MINIMAL.getContentAsString()), ModelNode.fromJSONString(config.toPrettyString()));
	}

}
