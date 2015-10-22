/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.model;

import java.io.PrintStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.openshift.restclient.images.DockerImageURI;
import com.openshift.restclient.model.deploy.IDeploymentTrigger;

/**
 * @author Jeff Cantrill
 */
public interface IDeploymentConfig extends IResource {
	
	/**
	 * Add or update a label to the template spec;
	 * @param key
	 * @param value
	 */
	void addTemplateLabel(String key, String value);

	/**
	 * Returns the number of replicas to be created by the replication
	 * controller generated from this deployment config
	 * @return
	 */
	int getReplicas();
	
	void setReplicas(int replicas);
	
	/**
	 * Returns the replica selector to be used by the replication
	 * controller generated from this deployment config
	 * 
	 * @return java.util.Map<String, String>
	 */
	Map<String, String> getReplicaSelector();
	
	/**
	 * Set the selector by completely replacing the values
	 * that were there before
	 * @param selector
	 */
	void setReplicaSelector(Map<String, String> selector);
	
	/**
	 * Convenience method to set the selector when there
	 * is a single key/value pair
	 * @param key
	 * @param value
	 */
	void setReplicaSelector(String key, String value);
	
	/**
	 * Get the list of deployment triggers
	 * @return a collection of trigger types
	 */
	Collection<String> getTriggerTypes();

	/**
	 * Convenience method to get the deployment
	 * strategy type
	 * @return the type as a string
	 */
	String getDeploymentStrategyType();
	
	/**
	 * Add a container to the pod that will be spun up as 
	 * part of this deployment.
	 * 
	 * @param name           the name of the container
	 * @param tag            the docker uri
	 * @param containerPorts  the container ports
	 * @param volumes		 the set of emptyDir volumes to add to the config
	 */
	void addContainer(String name, DockerImageURI tag,  Set<IPort> containerPorts, Map<String, String> envVars, List<String> volumes);

	/**
	 * Add a container to the pod that will be spun up as 
	 * part of this deployment, defaulting the name to the image name
	 * 
	 * @param tag            the docker uri
	 * @param containerPorts  the container ports
	 */
	void addContainer(DockerImageURI tag, Set<IPort> containerPorts, Map<String, String> envVars);
	
	/**
	 * Add a trigger of the given type
	 * or null if the type is unrecognized
	 *  
	 * @param type
	 * @return
	 */
	IDeploymentTrigger addTrigger(String type);
	
	/**
	 * Get the latest version number
	 * @return
	 */
	int getLatestVersionNumber();
	
	/**
	 * Set the latest version number
	 * @param new version number
	 * 
	 */
	void setLatestVersionNumber(int newVersionNumber);
	
	/**
	 * Return whether deployments have fired because of triggers
	 * @return
	 */
	boolean haveTriggersFired();
	
	/**
	 * Return whether deployments have fired based on an image trigger
	 * for a particular image
	 * @param imageNameTag 	the image name:tag associated with an image trigger
	 * @return
	 */
	boolean didImageTrigger(String imageNameTag);
	
	/**
	 * Get the image hexadecimal ID for the image tag used with the
	 * latest image change trigger
	 * @param imageNameTag	the image name:tag associated with an image trigger
	 * @return
	 */
	String getImageHexIDForImageNameAndTag(String imageNameTag);
	
	/**
	 * Get the image name:tag from a image change trigger firing
	 * @return
	 */
	String getImageNameAndTagForTriggeredDeployment();
}
