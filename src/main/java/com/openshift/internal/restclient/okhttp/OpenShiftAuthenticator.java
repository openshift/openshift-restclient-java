/******************************************************************************* 
 * Copyright (c) 2016-2019 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package com.openshift.internal.restclient.okhttp;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.openshift.internal.restclient.authorization.AuthorizationDetails;
import com.openshift.internal.util.URIUtils;
import com.openshift.restclient.IClient;
import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.authorization.IAuthorizationContext;
import com.openshift.restclient.authorization.IAuthorizationDetails;
import com.openshift.restclient.authorization.UnauthorizedException;
import com.openshift.restclient.http.IHttpConstants;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Adds authentication means to okhttp requests for use in OpenShift
 */
public class OpenShiftAuthenticator implements Authenticator, IHttpConstants {

    public static final String ACCESS_TOKEN = "access_token";
    private static final String AUTH_ATTEMPTS = "X-OPENSHIFT-AUTH-ATTEMPTS";
    private static final String CSRF_TOKEN = "X-CSRF-Token";
    private static final String ERROR = "error";
    private static final String ERROR_DETAILS = "error_details";

    private Collection<IChallangeHandler> challangeHandlers = new ArrayList<>();
    private IClient client;

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        Request request = response.request();
        if (!unauthorizedForCluster(response, request)) {
            return null;
        }
        String requestUrl = request.url().toString();
        try (Response authResponse = tryAuth()) {
            if (authResponse == null
                    || !authResponse.isSuccessful()) {
                throw new UnauthorizedException(getAuthDetails(requestUrl),
                        ResponseCodeInterceptor.getStatus(response.body().string()));
            }
            String token = getToken(authResponse);
            setToken(token, client.getAuthorizationContext());
            return new OpenShiftRequestBuilder(request.newBuilder())
                    .authorization(token)
                    .build();
        } catch(OpenShiftException e) {
            throw new UnauthorizedException(getAuthDetails(requestUrl),
                    ResponseCodeInterceptor.getStatus(response.body().string()));
        }
    }

    private boolean unauthorizedForCluster(Response response, Request request) {
        String requestHost = request.url().host();
        switch (response.code()) {
        case IHttpConstants.STATUS_UNAUTHORIZED:
        case IHttpConstants.STATUS_FORBIDDEN:
            return client.getBaseURL().getHost().equals(requestHost);
        default:
            return false;
        }
    }

    private Response tryAuth() throws IOException {
        OkHttpClient okClient = client.adapt(OkHttpClient.class);
        if (okClient == null) {
            return null;
        }
        Request authRequest = new Request.Builder()
                .addHeader(CSRF_TOKEN, "1")
                .url(new URL(client.getAuthorizationEndpoint().toExternalForm() 
                        + "?response_type=token&client_id=openshift-challenging-client").toString())
                .build();
        return okClient.newBuilder()
                .authenticator(new Authenticator() {
        
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        if (StringUtils.isNotBlank(response.request().header(AUTH_ATTEMPTS))) {
                            return null;
                        }
                        if (StringUtils.isNotBlank(response.header(IHttpConstants.PROPERTY_WWW_AUTHENTICATE))) {
                            for (IChallangeHandler challangeHandler : challangeHandlers) {
                                if (!challangeHandler.canHandle(response.headers())) {
                                    Builder requestBuilder = response.request().newBuilder()
                                            .header(AUTH_ATTEMPTS, "1");
                                    response.close();
                                    return challangeHandler.handleChallenge(requestBuilder).build();
                                }
                            }
                        }
                        return null;
                    }
                })
                .followRedirects(false)
                .build()
                .newCall(authRequest)
                .execute();
    }

    private IAuthorizationDetails getAuthDetails(String url) {
        IAuthorizationDetails details = null;
        Map<String, String> pairs = URIUtils.splitFragment(url);
        if (pairs.containsKey(ERROR)) {
            details = new AuthorizationDetails(pairs.get(ERROR), pairs.get(ERROR_DETAILS));
        }
        return details;
    }

    private String getToken(Response response) {
        String token = null;
        Map<String, String> pairs = URIUtils.splitFragment(response.header(PROPERTY_LOCATION));
        if (pairs.containsKey(ACCESS_TOKEN)) {
            token = pairs.get(ACCESS_TOKEN);
        }
        return token;
    }

    private void setToken(String token, IAuthorizationContext authorizationContext) {
        if (authorizationContext != null) {
            authorizationContext.setToken(token);
        }
    }

    public void setClient(IClient client) {
        this.client = client;
        challangeHandlers.clear();
        challangeHandlers.add(new BasicChallangeHandler(client.getAuthorizationContext()));
    }
}
