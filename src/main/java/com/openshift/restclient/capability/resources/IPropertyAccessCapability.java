/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.restclient.capability.resources;

import java.util.Map;

import com.openshift.restclient.capability.ICapability;

/**
 * A mechanism to access the underlying content of a json
 * structure
 * 
 * Relies on a dot delimited path to indentify the root of the entity
 * being returned (e.g. 'metadata.labels').  This does not provide
 * a mechanism to retrieve properties across an array kind.
 * 
 * @author jeff.cantrill
 *
 */
public interface IPropertyAccessCapability extends ICapability {

	/**
	 * 
	 * @param path
	 * @return
	 * 
	 * @throws @{@link UnresolvablePathException} when the path 
	 * does not resolve to an existing node
	 */
	Map<String, Object> asMap(String path);
	
	/**
	 * Return the string value to the path
	 * @param path
	 * @return
	 * @throws @{@link UnresolvablePathException} when the path 
	 * does not resolve to an existing node
	 */
	String asString(String path);
	
	/**
	 * The exception thrown when a path given to the capability is
	 * unresolvable
	 * 
	 * @author jeff.cantrill
	 *
	 */
	public static class UnresolvablePathException extends RuntimeException{

		/**
		 * 
		 */
		private static final long serialVersionUID = 2422016683166925224L;

		
	}
}
