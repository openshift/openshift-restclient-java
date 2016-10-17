/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.model;

import java.util.List;
import java.util.Map;

/**
 * Kubernetes Service to access a Pod
 * 
 * @author Jeff Cantrill
 */
public interface IService extends IResource{

	/**
	 * Sets the container port exposed by the image  
	 * @param port
	 */
	void setTargetPort(int port);
	
	/**
	 * Sets the exposed port that is mapped to a
	 * running image
	 * @param port
	 */
	void setPort(int port);

	/**
	 * Returns the first exposed port that is mapped to
	 * a running image
	 * @return
	 */
	int getPort();
	
	IServicePort addPort(int port, int targetPort);
	
	IServicePort addPort(int port, int targetPort, String name);
	
	/**
	 * Sets the container name that the service
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
	 * Returns the selector used to find the Pod
	 * to which this service is routing
	 * @return
	 */
	Map<String, String> getSelector();

	/**
	 * The port this service targets on the
	 * pod
	 * @return
	 */
	String getTargetPort();
	
	/**
	 * Returns the IP of the service.
	 * @return
	 */
	String getPortalIP();
	
	/**
	 * Retrieves the pods for this service
	 * @return
	 */
	List<IPod> getPods();
	
	/**
	 * Get the collection of ports for the service
	 * @return
	 */
	List<IServicePort> getPorts();
	
	/**
	 * Set the collection of ports for the service 
	 */
	void setPorts(List<IServicePort> ports);

	/**
	 * Returns the type of the service.
	 * @return
	 */
	String getType();

	/**
	 * Sets the type of the service.
	 */
	void setType(String type);
}
