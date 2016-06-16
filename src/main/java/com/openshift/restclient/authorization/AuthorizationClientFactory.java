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
@Deprecated
public class AuthorizationClientFactory {
	
	/**
	 * Create an authorization client
	 * @param client the OpenShift client to use when retrieving a user
	 * @return
	 */
	public IAuthorizationClient create(IClient client){
		return new AuthorizationClient(client);
	}
	
	/**
	 * Leaving here until we find a usecase to make it 'public' outside of
	 * the internal packages.
	 * 
	 * @author jeff.cantrill
	 *
	 */
	public static class AuthorizationClientBuilder{
		
		private int connectTimeoutMillis;
		private IClient client;
		
		public AuthorizationClientBuilder withClient(IClient client) {
			this.client = client;
			return this;
		}
		
		/**
		 * The connect timeout in millis
		 * @param connectTimeoutMillis
		 * @return
		 */
		public AuthorizationClientBuilder withConnectTimeout(int connectTimeoutMillis) {
			this.connectTimeoutMillis = connectTimeoutMillis;
			return this;
		}
		
		public IAuthorizationClient build() {
			return new AuthorizationClient(this.client, connectTimeoutMillis);
		}
	}
}
