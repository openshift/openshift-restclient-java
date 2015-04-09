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
 * @author Jeff Cantrill
 */
public interface IAuthorizationContext {

	AuthorizationType getType();
	
	boolean isAuthorized();
	
	/**
	 * Token to use for authentication.  Will return non-null
	 * value if authorized
	 * @return
	 */
	String getToken();
	
	/**
	 * Time in ?? when the token expires. Will return
	 * non-null value if authorized
	 * @return
	 */
	String getExpiresIn();
	
	static enum AuthorizationType {
		Basic,
		Kerberos
	}
	
}
