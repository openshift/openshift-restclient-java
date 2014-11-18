package com.openshift.kube;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.kube.Resource;

public class Status extends Resource {

	public Status(ModelNode node, Client client) {
		super(node, client);
	}

	public Status(String json) {
		super(json);
	}
	
	public String getMessage(){
		return getNode().get("message").asString();
	}

}
