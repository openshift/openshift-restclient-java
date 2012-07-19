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
package com.openshift.internal.client;

/**
 * A paramater wrapper to pass param key/values to the service when executing a remote operation from a link.
 * 
 * @author Xavier Coulon
 * 
 */
public class ServiceParameter {

	/** the parameter key. */
	private final String key;

	/** The parameter value. */
	private final Object value;

	public ServiceParameter(final String key, final Object value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

}
