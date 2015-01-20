package com.openshift3.internal.client.model;

import org.jboss.dmr.ModelNode;

import com.openshift3.client.IClient;
import com.openshift3.client.model.IPod;

public class Pod extends KubernetesResource implements IPod {

	public Pod(){
		super();
	}
	
	public Pod(ModelNode node, IClient client) {
		super(node, client);
	}
	
	
}
