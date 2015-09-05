/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model;

import static com.openshift.internal.restclient.capability.CapabilityInitializer.initializeCapabilities;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

import com.openshift.internal.restclient.model.properties.ResourcePropertyKeys;
import com.openshift.internal.util.JBossDmrExtentions;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.CapabilityVisitor;
import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.IResource;

/**
 * Resource is an abstract representation of a Kubernetes resource
 * 
 * @author Jeff Cantrill
 */
public class KubernetesResource implements IResource, ResourcePropertyKeys {
	
	private ModelNode node;
	private IClient client;
	private Map<Class<? extends ICapability>, ICapability> capabilities = new HashMap<Class<? extends ICapability>, ICapability>();
	private Map<String, String []> propertyKeys;
	private IProject project;
	
	/**
	 * 
	 * @param node
	 * @param client
	 * @param overrideProperties  the map of properties that override the defaults
	 */
	public KubernetesResource(ModelNode node, IClient client, Map<String, String []> overrideProperties){
		if(overrideProperties == null) overrideProperties = new HashMap<String, String []>();
		this.node = node;
		this.client = client;
		this.propertyKeys = overrideProperties;
		initializeCapabilities(capabilities, this, client);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ICapability> T getCapability(Class<T> capability) {
		return (T) capabilities.get(capability);
	}
	
	public Set<Class<? extends ICapability>> getCapabilities(){
		return Collections.unmodifiableSet(capabilities.keySet());
	}
	
	protected Map<Class<? extends ICapability>, ICapability> getModifiableCapabilities(){
		return capabilities;
	}
	
	@Override
	public boolean supports(Class<? extends ICapability> capability) {
		return capabilities.containsKey(capability);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ICapability, R> R accept(CapabilityVisitor<T, R> visitor, R unsupportedValue){
		if(capabilities.containsKey(visitor.getCapabilityType())){
			T capability = (T) capabilities.get(visitor.getCapabilityType());
			return (R) visitor.visit(capability);
		}
		return unsupportedValue;
	}
	
	@Override
	public IProject getProject() {
		if(this.project == null) {
			this.project = client.get(ResourceKind.PROJECT, getNamespace(), ""); 
		}
		return this.project;
	}

	@Override
	public Map<String, String> getAnnotations() {
		return asMap(ANNOTATIONS);
	}

	@Override
	public String getAnnotation(String key) {
		return getAnnotations().get(key);
	}
	
	
	@Override
	public void setAnnotation(String name, String value) {
		if(value == null) return;
		ModelNode annotations = get(ANNOTATIONS);
		annotations.get(name).set(value);
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
	
	@Override
	public String getKind(){
		ModelNode kindNode = get(ResourcePropertyKeys.KIND);
		if(kindNode.isDefined()){
			return kindNode.asString();
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
		return asString(METADATA_NAME);
	}
	
	public void setName(String name) {
		set(METADATA_NAME, name);
	}
	
	@Override
	public String getNamespace(){
		ModelNode node = get(NAMESPACE);
		if(node.getType() == ModelType.UNDEFINED){
			return "";
		}
		return node.asString();
	}
	
	public void setNamespace(String namespace){
		set(NAMESPACE, namespace);
	}

	@Override
	public void addLabel(String key, String value) {
		ModelNode labels = node.get(getPath(LABELS));
		labels.get(key).set(value);
	}
	
	
	@Override
	public Map<String, String> getLabels() {
		return asMap(LABELS);
	}
	
	/*---------- utility methods ------*/
	protected ModelNode get(String key){
		return get(node, key);
	}
	protected ModelNode get(ModelNode node, String key){
		return node.get(getPath(key));
	}

	protected Map<String, String> getEnvMap(String key) {
		Map<String, String> values = new HashMap<String, String>();
		ModelNode source = node.get(getPath(key));
		if(source.getType() == ModelType.LIST){
			for (ModelNode value : source.asList()) {
				values.put(value.get("name").asString(), value.get("value").asString());
			}
		}
		return values;
	}

	protected void set(String key, Map<String, String> values) {
		JBossDmrExtentions.set(node, propertyKeys, key, values);
	}
	
	protected void set(String key, int value) {
		JBossDmrExtentions.set(node, propertyKeys, key, value);
	}
	
	protected void set(ModelNode node, String key, int value) {
		JBossDmrExtentions.set(node, propertyKeys, key, value);
	}
	
	protected void set(String key, String value){
		JBossDmrExtentions.set(node, propertyKeys, key, value);
	}

	protected void set(ModelNode node, String key, String value){
		JBossDmrExtentions.set(node, propertyKeys, key, value);
	}

	protected void set(String key, boolean value){
		JBossDmrExtentions.set(node, propertyKeys, key, value);
	}
	
	protected void set(ModelNode node, String key, boolean value){
		JBossDmrExtentions.set(node, propertyKeys, key, value);
	}

	protected void setEnvMap(String key, Map<String, String> values) {
		ModelNode mapNodeParent = node.get(getPath(key));
		for(Map.Entry<String, String> value: values.entrySet()) {
			ModelNode mapNode = mapNodeParent.add();
			mapNode.get("name").set(value.getKey());
			mapNode.get("value").set(value.getValue());
		}
	}

	protected String[] getPath(String key) {
		return JBossDmrExtentions.getPath(propertyKeys, key);
	}
	
	protected String asString(ModelNode node, String subKey) {
		return JBossDmrExtentions.asString(node, propertyKeys, subKey);
	}
	
	protected int asInt(String key){
		return JBossDmrExtentions.asInt(node, propertyKeys, key);
	}
	
	protected int asInt(ModelNode node, String key){
		return JBossDmrExtentions.asInt(node, propertyKeys, key);
	}
	
	protected Map<String, String> asMap(String property){
		return JBossDmrExtentions.asMap(this.node, propertyKeys, property);
	}
	
	protected String asString(String property){
		return JBossDmrExtentions.asString(node, propertyKeys, property);
	}

	protected boolean asBoolean(String property) {
		return JBossDmrExtentions.asBoolean(node, propertyKeys, property);
	}

	@Override
	public String toString() {
		return node.toJSONString(true);
	}
	
	public String toPrettyString(){
		return node.toJSONString(false);
	}
	
	@Override
	public int hashCode() {
		String namespace = getNamespace();
		String name = getName();
		String kind = getKind();
		final int prime = 31;
		return prime * (namespace.hashCode() + name.hashCode() + kind.hashCode()); 
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj == null)
			return false;
		else if (getClass() != obj.getClass())
			return false;
		else {
			KubernetesResource other = (KubernetesResource) obj; 
			if (getKind() != null){
				if (!getKind().equals(other.getKind())) {
					return false;
				}
			} else {
				if (other.getKind() != null) {
					return false;
				}
			}
			if (getNamespace() != null) {
				if(!getNamespace().equals(other.getNamespace())) {
					return false;
				}
			} else {
				if (other.getNamespace() != null) {
					return false;
				}
			}
			if (getName() != null) {
				if(!getName().equals(other.getName())) {
					return false;
				}
			} else {
				if (other.getName() != null) {
					return false;
				}
			}
			
		}
		return true;
	}

	@Override
	public String toJson() {
		return toJson(false);
	}

	@Override
	public String toJson(boolean compact) {
		return node.toJSONString(compact);
	}

	
}


