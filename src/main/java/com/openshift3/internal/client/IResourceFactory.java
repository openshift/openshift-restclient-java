/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client;

import java.util.List;

import com.openshift3.client.ResourceKind;
import com.openshift3.client.model.IResource;

/**
 * Factory class for creating resources from a 
 * response string
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
}
