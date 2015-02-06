/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.client;

/**
 * ResourceKind are the various types of Kubernetes
 * resources that are of interest
 *
 */
public enum ResourceKind {
	
	Build("builds"),
	BuildConfig("buildConfigs"),
	DeploymentConfig("deploymentConfigs"),
	ImageRepository("imageRepositories"),
	Project("projects"),
	Pod("pods"),
	ReplicationController("replicationControllers"),
	Status(""),
	Service("services");

	// punting here for now
	private final String plural;
	
	ResourceKind(String plural){
		this.plural = plural;
	}
	
	public String pluralize() {
		return plural;
	}
}
