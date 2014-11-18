package com.openshift.kube.capability;

import com.openshift.internal.kube.Resource;
import com.openshift.kube.Client;
import com.openshift.kube.OpenShiftKubeException;
import com.openshift.kube.ResourceKind;
import com.openshift.kube.Service;

public class DefaultImageRegistryHosting implements ImageRegistryHosting {

	private Client client;
	private Service service;

	public DefaultImageRegistryHosting(Client client) {
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
	public boolean exists() {
		Resource resource;
		try{
			resource = client.get(ResourceKind.Service, "docker-registry", "");
		}catch(OpenShiftKubeException e){
			resource = e.getStatus();
		}
		if(resource.getKind() == ResourceKind.Service){
			this.service = (Service) resource;
			return true;
		}
		return false;
	}
}
