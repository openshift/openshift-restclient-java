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
 * @author Andre Dietisheim
 */
public class OpenShiftRequestException extends OpenShiftException {

	private static final long serialVersionUID = 1L;

	public OpenShiftRequestException(String message, Object... arguments) {
		super(message, arguments);
	}

}
