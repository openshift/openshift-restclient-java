/*******************************************************************************
 * Copyright (c) 2019-2020 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Function;

import org.jboss.dmr.ModelNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.OkHttpClient;

public class AuthorizationEndpoints  {

    public static final String PATH_OAUTH_AUTHORIZATION_SERVER = ".well-known/oauth-authorization-server";

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationEndpoints.class);

    private RequestingSupplier<ModelNode> endpointsSupplier;
    
    protected AuthorizationEndpoints(String baseUrl, OkHttpClient client) {
        this.endpointsSupplier = new RequestingSupplier<ModelNode>(
                baseUrl + "/" + PATH_OAUTH_AUTHORIZATION_SERVER,
                "authorization- & token-endpoint",
                client) {

            @Override
            protected ModelNode extractValue(String response) {
                return ModelNode.fromJSONString(response);
            }

            @Override
            protected ModelNode getDefaultValue() {
                return null;
            }
        };
    }

    public URL getAuthorizationEndpoint() {
        return getEndpoint(node -> node.get("authorization_endpoint").asString(), "authorization-endpoint");
    }

    public URL getTokenEndpoint() {
        return getEndpoint(node -> node.get("token_endpoint").asString(), "token_endpoint");
    }

    private URL getEndpoint(Function<ModelNode, String> extractor, String description) {
        URL authorizationEndpoint = null;
        try {
            ModelNode node = endpointsSupplier.get();
            if (node != null) {
                try {
                    authorizationEndpoint = new URL(extractor.apply(node));
                } catch (MalformedURLException e) {
                    LOGGER.error("Failed to determine {}.", description, e);
                }
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error("Could not determine {} endpoint: invalid JSON.", description);
        }
        return authorizationEndpoint;

    }
}