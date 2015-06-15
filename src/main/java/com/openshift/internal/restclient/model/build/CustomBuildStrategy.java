/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.build;

import java.util.Map;

import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.build.BuildStrategyType;
import com.openshift.restclient.model.build.ICustomBuildStrategy;

/**
 * @author Jeff Cantrill
 */
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
	public String getType() {
		return BuildStrategyType.CUSTOM;
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
