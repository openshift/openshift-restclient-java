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
 * The DTO for an environment variable
 * 
 * @author Syed Iqbal
 * 
 */
public class EnvironmentVariableResourceDTO extends BaseResourceDTO{
	/** The environment variable's name. */
	private final String name;
 
	/** the environment variable's value */
	private final String value;
 
	/**
	 * Instantiates a new environment variable resource dto.
	 *
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 */
	public EnvironmentVariableResourceDTO(final String name, final String value, final Map<String, Link> links,final Messages messages) {
		super(links, messages);
		this.name = name;
		this.value = value;
	}
 
	/**
	 * Returns the name of this environment variable
	 *
	 * @return the name
	 */
	public final String getName() {
		return name;
	}
 
	/**
	 * Returns the value of this gear group.
	 *
	 * @return the value
	 */
	public final String getValue() {
		return value;
	}

}
