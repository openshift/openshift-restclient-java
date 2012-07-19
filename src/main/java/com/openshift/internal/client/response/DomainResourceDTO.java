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

/**
 * @author Xavier Coulon
 */
public class DomainResourceDTO extends BaseResourceDTO {

	/** the domain's configured namespace. */
	private final String namespace;
	/** the domain's configured suffix (cannot be configured by user, returned by platform). */
	private String suffix;
	
	protected DomainResourceDTO(final String namespace, final String suffix, final Map<String, Link> links, final List<Message> creationLog) {
		super(links, creationLog);
		this.namespace = namespace;
		this.suffix = suffix;
	}

	/**
	 * @return the namespace
	 */
	public final String getNamespace() {
		return namespace;
	}

	public String getSuffix() {
		return suffix;
	}

}
