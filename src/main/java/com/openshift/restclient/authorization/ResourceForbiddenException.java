/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.restclient.authorization;

import com.openshift.restclient.OpenShiftException;

/**
 * @author jeff.cantrill
 */
public class ResourceForbiddenException extends OpenShiftException {

	private static final long serialVersionUID = 6998191096256199081L;

	public ResourceForbiddenException(String message, Throwable e) {
		super(message, e);
	}
	

}
