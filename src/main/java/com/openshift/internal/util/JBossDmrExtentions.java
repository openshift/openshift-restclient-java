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
		if( !node.isDefined()){
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
		if( !node.isDefined()){
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
		if( !node.isDefined()){
			return false;
		}
		return modelNode.asBoolean();
	}

}
