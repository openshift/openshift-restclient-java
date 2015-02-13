/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.client;

public class UnsupportedVersionException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public UnsupportedVersionException(String version){
		super(String.format("OpenShift API version '%s' is not supported by this client", version));
	}
}
