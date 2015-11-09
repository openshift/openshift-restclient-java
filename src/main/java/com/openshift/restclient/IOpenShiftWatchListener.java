/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.restclient;

import com.openshift.restclient.model.IResource;

/**
 * Handler to receive notification when a resource
 * changes
 * @author Jeff Cantrill
 *
 */
public interface IOpenShiftWatchListener {
	
	/**
	 * Called after the watch is started
	 */
	void started();
	
	/**
	 * Called when the watch stops
	 */
	void stopped();
	
	/**
	 * 
	 * @param resource   the resource that changed
	 * @param change     the change type
	 */
	void received(IResource resource, ChangeType change);
	
	enum ChangeType{
		
		ADDED,
		MODIFIED,
		DELETED
	}
}
