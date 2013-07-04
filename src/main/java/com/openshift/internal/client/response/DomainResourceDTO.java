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
 * @author Xavier Coulon
 */
public class DomainResourceDTO extends BaseResourceDTO {

	/** the domain's configured id. */
	private final String id;
	/** the domain's configured suffix (cannot be configured by user, returned by platform). */
	private String suffix;
	
	DomainResourceDTO(final String id, final String suffix, final Map<String, Link> links, final Messages messages) {
		super(links, messages);
		this.id = id;
		this.suffix = suffix;
	}

	/**
	 * @return the id
	 */
	public final String getId() {
		return id;
	}

	public String getSuffix() {
		return suffix;
	}

}
