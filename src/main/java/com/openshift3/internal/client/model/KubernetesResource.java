/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client.model;

import static com.openshift3.client.capability.CapabilityInitializer.initializeCapability;

import java.util.HashMap;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

import com.openshift3.client.IClient;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.capability.ICapability;
import com.openshift3.client.capability.resources.IDeploymentConfigTraceability;
import com.openshift3.client.capability.resources.IDeploymentTraceability;
import com.openshift3.client.capability.resources.ITemplateTraceability;
import com.openshift3.client.model.IResource;
import com.openshift3.internal.client.capability.resources.AnnotationDeploymentConfigTraceability;
import com.openshift3.internal.client.capability.resources.AnnotationDeploymentTraceability;
import com.openshift3.internal.client.capability.resources.AnnotationTemplateTraceability;

/**
 * Resource is an abstract representation of a Kubernetes resource
 *
 */
public class KubernetesResource implements IResource, ResourcePropertyKeys{
	
	private ModelNode node;
	private IClient client;
	private Map<Class<? extends ICapability>, ICapability> capabilities = new HashMap<Class<? extends ICapability>, ICapability>();
	private Map<String, String []> propertyKeys;
	
	public KubernetesResource(){
		this(new ModelNode(), null, null);
	}
	
	public KubernetesResource(String json){
		this.node = ModelNode.fromJSONString(json);
		initializeCapabilities();
	}
	
	public KubernetesResource(ModelNode node, IClient client, Map<String, String []> propertyKeys){
		this.node = node;
		this.client = client;
		this.propertyKeys = propertyKeys;
		initializeCapabilities();
	}
	
	protected void initializeCapabilities(){
		initializeCapability(capabilities, ITemplateTraceability.class, new AnnotationTemplateTraceability(this));
		initializeCapability(capabilities, IDeploymentConfigTraceability.class, new AnnotationDeploymentConfigTraceability(this, client));
		initializeCapability(capabilities, IDeploymentTraceability.class, new AnnotationDeploymentTraceability(this, client));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ICapability> T getCapability(Class<T> capability) {
		return (T) capabilities.get(capability);
	}
	
	@Override
	public boolean supports(Class<? extends ICapability> capability) {
		return capabilities.containsKey(capability);
	}
	
	
	@Override
	public Map<String, String> getAnnotations() {
		return asMap(ANNOTATIONS);
	}

	@Override
	public String getAnnotation(String key) {
		//TODO make efficient
		Map<String, String> annotations = getAnnotations();
		return annotations.get(key);
	}
	
	@Override
	public boolean isAnnotatedWith(String key) {
		Map<String, String> annotations = getAnnotations();
		return annotations.containsKey(key);
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
		return asString(APIVERSION);
	}
	
	@Override
	public String getCreationTimeStamp(){
		return asString(CREATION_TIMESTAMP);
	}
	@Override
	public String getName(){
		return asString(NAME);
	}
	
	@Override
	public void setName(String name) {
		set(NAME, name);
	}
	
	@Override
	public String getNamespace(){
		ModelNode node = get(NAMESPACE);
		if(node.getType() == ModelType.UNDEFINED){
			return "";
		}
		return node.asString();
	}
	
	@Override
	public void setNamespace(String namespace){
		set(NAMESPACE, namespace);
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
	protected ModelNode get(String key){
		String [] property = propertyKeys.get(key);
		return node.get(property);
	}
	
	protected void set(String key, int value) {
		String [] property = propertyKeys.get(key);
		node.get(property).set(value);
	}
	
	protected void set(String key, String value){
		String [] property = propertyKeys.get(key);
		node.get(property).set(value);
	}
	
	protected Map<String, String> asMap(String property){
		String [] path = propertyKeys.get(property);
		ModelNode node = this.node.get(path);
		HashMap<String, String> map = new HashMap<String, String>();
		if( ModelType.UNDEFINED == node.getType())
			return map;
		for (String key : node.keys()) {
			map.put(key, node.get(key).asString());
		}
		return map;
	}
	
	protected int asInt(String key){
		String [] property = propertyKeys.get(key);
		return node.get(property).asInt();
	}
	
	protected String asString(String property){
		String [] path = propertyKeys.get(property);
		return node.get(path).asString();
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


