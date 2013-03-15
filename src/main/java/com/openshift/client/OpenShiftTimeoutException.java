/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
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
 * @author Andre Dietisheim
 */
public class OpenShiftTimeoutException  extends OpenShiftEndpointException {

	private static final long serialVersionUID = 1L;

	public OpenShiftTimeoutException(String url, Throwable e, String message, Object... arguments) {
		super(url, e, null, message, arguments);
	}

}
