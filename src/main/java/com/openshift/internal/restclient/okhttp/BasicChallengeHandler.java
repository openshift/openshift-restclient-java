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

import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.Request.Builder;

public class BasicChallengeHandler implements IChallengeHandler {

    private IAuthorizationContext context;

    public BasicChallengeHandler(IAuthorizationContext context) {
        this.context = context;
    }

    @Override
    public boolean canHandle(Headers headers) {
        return IHttpConstants.AUTHORIZATION_BASIC
                .equalsIgnoreCase(headers.get(IHttpConstants.PROPERTY_WWW_AUTHENTICATE));
    }

    @Override
    public Builder handleChallenge(Builder builder) {
        return builder.header(IHttpConstants.PROPERTY_AUTHORIZATION,
                Credentials.basic(
                        StringUtils.defaultIfBlank(context.getUserName(), ""),
                        StringUtils.defaultIfBlank(context.getPassword(), "")));
    }

}