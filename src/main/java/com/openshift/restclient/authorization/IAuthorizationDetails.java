/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.restclient.authorization;

/**
 * The details about how to manually obtain
 * a request token
 *  
 * @author jeff.cantrill
 */
public interface IAuthorizationDetails {
	
	/**
	 * The message returned from the server
	 * for being unauthorized if provided
	 * @return the message or empty string
	 */
	String getMessage();
	
	/**
	 * The link to visit to request a valid
	 * token that can be used to access
	 * the server if provided
	 * @return the link or null if not provided
	 */
	String getRequestTokenLink();
	
	/**
	 * The authentication scheme
	 * @return
	 */
	String getScheme();
	
}
