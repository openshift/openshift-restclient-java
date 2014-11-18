package com.openshift.kube.capability;

import java.util.Map;

import com.openshift.kube.Client;

public class CapabilityInitializer {

	public CapabilityInitializer() {
	}

	public void populate(Map<Class<? extends Capability>, Capability> capabilities, Client client) {
		ImageRegistryHosting imageRegistry = new DefaultImageRegistryHosting(client);
		if(imageRegistry.exists()){
			capabilities.put(ImageRegistryHosting.class, imageRegistry);
		}
	}

}
