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
package com.openshift.restclient.model;

import java.util.Map;
import java.util.Set;

import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.volume.IVolume;
import com.openshift.restclient.model.volume.IVolumeMount;

public interface IContainer {

	void setName(String name);
	String getName();
	
	void setImage(DockerImageURI tag);
	DockerImageURI getImage();
	
	/**
	 * replace the env vars
	 * @param vars
	 */
	void setEnvVars(Map<String, String> vars);
	/**
	 * 
	 * @return an unmodifiable map of env vars
	 */
	Map<String, String> getEnvVars();
	
	void addEnvVar(String key, String value);
	
	/**
	 * replaces the set of ports
	 * @param ports
	 */
	void setPorts(Set<IPort> ports);
	
	/**
	 * 
	 * @return an unmodifiable set of the container ports
	 */
	Set<IPort> getPorts();
	
	void setImagePullPolicy(String policy);
	String getImagePullPolicy();
	
	void setLifecycle(String lifecycle);
	String getLifecycle();
	
	@Deprecated
	void setVolumes(Set<IVolume> volumes);
	@Deprecated
	Set<IVolume>  getVolumes();

	void setVolumeMounts(Set<IVolumeMount> volumes);
	Set<IVolumeMount> getVolumeMounts();
	
	String toJSONString();
}
