/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client.model;

import java.util.HashMap;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

import com.openshift3.client.IClient;
import com.openshift3.client.images.DockerImageURI;
import com.openshift3.client.model.IBuildConfig;
import com.openshift3.client.model.build.BuildStrategyType;
import com.openshift3.client.model.build.BuildTrigger;
import com.openshift3.client.model.build.IBuildStrategy;
import com.openshift3.internal.client.model.build.CustomBuildStrategy;
import com.openshift3.internal.client.model.build.DockerBuildStrategy;
import com.openshift3.internal.client.model.build.STIBuildStrategy;

public class BuildConfig extends KubernetesResource implements IBuildConfig {

	public BuildConfig(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
		//TODO add check to kind here
	}

	public String getSourceURI() {
		return asString(BUILDCONFIG_SOURCE_URI);
	}

	public void addTrigger(BuildTrigger type, String secret){
		//FIXME
//		ModelNode trigger = new ModelNode();
//		trigger.get("type").set(type.toString());
//		trigger.get(new String[]{type.toString(),"secret"}).set(secret);
//		getNode().get("triggers").add(trigger);
	}

	public void setSource(String type, String uri){
		//FIXME
//		ModelNode params = getNode().get("parameters");
//		params.get(new String[]{"source","type"}).set(type);
//		params.get(new String[]{"source","git","uri"}).set(uri);
	}
	
	public void setStrategy(String type, String baseImage){
		//FIXME
//		ModelNode strategy = getNode().get(new String []{"parameters","strategy"});
//		strategy.get("type").set(type);	
//		strategy.get(new String[]{"stiStrategy","image"}).set(baseImage);
	}
	
	public void setOutput(DockerImageURI imageUri){
		//FIXME
//		ModelNode output = getNode().get(new String []{"parameters","output"});
//		output.get("imageTag").set(imageUri.getUriWithoutHost());
//		output.get("registry").set(imageUri.getRepositoryHost());
	}

	@SuppressWarnings("unchecked")
	@Override
	public  <T extends IBuildStrategy> T getBuildStrategy() {
		switch(BuildStrategyType.valueOf(asString(BUILDCONFIG_TYPE))){
		case Custom:
			return (T) new CustomBuildStrategy(
						asString(BUILDCONFIG_CUSTOM_IMAGE),
						asBoolean(BUILDCONFIG_CUSTOM_EXPOSEDOCKERSOCKET),
						loadEnvironmentVars(new String[]{"customStrategy","env"},  get(BUILDCONFIG_STRATEGY))
					);
		case STI:
			return (T) new STIBuildStrategy(asString(BUILDCONFIG_STI_IMAGE),
					asString(BUILDCONFIG_STI_SCRIPTS),
					asBoolean(BUILDCONFIG_STI_CLEAN),
					loadEnvironmentVars(new String []{"stiStrategy","env"},  get(BUILDCONFIG_STRATEGY))
					);
		case Docker:
			return (T) new DockerBuildStrategy(
					asString(BUILDCONFIG_DOCKER_CONTEXTDIR),
					asBoolean(BUILDCONFIG_DOCKER_NOCACHE),
					asString(BUILDCONFIG_DOCKER_BASEIMAGE)
					);
		default:
		}
		return null;
	}


	private Map<String, String> loadEnvironmentVars(final String [] key, ModelNode root){
		Map<String, String> vars = new HashMap<String, String>();
		if(root.get(key).getType() == ModelType.LIST){
			for (ModelNode env : root.get(key).asList()) {
				vars.put(env.get("name").asString(), env.get("value").asString());
			}
		}
		return vars;
	}

}
