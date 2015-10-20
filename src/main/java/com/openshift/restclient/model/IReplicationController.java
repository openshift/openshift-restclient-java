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
import java.util.Map;

/**
 * @author Jeff Cantrill
 */
public interface IReplicationController  extends IResource{
	
	/**
	 * Returns the desired number of replicas
	 * @return
	 */
	int getDesiredReplicaCount();
	
	/**
	 * Sets a new desired number of replicas
	 * @param new number of replicas
	 */
	void setDesiredReplicaCount(int numOfReplicas);
	
	/**
	 * Returns the current number of replicas
	 * @return
	 */
	int getCurrentReplicaCount();
	
	/**
	 * Returns the selector used by the controller
	 * @return
	 */
	Map<String, String> getReplicaSelector();
	
	/**
	 * Retrieves the list of images deployed in the
	 * pod containers from this controller
	 * @return
	 */
	Collection<String> getImages();
	
}
