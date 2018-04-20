/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.internal.restclient.model.kubeclient;

import java.util.ArrayList;
import java.util.Collection;

import com.openshift.restclient.model.kubeclient.ICluster;
import com.openshift.restclient.model.kubeclient.IContext;
import com.openshift.restclient.model.kubeclient.IKubeClientConfig;
import com.openshift.restclient.model.kubeclient.IUser;

/**
 * Kube Client config impl
 * 
 *
 */
public class KubeClientConfig implements IKubeClientConfig {

    private Collection<ICluster> clusters = new ArrayList<>();
    private Collection<IContext> contexts = new ArrayList<>();
    private String currentContext = "";
    private Collection<IUser> users = new ArrayList<>();

    public void setApiVersion(String apiVersion) {

    }

    @Override
    public Collection<ICluster> getClusters() {
        return clusters;
    }

    public void setClusters(Collection<ICluster> clusters) {
        this.clusters = clusters;
    }

    @Override
    public Collection<IContext> getContexts() {
        return contexts;
    }

    public void setContexts(Collection<IContext> contexts) {
        this.contexts = contexts;
    }

    @Override
    public String getCurrentContext() {
        // TODO Auto-generated method stub
        return currentContext;
    }

    public void setCurrentContext(String currentContext) {
        this.currentContext = currentContext;
    }

    @Override
    public Collection<IUser> getUsers() {
        return users;
    }

    public void setUsers(Collection<IUser> users) {
        this.users = users;
    }

}
