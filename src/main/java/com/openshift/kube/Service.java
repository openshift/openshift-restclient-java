package com.openshift.kube;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.kube.Resource;


public class Service extends Resource {

	private static String [] SELECTOR = {"selector","name"};
	
	public Service (Client client) {
		this(new ModelNode(), client);
		setApiVersion("v1beta1");
		set("kind", ResourceKind.Service.toString());
	}
	
	public Service(ModelNode node, Client client) {
		super(node, client);
	}
	
	public void setPort(int port){
		set("port", port);
	}
	
	public int getPort(){
		return asInt("port");
	}
	
	public String getSelector(){
		return getNode().get(SELECTOR).asString();
	}
	
	public void setSelector(String value) {
		getNode().get(SELECTOR).set(value);
	}
	
	public void setContainerPort(int port){
		set("containerPort", port);
	}
	
	public int getContainerPort(){
		return asInt("containerPort");
	}

	public String getPortalIP() {
		return asString("portalIP");
	}
}
