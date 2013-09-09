/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client.response;

import java.util.Map;

import com.openshift.client.Messages;
import com.openshift.internal.client.CartridgeType;

/**
 * @author Xavier Coulon
 * @author Andre Dietisheim
 * 
 */
public class CartridgeResourceDTO extends BaseResourceDTO {

	private final String name;
	private final CartridgeType type;
	private String displayName;
	private String description;
	private ResourceProperties properties;

	/**
	 * Constructor used when a cartridge is constructed form the embedded
	 * property within the application.
	 * <p>
	 * ex.
	 * 
	 * <pre>
	 * "embedded":{
	 *            "switchyard-0":{
	 * 
	 *           },
	 * </pre>
	 */
	protected CartridgeResourceDTO(final String name, final CartridgeType type, final ResourceProperties properties) {
		this(name, null, null, type, properties, null, null);
	}

	/**
	 * Constructor used when a cartridge is constructed from the cartridges
	 * ("<application>/cartridges") resource.
	 */
	protected CartridgeResourceDTO(final String name, final String displayName, final String description,
			final String type, ResourceProperties properties, final Map<String, Link> links, final Messages messages) {
		this(name, displayName, description, CartridgeType.safeValueOf(type), properties, links, messages);
	}

	CartridgeResourceDTO(final String name, final String displayName, final String description,
			final CartridgeType type, ResourceProperties properties, final Map<String, Link> links,
			final Messages messages) {
		super(links, messages);
		this.name = name;
		this.displayName = displayName;
		this.description = description;
		this.type = type;
		this.properties = properties;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDescription() {
		return description;
	}

	public CartridgeType getType() {
		return type;
	}

	public ResourceProperties getProperties() {
		return properties;
	}

	@Override
	public String toString() {
		return "CartridgeResourceDTO ["
				+ " name=" + name
				+ ", description=" + description
				+ ", displayName=" + displayName
				+ ", type=" + type
				+ "]";
	}

}
