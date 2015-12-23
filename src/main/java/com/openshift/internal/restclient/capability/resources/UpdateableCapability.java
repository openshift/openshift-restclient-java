package com.openshift.internal.restclient.capability.resources;

import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.restclient.capability.resources.IUpdatable;
import com.openshift.restclient.model.IResource;

public class UpdateableCapability implements IUpdatable {

	private boolean isSupported;
	private KubernetesResource resource;
	public UpdateableCapability(IResource resource) {
		if(resource instanceof KubernetesResource) {
			isSupported = true;
			this.resource = (KubernetesResource) resource;
		}
	}

	@Override
	public boolean isSupported() {
		return isSupported;
	}

	@Override
	public String getName() {
		return UpdateableCapability.class.getSimpleName();
	}

	@Override
	public void updateFrom(IResource source) {
		if(source instanceof KubernetesResource) {
			KubernetesResource from = (KubernetesResource) source;
			resource.setNode(from.getNode());
		}
	}

}
