/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.client.model;

import java.util.List;

import com.openshift3.client.model.build.IBuildSource;
import com.openshift3.client.model.build.IBuildStrategy;
import com.openshift3.client.model.build.IBuildTrigger;

public interface IBuildConfig extends IResource {

	/**
	 * Return the source URL for a build
	 * @return
	 */
	String getSourceURI();
	
	/**
	 * The policies which will trigger a build
	 * @return
	 */
	List<IBuildTrigger> getBuildTriggers();
	
	/**
	 * The source info of the build
	 * @return
	 */
	<T extends IBuildSource> T getBuildSource();
	
	/**
	 * The strategy to for building the source
	 * @return
	 */
	<T extends IBuildStrategy> T getBuildStrategy(); 	
	
	/**
	 * Retrieve the name of the repository where the
	 * resulting build image will be pushed
	 * @return
	 */
	String getOutputRepositoryName();
}
