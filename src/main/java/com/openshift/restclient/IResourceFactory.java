/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient;

import java.util.List;

import com.openshift.restclient.model.IResource;

/**
 * Factory class for creating resources from a 
 * response string
 * 
 * @author Jeff Cantrill
 */
public interface IResourceFactory {
	
	/**
	 * Create a list of resources of the given kind
	 * from a response string
	 * @param json
	 * @param kind
	 * @return
	 */
	List<IResource> createList(String json, ResourceKind kind);
	
	/**
	 * Create a resource from a response string
	 * @param response
	 * @return
	 */
	<T extends IResource> T create(String response) ;

	/**
	 * Create a resource for a given version and kind
	 * @param version
	 * @param kind
	 * @return
	 */
	<T extends IResource> T create(String version, ResourceKind kind);
	
	
}
