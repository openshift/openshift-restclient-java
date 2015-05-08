/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient.model;

/**
 * @author Jeff Cantrill
 */
public interface IBuild extends IResource {

	/**
	 * Returns the status of the buld
	 * @return
	 */
	String getStatus();
	
	/**
	 * Returns the details about the status of this build
	 * @return
	 */
	String getMessage();
	
	/**
	 * Returns the name of the pod running the build
	 * @return
	 */
	@Deprecated
	String getPodName();
}
