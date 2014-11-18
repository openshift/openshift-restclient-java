package com.openshift.kube;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.kube.Resource;

public class Container extends Resource{

	public Container(ModelNode node, Client client) {
		super(node, client);
	}

	public String getImage(){
		return asString("image");
	}
}
