/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.model;

import java.util.Map;
import java.util.Set;

import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.capability.ICapable;


/**
 * IResource is a representation of a Kubernetes resource (e.g. Service, Pod, ReplicationController) 
 */
public interface IResource extends ICapable{

	/**
	 * Retrieve the list of capabilities supported by this resource
	 * @return
	 */
	Set<Class<? extends ICapability>>  getCapabilities();
	
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
