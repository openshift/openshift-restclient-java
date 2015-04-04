/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.authorization;

/**
 * A type to decouple an IAuthorizationStrategy from
 * the underlying connection stream 
 */
public interface IRequest {

	/**
	 * Add the property with the given name and
	 * value to the underlying request
	 * 
	 * @param name
	 * @param value
	 */
	void setProperty(String name, String value);
}
