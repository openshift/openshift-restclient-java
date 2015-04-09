/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.model.template;

import java.util.Collection;
import java.util.Map;

import com.openshift.restclient.model.IResource;

/**
 * Representation of a set of pre-configured parameterized set of resources
 * 
 * @author Jeff Cantrill
 */
public interface ITemplate extends IResource {
	
	/**
	 * Retrieves the list of resources this template
	 * creates
	 * @return
	 */
	Collection<IResource> getItems();
	
	/**
	 * Returns a map of parameter names to parameters.
	 * @return
	 */
	Map<String, IParameter> getParameters();
}
