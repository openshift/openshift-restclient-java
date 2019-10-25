/******************************************************************************* 
 * Copyright (c) 2019 Red Hat, Inc. 
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
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.openshift.internal.restclient.DefaultClient;
import com.openshift.internal.restclient.authorization.AuthorizationDetails;
import com.openshift.internal.util.URIUtils;
import com.openshift.restclient.IClient;
import com.openshift.restclient.authorization.IAuthorizationContext;
import com.openshift.restclient.authorization.IAuthorizationDetails;
import com.openshift.restclient.authorization.UnauthorizedException;
import com.openshift.restclient.http.IHttpConstants;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

/**
 * Adds authorization means to every request. Authorizes and retrieves the token
 * if it's not present yet.
 */
public class AuthenticatorInterceptor implements Interceptor, IHttpConstants {

    private static final Logger LOGGER = Logger.getLogger(AuthenticatorInterceptor.class);

    public static final String ACCESS_TOKEN = "access_token";
    private static final String AUTH_ATTEMPTS = "X-OPENSHIFT-AUTH-ATTEMPTS";
    private static final String CSRF_TOKEN = "X-CSRF-Token";
    private static final String ERROR = "error";
    private static final String ERROR_DETAILS = "error_details";

    private IClient client;
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String url = request.url().toString();
        if (isUrlWithoutAuthorization(request, url)) {
            return chain.proceed(request);
        }
        IAuthorizationContext authorizationContext = client.getAuthorizationContext();
        if (StringUtils.isBlank(authorizationContext.getToken())) {
            try (Response authResponse = authenticate()) {
                if (authResponse == null
                        || !authResponse.isSuccessful()) {
                    throw new UnauthorizedException(getAuthorizationDetails(url),
                            authResponse == null ? null : ResponseCodeInterceptor.getStatus(authResponse.body().string()));
                }
                String token = getToken(authResponse);
                setToken(token, client.getAuthorizationContext());
                request = new OpenShiftRequestBuilder(request.newBuilder())
                        .acceptJson()
                        .authorization(authorizationContext)
                        .build();
            }
        }
        return chain.proceed(request);
    }

    private boolean isUrlWithoutAuthorization(Request request, String url) {
        return url.endsWith(DefaultClient.PATH_OPENSHIFT_VERSION)
                || url.endsWith(DefaultClient.PATH_KUBERNETES_VERSION)
                || url.endsWith(DefaultClient.PATH_OAUTH_AUTHORIZATION_SERVER)
                || request.url().toString().startsWith(client.getAuthorizationEndpoint().toString())
                || url.endsWith(DefaultClient.URL_HEALTH_CHECK);
    }

    private Response authenticate() throws IOException {
        OkHttpClient okClient = client.adapt(OkHttpClient.class);
        if (okClient == null) {
            return null;
        }
        Request authRequest = appendAuthorization(
                client.getAuthorizationContext(),
                new Request.Builder()
                    .addHeader(CSRF_TOKEN, "1")
                    .url(new URL(client.getAuthorizationEndpoint().toExternalForm() 
                            + "?response_type=token&client_id=openshift-challenging-client").toString()));
        return okClient.newBuilder()
                .followRedirects(false)
                .build()
                .newCall(authRequest)
                .execute();
    }

    private IAuthorizationDetails getAuthorizationDetails(String url) {
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
    }

    private Request appendAuthorization(IAuthorizationContext context, Builder builder) {
        builder.header(AUTH_ATTEMPTS, "1");
        return new BasicChallengeHandler(context).handleChallenge(builder).build();
    }

}
