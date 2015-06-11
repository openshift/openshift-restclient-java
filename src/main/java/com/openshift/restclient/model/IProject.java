/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.model;

import java.util.List;

import com.openshift.restclient.ResourceKind;

/**
 * @author Jeff Cantrill
 */
public interface IProject extends IResource {

	/**
	 * Retrieves resource of the given kind that are scoped to
	 * this project
	 * @param kind
	 * @return List<IResources>
	 */
	<T extends IResource> List<T> getResources(ResourceKind kind);

	String getDisplayName();
	
}
