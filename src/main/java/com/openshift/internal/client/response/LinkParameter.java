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

import com.openshift.client.OpenShiftRequestException;


/**
 * @author Xavier Coulon
 * @author Andre Dietisheim
 */
public class LinkParameter {

	protected final String name;
	protected final LinkParameterType type;
	protected final String description;
	protected final String defaultValue;
	protected final List<String> validOptions;

	protected LinkParameter(final String name, final String type, final String defaultValue, final String description,
			final List<String> validOptions) throws OpenShiftRequestException {
		this(name, new LinkParameterType(type), defaultValue, description, validOptions);
	}

	protected LinkParameter(final String name, final LinkParameterType type, final String defaultValue, final String description,
			final List<String> validOptions) throws OpenShiftRequestException {
		this.name = name;
		this.type = type;
		this.description = description;
		this.defaultValue = defaultValue;
		this.validOptions = validOptions;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @return the type
	 */
	public final LinkParameterType getType() {
		return type;
	}

	/**
	 * @return the description
	 */
	public final String getDescription() {
		return description;
	}

	/**
	 * @return the defaultValue, or null. Only applicable to optional parameters.
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	public List<String> getValidOptions() {
		return validOptions;
	}
}