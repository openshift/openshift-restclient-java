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
	//OpenShift Kinds
	Build("builds"),
	BuildConfig("buildconfigs"),
	DeploymentConfig("deploymentconfigs"),
	ImageStream("imagestreams"),
	OAuthAccessToken("oauthaccesstokens"),
	OAuthAuthorizeToken("oauthauthorizetokens"),
	OAuthClient("oauthclients"),
	OAuthClientAuthorization("oauthclientauthorizations"),
	Policy("policies"),
	PolicyBinding("policybindings"),
	Project("projects"),
	ProjectRequest("projectrequests"),
	Role("roles"),
	RoleBinding("rolebindings"),
	Route("routes"),
	Template("templates"),
	User("users"),
	
	//Kubernetes Kinds
	Event("events"),
	LimitRange("limitranges"),
	Pod("pods"),
	ReplicationController("replicationcontrollers"),
	ResourceQuota("resourcequotas"), 
	Service("services"), 
	Secret("secrets"), 
	/*
	 * These are not true resources that can be used (mostly) in
	 * RESTful operations
	 */
	@Deprecated
	Config(""), //not rest resource,
	List(""),
	Status(""), //not rest resource
	@Deprecated
	TemplateConfig("templateconfig"),//mechanism for processing templates pre v1beta3
	ProcessedTemplates("processedtemplates"),//mechanism for processing templates
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
