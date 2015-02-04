/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.client;

import com.openshift3.internal.client.model.Status;

public class OpenShiftException extends RuntimeException {

	private static final long serialVersionUID = -7076942050102006278L;
	private Status status;

	public OpenShiftException(String message, Throwable cause, Status status) {
		super(message, cause);
		this.status = status;
	}		
	
	public Status getStatus(){
		return this.status;
	}
}
