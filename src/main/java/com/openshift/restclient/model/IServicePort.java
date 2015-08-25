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
package com.openshift.restclient.model;

public interface IServicePort {

	/**
	 * The name of the port
	 * 
	 * @return the name or null if undefined.
	 */
	String getName();
	void setName(String name);
	
	/**
	 * Port exposed by the service
	 * @return
	 */
	int getPort();
	void setPort(int port);
	
	/**
	 * The target port on the pod it services
	 * @return
	 */
	int getTargetPort();
	void setTargetPort(int port);
	
	/**
	 * IP protocol (TCP, UDP)
	 * @return
	 */
	String getProtocol();
	void setProtocol(String proto);
}
