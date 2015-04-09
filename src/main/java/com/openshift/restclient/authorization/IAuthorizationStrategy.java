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
 * Authorization interface to provide various authorization mechanisms
 * to an OpenShift server 
 * 
 * @author Jeff Cantrill
 *
 */
public interface IAuthorizationStrategy {

	/**
	 * Authorizes the given request
	 * @param request
	 */
	void authorize(IRequest request);
}
