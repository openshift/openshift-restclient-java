/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.dmr.ModelNode;

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
		String [] path = propertyKeys.get(key);
		if(path == null) throw new UnregisteredPropertyException(key);
		ModelNode modelNode = node.get(path);
		modelNode.set(value);
	}

	public static void set(ModelNode node, Map<String, String []> propertyKeys, String key, String value){
		if(propertyKeys == null) return;
		String [] path = propertyKeys.get(key);
		if(path == null) throw new UnregisteredPropertyException(key);
		set(node, path, value);
	}

	public static void set(ModelNode node, String [] path, String value){
		if(value == null) return;
		ModelNode modelNode = node.get(path);
		modelNode.set(value);
	}
	
	public static void set(ModelNode node, Map<String, String []> propertyKeys, String key, int value) {
		if(propertyKeys == null) return;
		String [] path = propertyKeys.get(key);
		if(path == null) throw new UnregisteredPropertyException(key);
		ModelNode modelNode = node.get(path);
		modelNode.set(value);
	}

	public static void set(ModelNode node, Map<String, String []> propertyKeys, String key, Map<String, String> values) {
		if(propertyKeys == null) return;
		String [] path = propertyKeys.get(key);
		if(path == null) throw new UnregisteredPropertyException(key);
		ModelNode modelNode = node.get(path);
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
			String [] path = propertyKeys.get(key);
			if(path == null) throw new UnregisteredPropertyException(key);
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
	 * 
	 * @param node
	 * @param propertyKeys
	 * @param key
	 * @return
	 * @throws UnregisteredPropertyException   if the property is not found in the property map
	 */
	public static int asInt(ModelNode node, Map<String, String []> propertyKeys, String key){
		String [] path = propertyKeys.get(key);
		if(path == null) throw new UnregisteredPropertyException(key);
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
		String [] path = propertyKeys.get(key);
		if(path == null) throw new UnregisteredPropertyException(key);
		ModelNode modelNode = node.get(path);
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
		String [] path = propertyKeys.get(key);
		if(path == null) throw new UnregisteredPropertyException(key);
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
		String [] property = propertyKeys.get(key);
		if (property == null) {
			throw new UnregisteredPropertyException(key);
		}
		return property;
	}
}
