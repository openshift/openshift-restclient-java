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

import com.openshift.restclient.model.IStatus;

/**
 * The exception thrown when the client is trying to submit a request
 * to with parameters that are not accepted by the server
 * 
 * @author jeff.cantrill
 *
 */
public class BadRequestException extends OpenShiftException {


	/**
	 * 
	 */
	private static final long serialVersionUID = -333562634088784896L;

	public BadRequestException(Throwable e, IStatus status, String endpoint) {
		super(e, status, "%s", endpoint);
	}

}
