/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.client.model;

import com.openshift3.client.ResourceKind;


/**
 * IResource is a representation of a Kubernetes resource (e.g. Service, Pod, ReplicationController) 
 */
public interface IResource {

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
	
}
