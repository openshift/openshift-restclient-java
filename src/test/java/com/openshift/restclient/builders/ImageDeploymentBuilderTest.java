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

import com.openshift.internal.restclient.builders.ImageDeploymentBuilder;
import com.openshift.internal.restclient.model.DeploymentConfig;
import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.utils.Samples;

/**
 * @author Jeff Cantrill
 */
//TODO WIP determine if this is still needed
public class ImageDeploymentBuilderTest {
	
//	@Test
	public void test() {
		DockerImageURI tag = new DockerImageURI("172.30.17.59:5001/jcantrill/javaparks:latest");
		ImageDeploymentBuilder builder = new ImageDeploymentBuilder("hello-openshift-project", tag, 8080);
		List<KubernetesResource> resources = builder.build();
		assertEquals("Exp. only one resource", 1, resources.size());
		assertEquals("Exp. a deployment config", ResourceKind.DeploymentConfig, resources.get(0).getKind());
		DeploymentConfig config = (DeploymentConfig) resources.get(0);
		
		assertEquals(ModelNode.fromJSONString(Samples.DEPLOYMENT_CONFIG_MINIMAL.getContentAsString()), ModelNode.fromJSONString(config.toPrettyString()));
	}

}
