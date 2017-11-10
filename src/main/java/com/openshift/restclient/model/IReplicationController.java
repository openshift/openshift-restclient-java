/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.probe.IProbe;
import com.openshift.restclient.model.volume.IVolumeSource;

/**
 * @author Jeff Cantrill
 */
public interface IReplicationController  extends IResource{
	
	static final String DEPLOYMENT_PHASE = "openshift.io/deployment.phase";
	
	/**
	 * Set an environment variable to the given name and
	 * value on the first container in the list of containers
	 * 
	 * @param name
	 * @param value
	 */
	void setEnvironmentVariable(String name, String value);

	/**
	 * Set an environment variable to the given name and
	 * value on the given container.  Returns silently
	 * if the containerName is not found
	 * 
	 * @param containerName
	 * @param name
	 * @param value
	 */
	void setEnvironmentVariable(String containerName, String name, String value);
	
	/**
	 * Removes an environment variable with the given name.
	 * Returns silently if the name is not found
	 * 
	 * @throws IllegalArgumentException if name is null
	 * 
	 * @param name
	 */
	public void removeEnvironmentVariable(String name);
	
	/**
	 * Removes an environment variable with the given name and
	 * value on the given container.  Returns silently
	 * if the containerName or the name is not found
	 * 
	 * @throws IllegalArgumentException if name is null
	 * 
	 * @param containerName
	 * @param name
	 */
	public void removeEnvironmentVariable(String containerName, String name);
	
	/**
	 * Return the list of env vars of the first container
	 * @return
	 */
	Collection<IEnvironmentVariable> getEnvironmentVariables();

	/**
	 * Return the list of env vars for the given container or an empty list
	 * if the container is not found
	 * @param containerName
	 * @return
	 */
	Collection<IEnvironmentVariable> getEnvironmentVariables(String containerName);
	
	/**
	 * Returns the desired number of replicas
	 * @return
	 */
	int getDesiredReplicaCount();
	int getReplicas();
	
	void setReplicas(int count);
	
	/**
	 * Sets a new desired number of replicas
	 * @param new number of replicas
	 */
	void setDesiredReplicaCount(int numOfReplicas);
	
	/**
	 * Returns the current number of replicas
	 * @return
	 */
	int getCurrentReplicaCount();
	
	/**
	 * Returns the selector used by the controller
	 * @return
	 */
	Map<String, String> getReplicaSelector();
	
	/**
	 * Set the selector using the map of values
	 * @param selector
	 */
	void setReplicaSelector(Map<String, String> selector);
	void setReplicaSelector(String key, String value);
	
	/**
	 * Retrieves the list of images deployed in the
	 * pod containers from this controller
	 * @return
	 */
	Collection<String> getImages();

	/**
	 * Add a container to the pod that will be spun up as 
	 * part of this deployment.
	 * 
	 * @param name           the name of the container
	 * @param tag            the docker uri
	 * @param containerPorts  the container ports
	 * @param volumes		 the set of emptyDir volumes to add to the config
	 */
	IContainer addContainer(String name, DockerImageURI tag,  Set<IPort> containerPorts, Map<String, String> envVars, List<String> volumes);

	/**
	 * Add a container to the pod that will be spun up as 
	 * part of this deployment, defaulting the name to the image name
	 * 
	 * @param tag            the docker uri
	 * @param containerPorts  the container ports
	 */
	IContainer addContainer(DockerImageURI tag, Set<IPort> containerPorts, Map<String, String> envVars);

	/**
	 * Add a container with the given name 
	 * @param name
	 * @return
	 */
	IContainer addContainer(String name);
	
	/**
	 * Retrieve a container by name or null if it is not found
	 * @param name
	 * @return the container or null if not found
	 */
	IContainer getContainer(String name);

	/**
	 * Retrieve the containers defined in spec
	 * @param name
	 * @return collection of containers or empty collection
	 */
	Collection<IContainer> getContainers();
	
	/**
	 * Returns the labels for the template in this replication controller
	 * @return
	 */
	Map<String, String> getTemplateLabels();

	/**
	 * Add or update a label to the template spec;
	 * @param key
	 * @param value
	 */
	void addTemplateLabel(String key, String value);
	
	/**
	 * The volumes associated with the pod spec
	 * @return
	 */
	Set<IVolumeSource> getVolumes();

	/**
	 * Adds a volume to the pod spec. If a volume with the same name already exists, the volume is not added.
	 *
	 * @param volumeSource The volume to add to the pod spec
     */
	void addVolume(IVolumeSource volumeSource);
	
	/**
	 * Add a volume source of the given type with the given name.  Unimplemented types
	 * will return a generic volumesource impl
	 * 
	 * @param volumetype
	 * @param name
	 * @return
	 */
	<T extends IVolumeSource> T addVolume(String volumetype, String name);

	/**
	 * Sets the volumes associated with the pod spec. Existing volumes will be overwritten.
	 *
	 * @param volumes The volumes to assign to the pod spec.
     */
	void setVolumes(Set<IVolumeSource> volumes);

	void setContainers(Collection<IContainer> containers);
	
	/**
     * Sets the service account to run this replication controller under. 
     *
     * @param volumes The service account to assign to the spec.
     */
	void setServiceAccountName(String serviceAccount);
    
    /**
     * Retrieves the service account used by the controller
     * @return
     */
    String getServiceAccountName();

}
