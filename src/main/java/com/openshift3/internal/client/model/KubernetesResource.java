/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client.model;

import java.util.HashMap;
import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift3.client.IClient;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.model.IResource;

/**
 * Resource is an abstract representation of a Kubernetes resource
 *
 */
public class KubernetesResource implements IResource{
	
	private static final String [] CREATION_TIMESTAMP = {"creationTimestamp"};
	private static final String [] LABELS = {"labels"};
	private static final String [] NAME = {"id"};
	private static final String [] NAMESPACE = {"namespace"};
	
	private ModelNode node;
	private IClient client;
	
	public KubernetesResource(){
		this(new ModelNode(), null);
	}
	public KubernetesResource(ModelNode node, IClient client){
		this.node = node;
		this.client = client;
		//TODO figure out how to handle version changes
		setApiVersion("v1beta1");
	}
	
	public KubernetesResource(String json){
		this.node = ModelNode.fromJSONString(json);
	}
	
	public IClient getClient(){
		return client;
	}
	
	public ModelNode getNode(){
		return node;
	}
	
	public void refresh(){
		//TODO find better way to bypass serialization/deserialization
		this.node = ModelNode.fromJSONString(client.get(getKind(), getName(), getNamespace()).toString());
	}
	
	// TODO Pretty certain this should be protected
	@Override
	public ResourceKind getKind(){
		if(node.has("kind")){
			return ResourceKind.valueOf(node.get("kind").asString());
		}
		return null;
	}
	
	@Override
	public String getApiVersion(){
		return node.get("apiVersion").asString();
	}
	
	public void setApiVersion(String version){
		node.get("apiVersion").set(version);
	}
	
	@Override
	public String getCreationTimeStamp(){
		return asString(CREATION_TIMESTAMP);
	}
	@Override
	public String getName(){
		return node.get(NAME).asString();
	}
	
	@Override
	public void setName(String name) {
		node.get(NAME).set(name);
	}
	
	@Override
	public String getNamespace(){
		return node.get(NAMESPACE).asString();
	}
	
	@Override
	public void setNamespace(String namespace){
		node.get(NAMESPACE).set(namespace);
	}

	@Override
	public void addLabel(String key, String value) {
		ModelNode labels = node.get("labels");
		labels.get(key).set(value);
	}
	
	
	@Override
	public Map<String, String> getLabels() {
		return asMap(LABELS);
	}
	
	/*---------- utility methods ------*/
	protected ModelNode get(String path){
		return node.get(path);
	}
	
	protected ModelNode get(String [] path){
		return node.get(path);
	}
	
	protected void set(String property, String value) {
		node.get(property).set(value);
	}
	
	protected void set(String propery, int value) {
		node.get(propery).set(value);
	}
	
	protected void set(String [] property, String value){
		node.get(property).set(value);
	}
	
	protected Map<String, String> asMap(String [] path){
		ModelNode node = this.node.get(path).asObject();
		HashMap<String, String> map = new HashMap<String, String>();
		for (String key : node.keys()) {
			map.put(key, node.get(key).asString());
		}
		return map;
	}
	
	protected int asInt(String property){
		return node.get(property).asInt();
	}
	
	protected int asInt(String [] path){
		return node.get(path).asInt();
	}
	
	protected String asString(String property){
		return node.get(property).asString();
	}
	protected String asString(String [] property){
		return node.get(property).asString();
	}

	@Override
	public String toString() {
		return node.toJSONString(false);
	}
	
	public String toPrettyString(){
		return node.toJSONString(false);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KubernetesResource other = (KubernetesResource) obj;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		return true;
	}
	
}


