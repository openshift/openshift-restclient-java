/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.restclient;

/**
 * The exception thrown when the client is trying to submit a request
 * to an unrecognized endpoint.  This usually occurs when a newer client
 * is trying to utilize a feature that is unavailable to an older server
 * 
 * @author jeff.cantrill
 *
 */
public class UnsupportedEndpointException extends OpenShiftException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9004398690965821552L;

	public UnsupportedEndpointException(String message, Object... arguments) {
		super(message, arguments);
	}

}
