/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.client.model.route;

import com.openshift3.client.model.IResource;

/**
 * OpenShift route to Service
 */
public interface IRoute extends IResource {

	/**
	 * Retrieves the externally available hostname that can be used to access
	 * service.
	 * 
	 * @return Route hostname.
	 */
	String getHost();

	/**
	 * Sets the externally available hostname that can be used to access
	 * service.
	 * 
	 * @param host
	 *            hostname to use
	 */
	void setHost(String host);

	/**
	 * Retrieves the path that the router watches for, to route traffic for to
	 * the service.
	 * 
	 * @return Route path.
	 */
	String getPath();

	/**
	 * Sets the path that the router watches for, to route traffic for to the
	 * service.
	 * 
	 * @param path
	 *            route path
	 */
	void setPath(String path);

	/**
	 * Retrieves the name of the service this route leads to.
	 * 
	 * @return Name of the service for this route.
	 */
	String getServiceName();

	/**
	 * Sets the name of the service this route should lead to.
	 * 
	 * @param serviceName
	 *            Name of the service this route should lead to.
	 */
	void setServiceName(String serviceName);

	/**
	 * Retrieves the TLS configuration of this route.
	 * 
	 * @return TLS configuration.
	 */
	ITLSConfig getTLSConfig();
}
