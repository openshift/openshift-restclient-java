package com.openshift3.internal.client.model;

import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift3.client.IClient;
import com.openshift3.client.model.IPod;

public class Pod extends KubernetesResource implements IPod {

	public Pod(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
	}

	@Override
	public String getIP() {
		return asString(POD_IP);
	}
	
	
}
