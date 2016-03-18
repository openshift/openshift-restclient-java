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

import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.capability.ICapable;

/**
 * IResource is a representation of a Kubernetes resource (e.g. Service, Pod, ReplicationController) 
 * 
 * @author Jeff Cantrill
 */
public interface IResource extends ICapable, Annotatable {
	
	Map<String, String> getMetadata();

	/**
	 * @return the list of capabilities supported by this resource
	 */
	Set<Class<? extends ICapability>>  getCapabilities();
	
	/**
	 * @return the resource kind
	 */
	String getKind();
	
	/**
	 * @return the version of this resource
	 */
	String getApiVersion();

	/**
	 * Returns the timestamp of when this resource
	 * was created
	 * @return
	 */
	String getCreationTimeStamp();
	
	/**
	 * Returns the identifier for this resource
	 * @return
	 */
	String getName();
	
	/**
	 * Returns the scope of this resource
	 * @return
	 */
	String getNamespace();
	
	/**
	 * Return the project of the resource which
	 * corresponds to the namespace
	 * @return
	 */
	IProject getProject();
	
	/**
	 * Retrieves the labels associated with the resource
	 * @return
	 */
	Map<String, String> getLabels();
	
	/**
	 * Add or update a label;
	 * @param key
	 * @param value
	 */
	void addLabel(String key, String value);
	
	/**
	 * Returns <code>true</code> if the resource is annotated with
	 * the given key
	 * @param key
	 * @return true if the annotation key exists
	 */
	boolean isAnnotatedWith(String key);
	
	/**
	 * Retrieves the annotated value for the given key
	 * @param key
	 * @return
	 */
	String getAnnotation(String key);
	
	/**
	 * Set the resource annotation
	 * @param key
	 * @param value
	 */
	void setAnnotation(String key, String value);
	
	/**
	 * Removes the resource annotation
	 * @param key
	 */
	void removeAnnotation(String key);
	
	/**
	 * Retrieves the annotations associated with the resource
	 * @return
	 */
	Map<String, String> getAnnotations();

	String getResourceVersion();
	
	/**
	 * 
	 * @return the json string representing the resource
	 */
	String toJson();
	
	/**
	 * 
	 * @param compact true if the string should be compact; default: false
	 * @return the json string representing the resource
	 */
	String toJson(boolean compact);
}
