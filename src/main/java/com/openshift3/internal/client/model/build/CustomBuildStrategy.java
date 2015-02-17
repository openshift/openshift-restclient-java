/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client.model.build;

import java.util.Map;

import com.openshift3.client.images.DockerImageURI;
import com.openshift3.client.model.build.BuildStrategyType;
import com.openshift3.client.model.build.ICustomBuildStrategy;

public class CustomBuildStrategy implements ICustomBuildStrategy {

	private DockerImageURI image;
	private boolean exposeDockerSocket;
	private Map<String, String> env;

	public CustomBuildStrategy(String image, boolean exposeDockerSocket, Map<String, String> env){
		this.image = new DockerImageURI(image);
		this.exposeDockerSocket = exposeDockerSocket;
		this.env = env;
	}
	@Override
	public BuildStrategyType getType() {
		return BuildStrategyType.Custom;
	}

	@Override
	public Map<String, String> getEnvironmentVariables() {
		return env;
	}

	@Override
	public boolean exposeDockerSocket() {
		return exposeDockerSocket;
	}

	@Override
	public DockerImageURI getImage() {
		return image;
	}

}
