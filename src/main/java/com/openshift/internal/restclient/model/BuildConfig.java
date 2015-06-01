/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.OpenShiftAPIVersion;
import com.openshift.internal.restclient.model.build.CustomBuildStrategy;
import com.openshift.internal.restclient.model.build.DockerBuildStrategy;
import com.openshift.internal.restclient.model.build.GitBuildSource;
import com.openshift.internal.restclient.model.build.ImageChangeTrigger;
import com.openshift.internal.restclient.model.build.STIBuildStrategy;
import com.openshift.internal.restclient.model.build.WebhookTrigger;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.internal.util.JBossDmrExtentions;
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
import com.openshift.restclient.model.build.ICustomBuildStrategy;
import com.openshift.restclient.model.build.IDockerBuildStrategy;
import com.openshift.restclient.model.build.ISTIBuildStrategy;

/**
 * @author Jeff Cantrill
 */
public class BuildConfig extends KubernetesResource implements IBuildConfig {

	public BuildConfig(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
		//TODO add check to kind here
	}
	
	@Override
	public List<IBuildTrigger> getBuildTriggers() {
		List<IBuildTrigger> triggers = new ArrayList<IBuildTrigger>();
		List<ModelNode> list = get(BUILDCONFIG_TRIGGERS).asList();
		final String name = getName();
		final String url = getClient() != null ? getClient().getBaseURL().toString() : "";
		final String version = getClient() != null ? getClient().getOpenShiftAPIVersion() : "";
		for (ModelNode node : list) {
			switch(BuildTriggerType.valueOf(node.get("type").asString())){
				case generic:
					triggers.add(new WebhookTrigger(BuildTriggerType.generic, node.get(new String[]{"generic","secret"}).asString(), name, url, version,getNamespace()));
					break;
				case github:
					triggers.add(new WebhookTrigger(BuildTriggerType.github, node.get(new String[]{"github","secret"}).asString(), name, url, version, getNamespace()));
					break;
				case imageChange:
					Map<String, String[]> keys = ResourcePropertiesRegistry.getInstance().get(version, ResourceKind.BuildConfig);
					triggers.add(new ImageChangeTrigger(
							JBossDmrExtentions.asString(node, keys, BUILD_CONFIG_IMAGECHANGE_IMAGE),
							JBossDmrExtentions.asString(node, keys, BUILD_CONFIG_IMAGECHANGE_NAME),
							JBossDmrExtentions.asString(node, keys, BUILD_CONFIG_IMAGECHANGE_TAG))
					);
					break;
				default:
			}
		}
		return triggers;
	}

	@Override
	public String getOutputRepositoryName() {
		return asString(BUILDCONFIG_OUTPUT_REPO);
	}

	public String getSourceURI() {
		return asString(BUILDCONFIG_SOURCE_URI);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends IBuildSource> T getBuildSource() {
		switch(BuildSourceType.valueOf(asString(BUILDCONFIG_SOURCE_TYPE))){
		case Git:
			return (T) new GitBuildSource(asString(BUILDCONFIG_SOURCE_URI), asString(BUILDCONFIG_SOURCE_REF));
		default:
		}
		return null;
	}

	public void setSource(String type, String uri){
		//FIXME
//		ModelNode params = getNode().get("parameters");
//		params.get(new String[]{"source","type"}).set(type);
//		params.get(new String[]{"source","git","uri"}).set(uri);
	}
	
	@Override
	public void setBuildStrategy(IBuildStrategy strategy) {
		// Remove other strategies if already set?
		switch(strategy.getType()) {
		case Custom:
			if ( !(strategy instanceof ICustomBuildStrategy)) {
				throw new IllegalArgumentException("IBuildStrategy of type Custom does not implement ICustomBuildStrategy");
			}
			ICustomBuildStrategy custom = (ICustomBuildStrategy)strategy;
			if(custom.getImage() != null) {
				set(BUILDCONFIG_CUSTOM_IMAGE, custom.getImage().toString());
			}
			set(BUILDCONFIG_CUSTOM_EXPOSEDOCKERSOCKET, custom.exposeDockerSocket());
			if(custom.getEnvironmentVariables() != null) {
				setEnvMap(BUILDCONFIG_CUSTOM_ENV, custom.getEnvironmentVariables());
			}
			break;
		case STI:
			if ( !(strategy instanceof ISTIBuildStrategy)) {
				throw new IllegalArgumentException("IBuildStrategy of type Custom does not implement ISTIBuildStrategy");
			}
			ISTIBuildStrategy sti = (ISTIBuildStrategy)strategy;
			if(sti.getImage() != null) {
				set(BUILDCONFIG_STI_IMAGE, sti.getImage().toString());
			}
			if(sti.getScriptsLocation() != null) {
				set(BUILDCONFIG_STI_SCRIPTS, sti.getScriptsLocation());
			}
			if(OpenShiftAPIVersion.v1beta1.name().equals(getApiVersion())) {
				set(BUILDCONFIG_STI_CLEAN, sti.forceClean());
			} else if(OpenShiftAPIVersion.v1beta3.name().equals(getApiVersion())) {
				set(BUILDCONFIG_STI_INCREMENTAL, sti.incremental());
			}
			if(sti.getEnvironmentVariables() != null) {
				setEnvMap(BUILDCONFIG_STI_ENV, sti.getEnvironmentVariables());
			}
			break;
		case Docker:
			if ( !(strategy instanceof IDockerBuildStrategy)) {
				throw new IllegalArgumentException("IBuildStrategy of type Custom does not implement IDockerBuildStrategy");
			}
			IDockerBuildStrategy docker = (IDockerBuildStrategy)strategy;
			if(docker.getBaseImage() != null) {
				set(BUILDCONFIG_DOCKER_BASEIMAGE, docker.getBaseImage().toString());
			}
			if(docker.getContextDir() != null) {
				set(BUILDCONFIG_DOCKER_CONTEXTDIR, docker.getContextDir());
			}
			set(BUILDCONFIG_DOCKER_NOCACHE, docker.isNoCache());
			break;
		}

		set(BUILDCONFIG_TYPE, strategy.getType().name());
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
						getEnvMap(BUILDCONFIG_CUSTOM_ENV)
					);
		case STI:
			boolean incremental = false;
			if(OpenShiftAPIVersion.v1beta1.name().equals(getApiVersion())) {
				incremental = !asBoolean(BUILDCONFIG_STI_CLEAN);
			} else if(OpenShiftAPIVersion.v1beta3.name().equals(getApiVersion())) {
				incremental = asBoolean(BUILDCONFIG_STI_INCREMENTAL);
			}

			return (T) new STIBuildStrategy(asString(BUILDCONFIG_STI_IMAGE),
					asString(BUILDCONFIG_STI_SCRIPTS),
					incremental,
					getEnvMap(BUILDCONFIG_STI_ENV)
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
}
