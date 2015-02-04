/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.client.model;

import java.util.Map;

/**
 * Kubernetes Service to access a Pod
 */
public interface IService  extends IResource{

	/**
	 * The container port exposed by the image  
	 * @param port
	 */
	void setContainerPort(int port);

	/**
	 * The exposed port that is mapped to a
	 * running image
	 * @param port
	 */
	void setPort(int port);

	/**
	 * The exposed port that is mapped to
	 * a running image
	 * @return
	 */
	int  getPort();

	/**
	 * The container name that the service
	 * routes traffic to. 
	 * @param selector
	 */
	void setSelector(Map<String, String> selector);
	
	/**
	 * Convenience method for setting a selector that has
	 * a singular key/value pair.
	 * @param key
	 * @param value
	 */
	void setSelector(String key, String value);
	
	/**
	 * The selector used to find the Pod
	 * to which this service is routing
	 * @return
	 */
	Map<String, String> getSelector();

	/**
	 * A port where the container is
	 * receiving traffic
	 * @return
	 */
	int getContainerPort();

	/**
	 * The IP of the service.
	 * @return
	 */
	String getPortalIP();


}
