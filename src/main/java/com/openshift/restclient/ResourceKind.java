/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient;

/**
 * ResourceKind are the various types of Kubernetes
 * resources that are of interest
 *
 */
// TODO: evaluate replacing this by constants/class since enums are not extendable
public enum ResourceKind {
	
	Build("builds"),
	BuildConfig("buildConfigs"),
	DeploymentConfig("deploymentConfigs"),
	ImageStream("imageStreams"),
	Project("projects"),
	Pod("pods"),
	ReplicationController("replicationControllers"),
	Route("routes"),
	Service("services"), 
	Template("templates"),
	
	/*
	 * These are not true resources that can be used (mostly) in
	 * RESTful operations
	 */
	Config(""), //not rest resource,
	List(""),
	Status(""), //not rest resource
	TemplateConfig("templateConfigs") //mechanism for processing templates
	;

	// punting here for now
	private final String plural;
	
	ResourceKind(String plural){
		this.plural = plural;
	}
	
	public String pluralize() {
		return plural;
	}
}
