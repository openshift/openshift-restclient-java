/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.model;

import java.util.List;

import com.openshift.restclient.model.build.IBuildSource;
import com.openshift.restclient.model.build.IBuildStrategy;
import com.openshift.restclient.model.build.IBuildTrigger;

/**
 * @author Jeff Cantrill
 */
public interface IBuildConfig extends IResource {

	/**
	 * Returns the source URL for a build
	 * @return
	 */
	String getSourceURI();
	
	/**
	 * Returns the policies which will trigger a build
	 * @return
	 */
	List<IBuildTrigger> getBuildTriggers();
	
	/**
	 * Add a trigger to the list of triggers for this build.
	 *
	 * @param trigger
	 */
	void addBuildTrigger(IBuildTrigger trigger);

	/**
	 * Returns the source info of the build
	 * @return
	 */
	<T extends IBuildSource> T getBuildSource();

	/**
	 * Set the source for the build.
	 *
	 * @param source
	 */
	void setBuildSource(IBuildSource source);

	/**
	 * Returns the strategy to for building the source
	 * @return
	 */
	<T extends IBuildStrategy> T getBuildStrategy(); 	

	/**
	 * Set the strategy for how the build should be built. <br/>
	 * Depending on the strategies available on the server this
	 * could be 'source', 'docker' or 'custom'.
	 *
	 * @param strategy
	 */
	void setBuildStrategy(IBuildStrategy strategy);
	
	/**
	 * Retrieves the name of the repository where the
	 * resulting build image will be pushed
	 * @return
	 */
	String getOutputRepositoryName();
}
