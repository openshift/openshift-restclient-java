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
import com.openshift.restclient.model.build.ISTIBuildStrategy;

/**
 * @author Jeff Cantrill
 */
public class STIBuildStrategy implements ISTIBuildStrategy{

	private DockerImageURI image;
	private String scriptsLocation;
	private boolean incremental;
	private Map<String, String> envVars;

	public STIBuildStrategy(String image, String scriptsLocation, boolean incremental, Map<String, String> envVars) {
		this.image = new DockerImageURI(image);
		this.scriptsLocation = scriptsLocation;
		this.incremental = incremental;
		this.envVars = envVars;
	}

	@Override
	public BuildStrategyType getType() {
		return BuildStrategyType.STI;
	}

	@Override
	public DockerImageURI getImage() {
		return image;
	}

	@Override
	public String getScriptsLocation() {
		return scriptsLocation;
	}

	@Override
	public Map<String, String> getEnvironmentVariables() {
		return envVars;
	}

	@Override
	public boolean incremental() {
		return incremental;
	}

	@Override
	public boolean forceClean() {
		return !incremental;
	}

}
