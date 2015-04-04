package com.openshift.internal.restclient.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

import com.openshift.restclient.IClient;
import com.openshift.restclient.model.IPod;

public class Pod extends KubernetesResource implements IPod {

	public Pod(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
	}

	@Override
	public String getIP() {
		return asString(POD_IP);
	}

	@Override
	public String getHost() {
		return asString(POD_HOST);
	}

	@Override
	public Collection<String> getImages() {
		Collection<String> images = new ArrayList<String>();
		ModelNode node = get(POD_CONTAINERS);
		if(node.getType() != ModelType.LIST) return images;
		for (ModelNode entry : node.asList()) {
			images.add(entry.get("image").asString());
		}
		return images;
	}

	@Override
	public String getStatus() {
		return asString(POD_STATUS);
	}
	
}
