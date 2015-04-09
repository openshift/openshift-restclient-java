/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.capability.resources;

import java.util.Collection;

import com.openshift.restclient.capability.ICapability;

/**
 * Determine if resource is tagged with categories
 * of technologies it includes
 * 
 * @author Jeff Cantrill
 */
public interface ITags extends ICapability {
	
	/**
	 * The list of tags
	 */
	Collection<String> getTags();
}
