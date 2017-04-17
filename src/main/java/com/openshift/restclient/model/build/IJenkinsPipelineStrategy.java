/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.model.build;

import java.util.Collection;

import com.openshift.restclient.model.IEnvironmentVariable;

/**
 * @author Andre Dietisheim
 */
public interface IJenkinsPipelineStrategy extends IBuildStrategy {
	
	static final String JENKINS_FILE = "jenkinsPipelineStrategy.jenkinsfile";
	static final String JENKINS_FILE_PATH = "jenkinsPipelineStrategy.jenkinsfilePath";
	static final String ENV = "jenkinsPipelineStrategy.env";

	void setJenkinsfilePath(String filePath);
	String getJenkinsfilePath();

	void setJenkinsfile(String jenkinsFile);
	String getJenkinsfile();

	Collection<IEnvironmentVariable> getEnvVars();
	void setEnvVars(Collection<IEnvironmentVariable> envVars);
}
