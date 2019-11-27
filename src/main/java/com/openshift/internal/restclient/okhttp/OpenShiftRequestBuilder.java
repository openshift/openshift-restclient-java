/*******************************************************************************
 * Copyright (c) 2019 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.internal.restclient.okhttp;

import static com.openshift.restclient.http.IHttpConstants.MEDIATYPE_APPLICATION_JSON;
import static com.openshift.restclient.http.IHttpConstants.PROPERTY_ACCEPT;

import java.net.URL;

import org.apache.commons.lang.StringUtils;

import com.openshift.restclient.authorization.IAuthorizationContext;
import com.openshift.restclient.http.IHttpConstants;

import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;

public class OpenShiftRequestBuilder {

    private Builder builder;

    public OpenShiftRequestBuilder() {
        this(new Builder());
    }

    public OpenShiftRequestBuilder(Builder builder) {
        this.builder = builder;
    }

    public OpenShiftRequestBuilder accept(String mediaType) {
        builder.header(PROPERTY_ACCEPT, mediaType);
        return this;
    }

    public OpenShiftRequestBuilder acceptJson() {
        builder.header(PROPERTY_ACCEPT, MEDIATYPE_APPLICATION_JSON);
        return this;
    }

    public OpenShiftRequestBuilder authorization(IAuthorizationContext authorizationContext) {
        String token = null;
        if (authorizationContext != null 
                && StringUtils.isNotBlank(authorizationContext.getToken())) {
            token = authorizationContext.getToken();
        }
        return authorization(token);
    }

    public OpenShiftRequestBuilder authorization(String token) {
        if (!StringUtils.isBlank(token)) {
            builder.header(IHttpConstants.PROPERTY_AUTHORIZATION, IHttpConstants.AUTHORIZATION_BEARER + " " + token);
        }
        return this;
    }

    public OpenShiftRequestBuilder url(URL url) {
        builder.url(url);
        return this;
    }

    public OpenShiftRequestBuilder url(String url) {
        builder.url(url);
        return this;
    }

    public OpenShiftRequestBuilder method(String method, RequestBody body) {
        builder.method(method, body);
        return this;
    }

    public OpenShiftRequestBuilder addHeader(String key, String value) {
        builder.addHeader(key, value);
        return this;
    }

    public OpenShiftRequestBuilder header(String key, String value) {
        builder.header(key, value);
        return this;
    }

    public OpenShiftRequestBuilder tag(Object tag) {
        builder.tag(tag);
        return this;
    }

    public Builder builder() {
        return builder;
    }

    public Request build() {
        return builder.build();
    }
}
