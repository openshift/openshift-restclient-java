/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client.response;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.openshift.internal.client.utils.CollectionUtils;

/**
 * Properties that hold informations available in cartridges
 * 
 * @author Andre Dietisheim
 */
public class CartridgeResourceProperties {

	public static final String CONNECTION_URL = "connection_url";
	public static final String JOB_URL = "job_url";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String DATABASE_NAME = "database_name";

	private Map<String, CartridgeResourceProperty> properties = new LinkedHashMap<String, CartridgeResourceProperty>();

	public String getPropertyValue(String name) {
		CartridgeResourceProperty property = properties.get(name);
		if (property == null) {
			return null;
		}
		return property.getValue();
	}

	public void add(String name, CartridgeResourceProperty property) {
		properties.put(name, property);
	}

	public CartridgeResourceProperty getProperty(String name) {
		return properties.get(name);
	}

	public List<CartridgeResourceProperty> getAll() {
		return CollectionUtils.toUnmodifiableCopy(properties.values());
	}

	public int size() {
		return properties.size();
	}

	@Override
	public String toString() {
		return "ResourceProperties [ "
				+ "properties=" + properties +
				" ]";
	}

}
