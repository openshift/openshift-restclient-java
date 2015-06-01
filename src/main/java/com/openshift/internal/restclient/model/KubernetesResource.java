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
import com.openshift.restclient.model.IResource;

/**
 * Resource is an abstract representation of a Kubernetes resource
 * 
 * @author Jeff Cantrill
 */
public abstract class KubernetesResource implements IResource, ResourcePropertyKeys {
	
	private ModelNode node;
	private IClient client;
	private Map<Class<? extends ICapability>, ICapability> capabilities = new HashMap<Class<? extends ICapability>, ICapability>();
	private Map<String, String []> propertyKeys;
	
	protected KubernetesResource(ModelNode node, IClient client, Map<String, String []> propertyKeys){
		this.node = node;
		this.client = client;
		this.propertyKeys = propertyKeys;
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
	
	@Override
	public ResourceKind getKind(){
		ModelNode kindNode = get(ResourcePropertyKeys.KIND);
		if(kindNode.isDefined()){
			return ResourceKind.valueOf(kindNode.asString());
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
	
	public void setNamespace(String namespace){
		set(NAMESPACE, namespace);
	}

	@Override
	public void addLabel(String key, String value) {
		ModelNode labels = node.get(ResourcePropertyKeys.LABELS);
		labels.get(key).set(value);
	}
	
	
	@Override
	public Map<String, String> getLabels() {
		return asMap(LABELS);
	}
	
	/*---------- utility methods ------*/
	protected ModelNode get(String key){
		return node.get(getPath(key));
	}

	protected void set(String key, int value) {
		node.get(getPath(key)).set(value);
	}
	
	protected void set(String key, String value){
		node.get(getPath(key)).set(value);
	}

	protected void set(String key, boolean value){
		node.get(getPath(key)).set(value);
	}

	private String[] getPath(String key) {
		String [] property = propertyKeys.get(key);
		if (property == null) {
			throw new IllegalArgumentException(String.format("key %s is not known to the resource %s", key, getName().isEmpty()? getClass().getSimpleName() : getName()));
		}
		return property;
	}

	protected Map<String, String> asMap(String property){
		return JBossDmrExtentions.asMap(this.node, propertyKeys, property);
	}
	
	protected int asInt(String key){
		return JBossDmrExtentions.asInt(node, propertyKeys, key);
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
		ResourceKind kind = getKind();
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
				if (getKind() != other.getKind()) {
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
	
}


