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

import java.net.URL;
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
	private String displayName;
	private String description;
	private final CartridgeType type;
	private URL url;
	private boolean obsolete;
	private CartridgeResourceProperties properties;

	protected CartridgeResourceDTO(final String name, final CartridgeType type, boolean obsolete, final CartridgeResourceProperties properties) {
		this(name, null, null, type, null, obsolete, properties, null, null);
	}

	protected CartridgeResourceDTO(final String name, final String displayName, final String description,
			final String type, URL url, boolean obsolete, CartridgeResourceProperties properties, final Map<String, Link> links, final Messages messages) {
		this(name, displayName, description, CartridgeType.safeValueOf(type), url, obsolete, properties, links, messages);
	}

	CartridgeResourceDTO(final String name, final String displayName, final String description,
			final CartridgeType type, URL url, boolean obsolete, CartridgeResourceProperties properties, final Map<String, Link> links,
			final Messages messages) {
		super(links, messages);
		this.name = name;
		this.displayName = displayName;
		this.description = description;
		this.type = type;
		this.url = url;
		this.obsolete = obsolete;
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

	public URL getUrl() {
		return url;
	}

	public boolean getObsolete() {
		return obsolete;
	}
	
	public CartridgeResourceProperties getProperties() {
		return properties;
	}

	@Override
	public String toString() {
		return "CartridgeResourceDTO ["
				+ " name=" + name
				+ ", description=" + description
				+ ", displayName=" + displayName
				+ ", type=" + type
				+ ", url=" + url
				+ ", obsolete=" + obsolete
				+ "]";
	}

}
