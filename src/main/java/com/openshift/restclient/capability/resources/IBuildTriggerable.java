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
package com.openshift.restclient.capability.resources;

import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.model.IBuild;

import java.util.List;

/**
 * Capability to trigger a build based on the build configuration
 * @author Jeff Cantrill
 *
 */
public interface IBuildTriggerable extends ICapability {
	
	/**
	 * Trigger a build based on a build config
	 * @return The build that was triggered
	 */
	IBuild trigger();
	
	/**
	 * Trigger a build with the given source level commit id
	 * @param commitId
	 * @return The build that was triggered
	 * @deprecated
	 * 		Replaced by calling {@link #setCommitId(String)}, followed
	 * 		by {@link #trigger()}.
	 */
	IBuild trigger(String commitId);
	
	/**
	 * Set the commit level for the SCM extraction
	 * of the source code the build operates against
	 * @param commitId  the specific hexadecimal commit ID associated with a SCM log level
	 */
	void setCommitId(String commitId);
	
	/**
	 * Get the commit level for the SCM extraction
	 * of the source code the build operates against
	 * @return the specific hexadecimal commit ID associated with a SCM log level
	 */
	String getCommitId();
	
	/**
	 * Add a human readable short explanation of why this build request was issued
	 * @param cause the description to add to the list of causes for this request
	 */
	void addBuildCause(String cause);
	
	/**
	 * Get the list of human readable short explanations of why this build request was issued
	 * @return list of reasons for the build
	 */
	List<String> getBuildCauses();

	/**
	 * Sets an environment variable for this build request
	 * @param name The name of the environment variable
	 * @param value The value of the environment variable
	 */
	void setEnvironmentVariable(String name, String value);

}
