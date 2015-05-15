/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.authorization;

import com.openshift.restclient.model.user.IUser;

/**
 * @author Jeff Cantrill
 */
public interface IAuthorizationContext {
	
	public static final String AUTHSCHEME_BASIC = "Basic";
	public static final String AUTHSCHEME_OAUTH = "OAuth";
	
	/**
	 * The authorized user if it can be found for this token
	 * @return return the user for the token or null if not authorized
	 */
	IUser getUser();
	
	/**
	 * 
	 * @return true if the token is non-null; false otherwise
	 */
	boolean isAuthorized();
	
	/**
	 * The authorization scope if it can be determined.
	 * @return the scope or null if unknown
	 */
	String getAuthScheme();
	
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

}
