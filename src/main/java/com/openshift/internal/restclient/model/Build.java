/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model;

import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.capability.CapabilityInitializer;
import com.openshift.internal.restclient.model.build.BuildStatus;
import com.openshift.internal.restclient.model.build.CustomBuildStrategy;
import com.openshift.internal.restclient.model.build.DockerBuildStrategy;
import com.openshift.internal.restclient.model.build.GitBuildSource;
import com.openshift.internal.restclient.model.build.SourceBuildStrategy;
import com.openshift.restclient.IClient;
import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IBuild;
import com.openshift.restclient.model.build.BuildSourceType;
import com.openshift.restclient.model.build.BuildStrategyType;
import com.openshift.restclient.model.build.IBuildSource;
import com.openshift.restclient.model.build.IBuildStatus;
import com.openshift.restclient.model.build.IBuildStrategy;

/**
 * @author Jeff Cantrill
 */
public class Build extends KubernetesResource implements IBuild{
	
	private static final String BUILD_MESSAGE = "status.message";
	private static final String BUILD_PODNAME = "podName";
	private static final String BUILD_STATUS = "status.phase";
	private static final String BUILD_STATUS_CANCELLED = "status.cancelled";
	private static final String OUTPUT_KIND = "spec.output.to.kind";
	private static final String OUTPUT_NAME = "spec.output.to.name";
	
	private static final String COMPLETE = "Complete";
	private static final String FAILED = "Failed";
	private static final String CANCELLED = "Cancelled";
	private Map<String, String[]> propertyKeys;


	public Build(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
		this.propertyKeys = propertyKeys;
		CapabilityInitializer.initializeCapabilities(getModifiableCapabilities(), this, client);
	}

	@Override
	public String getStatus() {
		return asString(BUILD_STATUS);
	}

	@Override
	public String getMessage() {
		return asString(BUILD_MESSAGE);
	}

	@Override
	public String getPodName() {
		return asString(BUILD_PODNAME);
	}

	@Override
	public boolean cancel() {
		String currentStatus = getStatus();
		if (!currentStatus.equalsIgnoreCase(COMPLETE) && !currentStatus.equalsIgnoreCase(FAILED) && !currentStatus.equalsIgnoreCase(CANCELLED)) {
			set(BUILD_STATUS_CANCELLED, true);
			return true;
		}
		return false;
	}

	@Override
	public DockerImageURI getOutputTo() {
		return new DockerImageURI(asString(OUTPUT_NAME));
	}

	@Override
	public String getOutputKind() {
		return asString(OUTPUT_KIND);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends IBuildSource> T getBuildSource() {
		switch(asString("spec.source.type")){
		case BuildSourceType.GIT:
			return (T) new GitBuildSource(asString("spec.source.git.uri"), asString("spec.source.git.ref"), asString("spec.source.git.contextDir"));
		default:
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public  <T extends IBuildStrategy> T getBuildStrategy() {
		switch(asString("spec.strategy.type")){
		case BuildStrategyType.CUSTOM:
			return (T) new CustomBuildStrategy(
						asString("spec.strategy.customStrategy.image"),
						asBoolean("spec.strategy.customStrategy.exposeDockerSocket"),
						getEnvMap("spec.strategy.customStrategy.env")
					);
		case BuildStrategyType.SOURCE:
			return (T) new SourceBuildStrategy(get("spec.strategy"), getPropertyKeys());

		case BuildStrategyType.DOCKER:

			return (T) new DockerBuildStrategy(
					asString("spec.strategy.dockerStrategy.contextDir"),
					asBoolean("spec.strategy.dockerStrategy.noCache"),
					asString("spec.strategy.dockerStrategy.baseImage")
					);
		default:
		}
		return null;
	}

	@Override
	public String getPushSecret() {
		return asString("spec.output.pushSecret.name");
	}

	@Override
	public IBuildStatus getBuildStatus() {
		return new BuildStatus(get("status"), this.propertyKeys);
	}

	
	
}
