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
package com.openshift.internal.restclient.okhttp;

import org.apache.commons.lang.StringUtils;

import com.openshift.restclient.authorization.IAuthorizationContext;
import com.openshift.restclient.http.IHttpConstants;
import com.openshift.restclient.utils.Base64Coder;

import okhttp3.Headers;
import okhttp3.Request.Builder;

/**
 * 
 * @author jeff.cantrill
 *
 */
public class BasicChallangeHandler implements IChallangeHandler{

	private IAuthorizationContext context;

	public BasicChallangeHandler(IAuthorizationContext context) {
		this.context = context;
	}

	@Override
	public boolean canHandle(Headers headers) {
		return OpenShiftAuthenticator.AUTHORIZATION_BASIC.equalsIgnoreCase(headers.get(OpenShiftAuthenticator.PROPERTY_WWW_AUTHENTICATE));
	}

	@Override
	public Builder handleChallange(Builder builder) {
		StringBuilder value = new StringBuilder(context.getUserName()).append(":");
		if(StringUtils.isNotBlank(context.getPassword())) {
			value.append(context.getPassword());
		}
		return builder.header(OpenShiftAuthenticator.PROPERTY_AUTHORIZATION, IHttpConstants.AUTHORIZATION_BASIC + " " + Base64Coder.encode(value.toString()));
	}
	
}