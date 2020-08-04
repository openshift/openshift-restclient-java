/******************************************************************************* 
 * Copyright (c) 2019-2020 Red Hat, Inc. 
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

import com.openshift.internal.restclient.AuthorizationEndpoints;
import com.openshift.internal.restclient.DefaultClient;
import com.openshift.internal.restclient.authorization.AuthorizationDetails;
import com.openshift.internal.util.URIUtils;
import com.openshift.restclient.IClient;
import com.openshift.restclient.authorization.IAuthorizationContext;
import com.openshift.restclient.authorization.IAuthorizationDetails;
import com.openshift.restclient.authorization.UnauthorizedException;
import com.openshift.restclient.http.IHttpConstants;

import okhttp3.Headers;
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

    public static final String ACCESS_TOKEN = "access_token";
    public static final String EXPIRES_IN = "expires_in";
    private static final String CSRF_TOKEN = "X-CSRF-Token";
    private static final String ERROR = "error";
    private static final String ERROR_DETAILS = "error_details";

    private IClient client;
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String url = request.url().toString();
        if (AuthAttempHeader.isContainedIn(request.headers())
                || isUrlWithoutAuthorization(url)) {
            return chain.proceed(request);
        }
        IAuthorizationContext authorizationContext = client.getAuthorizationContext();
        if (StringUtils.isBlank(authorizationContext.getToken())) {
            request = createAuthorizationRequest(request, url, authorizationContext);
        }
        return chain.proceed(request);
    }

    private Request createAuthorizationRequest(Request request, String url, IAuthorizationContext authorizationContext) throws IOException {
        try (Response authResponse = authenticate()) {
            if (authResponse != null) {
                if (!authResponse.isSuccessful()) {
                    throw new UnauthorizedException(getAuthorizationDetails(url),
                            ResponseCodeInterceptor.getStatus(authResponse.body().string()));
                }
                Map<String, String> location = getLocation(authResponse);
                setToken(getToken(location), authorizationContext);
                setExpiresIn(getExpiresIn(location), authorizationContext);
                request = new OpenShiftRequestBuilder(request.newBuilder()).acceptJson()
                        .authorization(authorizationContext)
                        .build();
            }
            return request;
        }
    }

    private boolean isUrlWithoutAuthorization(String url) {
        return url.endsWith(DefaultClient.PATH_OPENSHIFT_VERSION)
                || url.endsWith(DefaultClient.PATH_KUBERNETES_VERSION)
                || url.endsWith(DefaultClient.PATH_HEALTH_CHECK)
                || url.endsWith(AuthorizationEndpoints.PATH_OAUTH_AUTHORIZATION_SERVER)
                || isAuthorizationEndpoint(url, client.getAuthorizationEndpoint());
    }

    private boolean isAuthorizationEndpoint(String url, URL authEndpoint) {
        return authEndpoint != null
                && url.startsWith(authEndpoint.toString());
    }

    private Response authenticate() throws IOException {
        OkHttpClient okClient = client.adapt(OkHttpClient.class);
        if (okClient == null) {
            return null;
        }
        URL endpoint = client.getAuthorizationEndpoint();
        if (endpoint == null) {
            return null;
        }
        Request authRequest = appendAuthorization(
                client.getAuthorizationContext(),
                new Request.Builder()
                    .addHeader(CSRF_TOKEN, "1")
                    .url(new URL(endpoint.toExternalForm() 
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

    private Map<String, String> getLocation(Response response) {
        if (response == null) {
            return null;
        }
        return URIUtils.splitFragment(response.header(PROPERTY_LOCATION));
    }
    
    private String getToken(Map<String, String> location) {
        if (location == null
                || location.isEmpty()) {
            return null;
        }
        return location.get(ACCESS_TOKEN);
    }

    private String getExpiresIn(Map<String, String> location) {
        if (location == null
                || location.isEmpty()) {
            return null;
        }
        return location.get(EXPIRES_IN);
    }

    private void setToken(String token, IAuthorizationContext authorizationContext) {
        if (authorizationContext != null) {
            authorizationContext.setToken(token);
        }
    }

    private void setExpiresIn(String expiresIn, IAuthorizationContext authorizationContext) {
        if (authorizationContext != null) {
            authorizationContext.setExpiresIn(expiresIn);
        }
    }

    public void setClient(IClient client) {
        this.client = client;
    }

    private Request appendAuthorization(IAuthorizationContext context, Builder builder) {
        AuthAttempHeader.add(builder);
        return new BasicChallengeHandler(context).handleChallenge(builder).build();
    }

    private static class AuthAttempHeader {

        private static final String AUTH_ATTEMPTS = "X-OPENSHIFT-AUTH-ATTEMPTS";

        public static void add(Builder builder) {
            builder.header(AUTH_ATTEMPTS, "1");    
        }

        public static boolean isContainedIn(Headers headers) {
            return headers != null
                    && headers.get(AUTH_ATTEMPTS) != null;
        }
    }
}
