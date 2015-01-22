/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client.capability.server;

import com.openshift3.client.IClient;
import com.openshift3.client.OpenShiftException;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.capability.server.IImageRegistryHosting;
import com.openshift3.internal.client.model.KubernetesResource;
import com.openshift3.internal.client.model.Service;

public class DefaultImageRegistryHosting implements IImageRegistryHosting {

	private IClient client;
	private Service service;

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
		KubernetesResource resource;
		try{
			resource = client.get(ResourceKind.Service, "docker-registry", "");
		}catch(OpenShiftException e){
			resource = e.getStatus();
		}
		if(resource.getKind() == ResourceKind.Service){
			this.service = (Service) resource;
			return true;
		}
		return false;
	}
}
