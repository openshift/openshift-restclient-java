/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client;

/**
 * OpenShift Exception that encapsulates an underlying exception that occurred during an SSH operation.
 * @author Xavier Coulon
 *
 */
public class OpenShiftSSHOperationException extends OpenShiftException {

	private static final long serialVersionUID = -1058021959768485317L;

	public OpenShiftSSHOperationException(Throwable cause, String message, Object... arguments) {
		super(cause, message, arguments);
	}

	public OpenShiftSSHOperationException(String message, Object... arguments) {
		super(message, arguments);
	}
}
