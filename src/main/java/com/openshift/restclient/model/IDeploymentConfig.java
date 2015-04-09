/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.model;

import java.util.Map;

/**
 * @author Jeff Cantrill
 */
public interface IDeploymentConfig extends IResource {

	/**
	 * Returns the number of replicas to be created by the replication
	 * controller generated from this deployment config
	 * @return
	 */
	int getReplicas();
	
	/**
	 * Returns the replica selector to be used by the replication
	 * controller generated from this deployment config
	 * 
	 * @return java.util.Map<String, String>
	 */
	Map<String, String> getReplicaSelector();
}
