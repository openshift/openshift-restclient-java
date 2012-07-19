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

import java.util.List;
import java.util.Map;

import com.openshift.internal.client.CartridgeType;

/**
 * @author Xavier Coulon
 * 
 */
public class CartridgeResourceDTO extends BaseResourceDTO {

	private final String name;
	private final CartridgeType type;

	public CartridgeResourceDTO(final String name, final String type, final Map<String, Link> links,
			final List<Message> creationLog) {
		super(links, creationLog);
		this.name = name;
		this.type = CartridgeType.safeValueOf(type);
	}

	public String getName() {
		return name;
	}

	public CartridgeType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "CartridgeResourceDTO ["
				+ "name=" + name
				+ ", type=" + type
				+ "]";
	}

}
