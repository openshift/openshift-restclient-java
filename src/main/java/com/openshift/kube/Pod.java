package com.openshift.kube;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.kube.Resource;

public class Pod extends Resource {

	public Pod(ModelNode node, Client client) {
		super(node, client);
	}
	
	
}
