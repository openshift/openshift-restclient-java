/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.model.secret;

import java.io.InputStream;

import com.openshift.restclient.model.IResource;

/**
 * Kubernetes Secret object to inject/mount sensitive data into containers 
 * 
 * @author Jiri Pechanec
 */
public interface ISecret extends IResource {

	/**
	 * Sets the container port exposed by the image  
	 * @param key - the name under which the data are mount in container
	 * @param data
	 */
	void addData(String key, InputStream data);

	/**
	 * Sets the container port exposed by the image  
	 * @param key - the name under which the data are mount in container
	 * @param data
	 */
	void addData(String key, byte[] data);
	
	/**
	 * Get the data stored under the key
	 * @param key
	 * @return byte representation of the stored data
	 */
	byte[] getData(String key);
	
	/**
	 * Sets the type of Secrete - default Opaque
	 * @param type
	 */
	void setType(String type);
	
	/**
	 * Returns the type of Secrete - default Opaque
	 * return
	 */
	String getType();
}
