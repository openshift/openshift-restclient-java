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

import java.util.List;

import com.openshift.restclient.model.IResource;

/**
 * Handler to receive notification when a resource
 * changes
 * @author Jeff Cantrill
 *
 */
public interface IOpenShiftWatchListener {
	
	/**
	 * Called when an endpoint connects
	 * The initial set of resources returned when determining
	 * the resourceVersion to watch
	 * @param resources  an Unmodifiable List
	 */
	void connected(List<IResource> resources);
	
	/**
	 * Called when and endpoint disconnects
	 */
	void disconnected();
	
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

	void error(Throwable err);
	
	/**
	 * Convenience class for implementing watch callbacks
	 * @author jeff.cantrill
	 *
	 */
	static class OpenShiftWatchListenerAdapter implements IOpenShiftWatchListener{

		@Override
		public void connected(List<IResource> resources) {
		}

		@Override
		public void disconnected() {
		}

		@Override
		public void received(IResource resource, ChangeType change) {
		}

		@Override
		public void error(Throwable err) {
		}
		
	}
}
