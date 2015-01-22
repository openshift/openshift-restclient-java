/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client.capability.resources;

import com.openshift3.client.IClient;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.capability.ICapability;
import com.openshift3.client.model.IResource;

/**
 * Determine if a resource has a capability if it 
 * has the given annotation
 */
public abstract class AnnotationCapability implements ICapability {

	private final IResource resource;
	private final IClient client;
	private final String name;

	public AnnotationCapability(String name, IResource resource, IClient client) {
		this.resource = resource;
		this.client = client;
		this.name = name;
	}
	
	@Override
	public boolean isSupported() {
		if(client == null) return false;
		return resource.isAnnotatedWith(getAnnotationKey());
	}
	
	/**
	 * Get the associated resource of the given kind using the
	 * name from the annotation key;
	 * @param kind
	 * @return
	 */
	protected <T extends IResource> T getAssociatedResource(ResourceKind kind){
		if(!isSupported()) return null;
		String name = getResource().getAnnotation(getAnnotationKey());
		return getClient().get(kind, name, getResource().getNamespace());
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	protected IResource getResource(){
		return this.resource;
	}
	
	protected IClient getClient(){
		return client;
	}
	/**
	 * The annotation key
	 * @return
	 */
	protected abstract String getAnnotationKey();

}
