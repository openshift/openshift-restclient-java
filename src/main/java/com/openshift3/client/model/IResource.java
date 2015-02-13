/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.client.model;

import java.util.Map;

import com.openshift3.client.ResourceKind;
import com.openshift3.client.capability.ICapability;
import com.openshift3.client.capability.ICapable;


/**
 * IResource is a representation of a Kubernetes resource (e.g. Service, Pod, ReplicationController) 
 */
public interface IResource extends ICapable{

	/**
	 * Determine if the client supports the desired capability
	 *  
	 * @param capability
	 * @return true if the client is able to offer this capability
	 */
	boolean supports(Class<? extends ICapability> capability);
	
	/**
	 * The resource kind
	 * @return
	 */
	ResourceKind getKind();
	
	/**
	 * The version of this resource
	 * @return
	 */
	String getApiVersion();

	/**
	 * Timestamp of when this resource
	 * was created
	 * @return
	 */
	String getCreationTimeStamp();
	
	/**
	 * The identifier for this resource
	 * @return
	 */
	String getName();
	
	/**
	 * Set the identifier for this resource
	 * @param name
	 */
	void setName(String name);
	
	/**
	 * The scope of this resource
	 * @return
	 */
	String getNamespace();
	
	/**
	 * The scope of this resource
	 * @param namespace
	 */
	void setNamespace(String namespace);
	
	/**
	 * Retrieve the labels associated with the resource
	 * @return
	 */
	Map<String, String> getLabels();
	
	void addLabel(String key, String value);
	
	/**
	 * Return true if the resource is annotated with
	 * the given key
	 * @param key
	 * @return true if the annotation key exists
	 */
	boolean isAnnotatedWith(String key);
	
	/**
	 * Retrieve the annotated value for the given key
	 * @param key
	 * @return
	 */
	String getAnnotation(String key);
	
	/**
	 * Retrieve the annotations associated with the resource
	 * @return
	 */
	Map<String, String> getAnnotations();

}
