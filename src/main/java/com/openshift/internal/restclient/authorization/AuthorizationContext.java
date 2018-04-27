/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.authorization;

import org.apache.commons.lang.StringUtils;

import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.authorization.IAuthorizationContext;
import com.openshift.restclient.authorization.IAuthorizationDetails;
import com.openshift.restclient.model.user.IUser;

public class AuthorizationContext implements IAuthorizationContext {

    private String token;
    private String expires;
    private String scheme;
    private IUser user;
    private String userName;
    private String password;
    private IClient client;

    public AuthorizationContext(String scope) {
        this.scheme = scope;
    }

    public AuthorizationContext(String token, String userName, String password) {
        this.token = token;
        this.userName = userName;
        this.password = password;
    }

    public AuthorizationContext(String token, String expires, IUser user, String scheme) {
        this.token = token;
        this.expires = expires;
        this.user = user;
        this.scheme = scheme;
    }

    public AuthorizationContext clone() {
        AuthorizationContext context = new AuthorizationContext(this.token, this.expires, this.user, this.scheme);
        context.setUserName(this.userName);
        context.setPassword(this.password);
        context.setClient(this.client);
        return context;
    }

    public void setClient(IClient client) {
        this.client = client;
    }

    @Override
    public boolean isAuthorized() {
        if (user == null) {
            synchronized (this) {
                user = client.get(ResourceKind.USER, "~", "");
            }
        }
        return StringUtils.isNotEmpty(token);
    }

    @Override
    public IAuthorizationDetails getAuthorizationDetails() {
        return new AuthorizationDetails(String.format("%s/oauth/token/request", client.getBaseURL()));
    }

    @Override
    public String getToken() {
        return this.token;
    }

    @Override
    public String getExpiresIn() {
        return expires;
    }

    @Override
    public String getAuthScheme() {
        return scheme;
    }

    @Override
    public IUser getUser() {
        return user;
    }

    public void setUser(IUser user) {
        this.user = user;
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getUserName() {
        return this.userName;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

}
