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

/**
 * A list of resources.
 * 
 * @author Jeff Cantrill
 */
public interface IList extends IResource{
	/**
	 * Retrieve the list of resources for this config
	 * @return
	 */
	Collection<IResource> getItems();

	/**
	 * Add all of the given resources to the list
	 * @param items
	 */
	void addAll(Collection<IResource> items);
}
