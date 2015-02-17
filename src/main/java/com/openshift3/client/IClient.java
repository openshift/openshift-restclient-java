/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.client;

import java.net.URL;
import java.util.List;
import java.util.Map;

import com.openshift3.client.capability.ICapable;
import com.openshift3.client.model.IResource;

/**
 * Client is the the simplest interface for interacting with the OpenShift
 * master server.
 *
 */
public interface IClient extends ICapable{
	
	/**
	 * List all possible resources of the given kind in the default namespace
	 * @param kind
	 * @return
	 */
	<T extends IResource> List<T> list(ResourceKind kind);

	
	/**
	 * list the given given resource kind scoping it to a specific namespace
	 * 
	 * @param kind
	 * @param namespace    The namespace to scope the possible results of this list
	 * @return
	 */
	<T extends IResource> List<T> list(ResourceKind kind, String namespace);
	
	/**
	 * list the given given resource kind scoping it to a specific namespace
	 * 
	 * @param kind
	 * @param namespace    The namespace to scope the possible results of this list
	 * @param labels             The label used to filter the resource
	 * @return
	 */
	<T extends IResource> List<T> list(ResourceKind kind, String namespace, Map<String, String> labels);

	/**
	 * 
	 * @param service
	 * @param name
	 * @return
	 */
	<T extends IResource> T get(ResourceKind kind, String name, String namespace);
	
	/**
	 * @param resource
	 * @return
	 */
	<T extends IResource> T create(T resource);

	/**
	 * @param resource
	 */
	<T extends IResource> void delete(T resource);

	/**
	 * 
	 * @return the base URL of this endpoint
	 */
	URL getBaseURL();
	
	/**
	 * The OpenShift API version for this client
	 * @return
	 * @throws UnsupportedVersionException
	 */
	String getOpenShiftAPIVersion() throws UnsupportedVersionException;
	
	/**
	 * Connect to the OpenShift server and potentially
	 * returns a authorization context?
	 */
	AuthorizationContext authorize();
	
	static class AuthorizationContext {
	}
}
