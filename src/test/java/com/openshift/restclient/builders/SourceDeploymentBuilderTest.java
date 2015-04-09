/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.builders;

import static org.junit.Assert.*;

import java.util.List;

import org.jboss.dmr.ModelNode;
import org.junit.Test;

import com.openshift.internal.restclient.builders.SourceDeploymentBuilder;
import com.openshift.internal.restclient.model.BuildConfig;
import com.openshift.internal.restclient.model.DeploymentConfig;
import com.openshift.internal.restclient.model.ImageRepository;
import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.utils.Samples;

/**
 * @author Jeff Cantrill
 */
//TODO WIP...need to determine if this is needed
public class SourceDeploymentBuilderTest {

//	@Test
	public void test() {
		DockerImageURI baseImage = new DockerImageURI("openshift/wildfly-8-centos");
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
