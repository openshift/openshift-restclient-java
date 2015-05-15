/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.authorization;

import com.openshift.internal.restclient.authorization.AuthorizationClient;
import com.openshift.restclient.IClient;

/**
 * @author Jeff Cantrill
 */
public class AuthorizationClientFactory {
	
	/**
	 * Create an authorization client
	 * @param client the OpenShift client to use when retrieving a user
	 * @return
	 */
	public IAuthorizationClient create(IClient client){
		return new AuthorizationClient(client);
	}
}
