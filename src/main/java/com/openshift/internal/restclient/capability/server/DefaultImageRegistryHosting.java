/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.capability.server;

import com.openshift.restclient.IClient;
import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.server.IImageRegistryHosting;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.IService;

/**
 * @author Jeff Cantrill
 */
public class DefaultImageRegistryHosting implements IImageRegistryHosting {

	private IClient client;
	private IService service;

	public DefaultImageRegistryHosting(IClient client) {
		this.client = client;
	}

	@Override
	public String getName() {
		return "dockerImageRegistry";
	}

	@Override
	public String getRegistryUri() {
		return String.format("%s:%s", service.getPortalIP(), service.getPort());
	}

	@Override
	public boolean isSupported() {
		IResource resource;
		try{
			resource = client.get(ResourceKind.Service, "docker-registry", "");
		}catch(OpenShiftException e){
			resource = e.getStatus();
		}
		if(resource.getKind() == ResourceKind.Service){
			this.service = (IService) resource;
			return true;
		}
		return false;
	}
}
