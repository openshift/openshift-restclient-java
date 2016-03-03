/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.restclient.authorization;

import org.apache.commons.lang.StringUtils;

import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.model.IStatus;

/**
 * @author Jeff Cantrill
 */
public class UnauthorizedException extends OpenShiftException {

	private static final long serialVersionUID = -3999801367045252906L;
	private static final String MSG_BASE = "Unauthorized to access resource.";
	private IAuthorizationDetails details;
	private IStatus status;
	
	public UnauthorizedException(IAuthorizationDetails details) {
		this(details, null);
	}

	public UnauthorizedException(IAuthorizationDetails details, IStatus status) {
		super(String.format("%s See the authorization details for additional information or contact your system administrator.", MSG_BASE));
		this.details = details;
		this.status = status;
	}
	
	public IAuthorizationDetails getAuthorizationDetails() {
		return details;
	}

	@Override
	public String getMessage() {
		String scheme = details.getScheme();
		if(StringUtils.isNotBlank(scheme)){
			return String.format("%s You can access the server using %s authentication.", MSG_BASE, scheme);
		}
		return StringUtils.defaultIfEmpty(details.getMessage(), super.getMessage());
	}

	@Override
	public IStatus getStatus() {
		return this.status;
	}
	
	

}
