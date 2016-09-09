/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.model.route;

/**
 * Target port for routes. If both attributes are present, then name is preferred.
 * 
 * @author Jeff Maury
 */
public interface ITargetPort {
	
	/**
	 * Returns the target port name.
	 * 
	 * @return target port name.
	 */
	String getTargetPortName();

	/**
	 * Sets the target port name.
	 * 
	 * @param portName target port name
	 */
	void setTargetPortName(String portName);

	/**
     * Returns the target port value.
     * 
     * @return target port value.
     */
    Integer getTargetPort();

    /**
     * Sets the target port value.
     * 
     * @param portName target port value
     */
    void setTargetPort(Integer port);
}
