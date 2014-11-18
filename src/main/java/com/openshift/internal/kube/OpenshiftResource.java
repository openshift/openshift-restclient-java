package com.openshift.internal.kube;

import org.jboss.dmr.ModelNode;

import com.openshift.kube.Client;

public class OpenshiftResource extends Resource {

	public OpenshiftResource(ModelNode node, Client client) {
		super(node, client);
		setApiVersion("v1beta1");
	}
	
	public OpenshiftResource(String json){
		super(json);
	}

	private static final String [] NAME = {"metadata", "name"};
	private static final String [] NAMESPACE = {"metadata", "namespace"};

	@Override
	public String getName() {
		return getNode().get(NAME).asString();
	}

	@Override
	public String getNamespace() {
		return getNode().get(NAMESPACE).asString();
	}

	@Override
	public void setName(String name) {
		getNode().get(NAME).set(name);
	}

	@Override
	public void setNamespace(String namespace) {
		getNode().get(NAMESPACE).set(namespace);
	}
	
	
	
}
