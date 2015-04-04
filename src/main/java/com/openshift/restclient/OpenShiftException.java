/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.restclient;

import com.openshift.restclient.model.IStatus;

public class OpenShiftException extends RuntimeException {

	private static final long serialVersionUID = -7076942050102006278L;
	private IStatus status;

	public OpenShiftException(String message, Throwable cause, IStatus status) {
		super(message, cause);
		this.status = status;
	}		
	
	public IStatus getStatus(){
		return this.status;
	}
}
