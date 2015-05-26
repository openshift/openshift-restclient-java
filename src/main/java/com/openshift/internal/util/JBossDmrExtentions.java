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

	public static Map<String, String> asMap(ModelNode root, Map<String, String []> propertyKeys, String property){
		HashMap<String, String> map = new HashMap<String, String>();
		if(propertyKeys != null){
			String [] path = propertyKeys.get(property);
			ModelNode node = root.get(path);
			if( !node.isDefined())
				return map;
			for (String key : node.keys()) {
				map.put(key, node.get(key).asString());
			}
		}
		return map;
	}
	
	public static int asInt(ModelNode node, Map<String, String []> propertyKeys, String key){
		String [] property = propertyKeys.get(key);
		ModelNode modelNode = node.get(property);
		if(modelNode.getType() == ModelType.UNDEFINED){
			return 0;
		}
		return modelNode.asInt();
	}
	
	public static String asString(ModelNode node, Map<String, String []> propertyKeys, String key){
		String [] path = propertyKeys.get(key);
		ModelNode modelNode = node.get(path);
		if(modelNode.getType() == ModelType.UNDEFINED){
			return "";
		}
		return modelNode.asString();
	}
	
	public static boolean asBoolean(ModelNode node, Map<String, String []> propertyKeys, String key) {
		String [] path = propertyKeys.get(key);
		ModelNode modelNode = node.get(path);
		if(modelNode.getType() == ModelType.UNDEFINED){
			return false;
		}
		return modelNode.asBoolean();
	}

}
