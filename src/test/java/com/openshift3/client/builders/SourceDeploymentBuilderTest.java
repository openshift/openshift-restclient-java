package com.openshift3.client.builders;

import static org.junit.Assert.*;

import java.util.List;

import org.jboss.dmr.ModelNode;
import org.junit.Test;

import com.openshift.client.utils.Samples;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.images.ImageUri;
import com.openshift3.internal.client.builders.SourceDeploymentBuilder;
import com.openshift3.internal.client.model.BuildConfig;
import com.openshift3.internal.client.model.DeploymentConfig;
import com.openshift3.internal.client.model.ImageRepository;
import com.openshift3.internal.client.model.KubernetesResource;

//TODO WIP...need to determine if this is needed
public class SourceDeploymentBuilderTest {

//	@Test
	public void test() {
		ImageUri baseImage = new ImageUri("openshift/wildfly-8-centos");
		SourceDeploymentBuilder builder = new SourceDeploymentBuilder("hello-openshift-project", "git@github.com:jcantrill/javaparks.git", "jcantrill",baseImage, "172.30.17.59:5001");
		List<KubernetesResource> resources = builder.build();
		assertEquals("Exp. multiple resources", 3, resources.size());
		for (KubernetesResource resource : resources) {
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
	
	private void assertDeploymentConfig(KubernetesResource resource) {
		DeploymentConfig config = (DeploymentConfig) resource;
		assertEquals(ModelNode.fromJSONString(Samples.DEPLOYMENT_CONFIG_MINIMAL.getContentAsString()).toJSONString(false), ModelNode.fromJSONString(config.toPrettyString()).toJSONString(false));
//		assertEquals(ModelNode.fromJSONString(Samples.DEPLOYMENT_CONFIG_MINIMAL.getContentAsString()), ModelNode.fromJSONString(config.toPrettyString()));
	}

	private void assertImageRepository(KubernetesResource resource) {
		ImageRepository repo = (ImageRepository) resource;
		assertEquals(ModelNode.fromJSONString(Samples.IMAGE_REPOSITORY_MINIMAL.getContentAsString()), ModelNode.fromJSONString(repo.toPrettyString()));
	}

	private void assertBuildConfig(KubernetesResource r){
		BuildConfig config = (BuildConfig)r;
		assertEquals(ModelNode.fromJSONString(Samples.BUILD_CONFIG_MINIMAL.getContentAsString()), ModelNode.fromJSONString(config.toPrettyString()));
	}

}
