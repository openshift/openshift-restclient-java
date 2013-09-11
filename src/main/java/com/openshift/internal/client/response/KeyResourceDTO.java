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

/**
 * The Class KeyResourceDTO.
 */
public class KeyResourceDTO extends BaseResourceDTO {

	/** The name. */
	private final String name;

	/** The type. */
	private final String type;

	/** The public key content. */
	private final String content;

	/**
	 * Instantiates a new key resource dto.
	 * 
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param content
	 *            the content
	 * @param links
	 *            the links
	 */
	KeyResourceDTO(final String name, final String type, final String content, final Map<String, Link> links, final Messages messages) {
		super(links, messages);
		this.name = name;
		this.type = type;
		this.content = content;
	}

	/**
	 * Returns the name.
	 * 
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Returns the type.
	 * 
	 * @return the type
	 */
	public final String getType() {
		return type;
	}

	/**
	 * Returns the public key content.
	 * 
	 * @return the public key content
	 */
	public final String getContent() {
		return content;
	}

	@Override
	public String toString() {
		return "KeyResourceDTO ["
				+ "name=" + name
				+ ", type=" + type
				+ ", content=" + content
				+ "]";
	}

}
