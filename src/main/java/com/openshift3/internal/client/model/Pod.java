package com.openshift3.internal.client.model;

import org.jboss.dmr.ModelNode;

import com.openshift3.client.IClient;

public class Pod extends KubernetesResource {

	public Pod(ModelNode node, IClient client) {
		super(node, client);
	}
	
	
}
