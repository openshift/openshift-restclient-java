package com.openshift.internal.kube;

import org.jboss.dmr.ModelNode;

import com.openshift.kube.Client;
import com.openshift.kube.ResourceKind;

/**
 * Resource is an abstract representation of a Kubernetes resource
 *
 */
public class Resource {
	
	private ModelNode node;
	private Client client;
	
	public Resource(){
		this(new ModelNode(), null);
	}
	public Resource(ModelNode node, Client client){
		this.node = node;
		this.client = client;
	}
	
	private static final String METADATA = "metadata";
	private static final String [] CREATION_TIMESTAMP = {METADATA,"creationTimestamp"};
	private static final String [] NAME = {METADATA,"name"};
	private static final String [] NAMESPACE = {METADATA, "namespace"};
	
	public Resource(String json){
		this.node = ModelNode.fromJSONString(json);
	}
	
	public Client getClient(){
		return client;
	}
	
	public void refresh(){
		this.node = client.get(getKind(), getName(), getNamespace()).getNode();
	}
	
	// TODO Pretty certain this should be protected
	public ResourceKind getKind(){
		if(node.has("kind")){
			return ResourceKind.valueOf(node.get("kind").asString());
		}
		return null;
	}
	
	public String getApiVersion(){
		return node.get("apiVersion").asString();
	}
	
	public void setApiVersion(String version){
		node.get("apiVersion").set(version);
	}
	
	protected ModelNode getNode(){
		return node;
	}
	
	public String getCreationTimeStamp(){
		return node.get(CREATION_TIMESTAMP).asString();
	}
	public String getName(){
		return node.get(NAME).asString();
	}
	
	public void setName(String name) {
		node.get(NAME).set(name);
	}
	
	public String getNamespace(){
		return node.get(NAMESPACE).asString();
	}
	
	public void setNamespace(String namespace){
		node.get(NAMESPACE).set(namespace);
	}

	public void addLabel(String key, String value) {
		ModelNode labels = node.get("labels");
		labels.get(key).set(value);
	}

	protected void set(String property, String value) {
		node.get(property).set(value);
	}
	
	protected void set(String propery, int value) {
		node.get(propery).set(value);
	}
	
	protected ModelNode get(String name){
		return node.get(name);
	}
	
	protected int asInt(String property){
		return node.get(property).asInt();
	}
	
	protected String asString(String property){
		return node.get(property).asString();
	}

	@Override
	public String toString() {
		return node.toJSONString(true);
	}
	
	public String toPrettyString(){
		return node.toJSONString(false);
	}

}


