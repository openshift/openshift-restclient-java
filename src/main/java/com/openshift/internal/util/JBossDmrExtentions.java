/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

/**
 * Helper extensions to those provided
 * by JBoss DMR library
 * 
 * @author Jeff Cantrill
 */
public class JBossDmrExtentions {
	
	private JBossDmrExtentions (){
	}
	
	
	public static void set(ModelNode node, Map<String, String []> propertyKeys, String key, boolean value){
		if(propertyKeys == null) return;
		ModelNode modelNode = node.get(getPath(propertyKeys, key));
		modelNode.set(value);
	}

	public static void set(ModelNode node, Map<String, String []> propertyKeys, String key, String value){
		if(propertyKeys == null) return;
		set(node, getPath(propertyKeys, key), value);
	}

	public static void set(ModelNode node, String [] path, String value){
		if(value == null) return;
		ModelNode modelNode = node.get(path);
		modelNode.set(value);
	}
	
	public static void set(ModelNode node, Map<String, String []> propertyKeys, String key, int value) {
		if(propertyKeys == null) return;
		ModelNode modelNode = node.get(getPath(propertyKeys, key));
		modelNode.set(value);
	}

	public static void set(ModelNode node, Map<String, String []> propertyKeys, String key, Map<String, String> values) {
		if(propertyKeys == null) return;
		ModelNode modelNode = node.get(getPath(propertyKeys, key));
		for (Entry<String, String> entry : values.entrySet()) {
			modelNode.get(entry.getKey()).set(entry.getValue());
		}
	}

	/**
	 * 
	 * @param root
	 * @param propertyKeys
	 * @param key
	 * @return
	 * @throws UnregisteredPropertyException   if the property is not found in the property map
	 */
	public static Map<String, String> asMap(ModelNode root, Map<String, String []> propertyKeys, String key){
		HashMap<String, String> map = new HashMap<String, String>();
		if(propertyKeys != null){
			String [] path = getPath(propertyKeys, key);
			ModelNode node = root.get(path);
			if( !node.isDefined())
				return map;
			for (String k : node.keys()) {
				map.put(k, node.get(k).asString());
			}
		}
		return map;
	}

	/**
	 * <T>  the type to return which are valid DMR types (e.g. asString()).
	 * String is currently only supported.  Add more as needed
	 * @param root
	 * @param propertyKeys
	 * @param key
	 * @param type
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Set asSet(ModelNode root, Map<String, String []> propertyKeys, String key, ModelType type){
		Set set = new HashSet();
		String [] path = getPath(propertyKeys, key);
		ModelNode node = root.get(path);
		if( !node.isDefined())
			return set;
		for (ModelNode entry : node.asList()) {
			Object instance = null;
			switch(type) {
			case STRING:
				instance = entry.asString();
				break;
			case BOOLEAN:
				instance = entry.asBoolean();
				break;
			case INT:
				instance = entry.asInt();
			default:
			}
			set.add(instance);
		}
		return set;
	}
	
	 public static void set(ModelNode root, Map<String, String []> propertyKeys, String key, Set<String> values) {
		String [] path = getPath(propertyKeys, key);
		ModelNode node = root.get(path);
		for (String entry : values) {
			node.add(entry);
		}
	}

	public static void set(ModelNode root, Map<String, String []> propertyKeys, String key, String... values) {
		String [] path = getPath(propertyKeys, key);
		ModelNode node = root.get(path);
		for (String value : values) {
			node.add(value);
		}
	}
	
	/**
	 * 
	 * @param node
	 * @param propertyKeys
	 * @param key
	 * @return
	 * @throws UnregisteredPropertyException   if the property is not found in the property map
	 */
	public static int asInt(ModelNode node, Map<String, String []> propertyKeys, String key){
		String [] path = getPath(propertyKeys, key);
		ModelNode modelNode = node.get(path);
		if( !modelNode.isDefined()){
			return 0;
		}
		return modelNode.asInt();
	}
	
	/**
	 * 
	 * @param node
	 * @param propertyKeys
	 * @param key
	 * @return
	 * @throws UnregisteredPropertyException   if the property is not found in the property map
	 */
	public static String asString(ModelNode node, Map<String, String []> propertyKeys, String key){
		ModelNode modelNode = node.get(getPath(propertyKeys, key));
		if( !modelNode.isDefined()){
			return "";
		}
		return modelNode.asString();
	}
	
	/**
	 * 
	 * @param node
	 * @param propertyKeys
	 * @param key
	 * @return
	 * @throws UnregisteredPropertyException   if the property is not found in the property map
	 */
	public static boolean asBoolean(ModelNode node, Map<String, String []> propertyKeys, String key) {
		String [] path = getPath(propertyKeys, key);
		ModelNode modelNode = node.get(path);
		if( !modelNode.isDefined()){
			return false;
		}
		return modelNode.asBoolean();
	}

	public static ModelNode get(ModelNode node, Map<String, String []> propertyKeys, String key){
		return node.get(getPath(propertyKeys,key));
	}
	
	public static String[] getPath(Map<String, String []> propertyKeys, String key) {
		if(propertyKeys != null && propertyKeys.containsKey(key)) {
			return propertyKeys.get(key); //allow override
		}
		return key.split("\\.");
	}
	
	@SuppressWarnings("unchecked")
	public static String[] getPath(String key) {
		return getPath(Collections.EMPTY_MAP, key);
	}
}
