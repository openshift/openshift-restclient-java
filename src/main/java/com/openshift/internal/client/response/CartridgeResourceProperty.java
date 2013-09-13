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


/**
 * @author Andre Dietisheim
 */
public class CartridgeResourceProperty {

	private String name;
	private String type;
	private String description;
	private String value;

	/**
	 * Constructor called when embedded cartridges are listed within an
	 * application. 
	 * <p>
	 * ex.
	 * <pre>
	 * "metrics-0.1":{
	 *          "connection_url":"https://eap6-foobarz.rhcloud.com/metrics/",
	 *          "info":"Connection URL: https://eap6-foobarz.rhcloud.com/metrics/"
	 * }
	 * 
	 * <pre>
	 */
	protected CartridgeResourceProperty(String name, String value) {
		this(name, null, null, value);
	}
	
	/**
	 * Constructor called when embedded cartridges are listed on their own (when
	 * /cartridges resource is queried.
	 * <p>
	 * ex.
	 * 
	 * <pre>
	 * "properties":[
	 *  {
	 *          "name":"connection_url",
	 *          "type":"cart_data",
	 *          "description":"Application metrics URL",
	 *          "value":"https://eap6-foobarz.rhcloud.com/metrics/"
	 * }
	 * <pre>
	 */
	protected CartridgeResourceProperty(String name, String type, String description, String value) {
		this.name = name;
		this.type = type;
		this.description = description;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "ResourceProperty ["
				+ " name=" + name 
				+ ", type=" + type 
				+ ", description=" + description 
				+ ", value=" + value 
				+ " ]";
	}

}
