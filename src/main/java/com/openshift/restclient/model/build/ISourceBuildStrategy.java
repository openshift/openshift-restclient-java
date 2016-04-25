/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.restclient.model.build;

import java.util.Collection;
import java.util.Map;

import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.IEnvironmentVariable;

/**
 * 
 * @author Jeff Cantrill
 *
 */
public interface ISourceBuildStrategy extends IBuildStrategy{
	
	/**
	 * Returns the Builder Image used to execute the build
	 */
	DockerImageURI getImage();
	void setImage(DockerImageURI image);
	
	String getFromKind();
	void setFromKind(String kind);

	String getFromNamespace();
	void setFromNamespace(String namespace);
	
	String getScriptsLocation();
	void setScriptsLocation(String location);
	
	Map<String, String> getEnvironmentVariables();
	void setEnvironmentVariables(Map<String, String> envVars);
	
	Collection<IEnvironmentVariable> getEnvVars();
	
	/**
	 * Setting using a null collection will early return without
	 * modification to the strategy
	 * @param envVars
	 */
	void setEnvVars(Collection<IEnvironmentVariable> envVars);

	boolean incremental();
	void setIncremental(boolean isIncremental);

}
