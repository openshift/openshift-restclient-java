package com.openshift3.client.capability;

import java.util.Map;

import com.openshift3.client.IClient;
import com.openshift3.internal.client.capability.DefaultImageRegistryHosting;

public class CapabilityInitializer {

	public CapabilityInitializer() {
	}

	public void populate(Map<Class<? extends Capability>, Capability> capabilities, IClient client) {
		ImageRegistryHosting imageRegistry = new DefaultImageRegistryHosting(client);
		if(imageRegistry.exists()){
			capabilities.put(ImageRegistryHosting.class, imageRegistry);
		}
	}

}
