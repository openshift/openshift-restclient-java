/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

import com.openshift.restclient.IClient;
import com.openshift.restclient.model.serviceaccount.IServiceAccount;
import org.jetbrains.annotations.NotNull;

/**
 * @author David Simansky | dsimansk@redhat.com
 */
public class ServiceAccount extends KubernetesResource implements IServiceAccount {

    private static final String SERVICE_ACCOUNT_SECRETS = "secrets";
    private static final String SERVICE_ACCOUNT_IMAGE_PULL_SECRETS = "imagePullSecrets";

    public ServiceAccount(ModelNode node, IClient client, Map<String, String[]> propertyKeys) {
        super(node, client, propertyKeys);
    }

    @Override
    public Collection<String> getSecrets() {
        return getServiceAccountSecrets(SERVICE_ACCOUNT_SECRETS);
    }

    @NotNull
    private Collection<String> getServiceAccountSecrets(String serviceAccountSecrets) {
        Collection<String> secrets = new ArrayList<>();
        ModelNode node = get(serviceAccountSecrets);
        if (node.getType() != ModelType.LIST) {
            return secrets;
        }
        for (ModelNode entry : node.asList()) {
            secrets.add(asString(entry, NAME));
        }
        return secrets;
    }

    @Override
    public void addSecret(String secret) {
        ModelNode secretNodeName = get(SERVICE_ACCOUNT_SECRETS).add();
        set(secretNodeName, NAME, secret);
    }

    @Override
    public Collection<String> getImagePullSecrets() {
        return getServiceAccountSecrets(SERVICE_ACCOUNT_IMAGE_PULL_SECRETS);
    }

    @Override
    public void addImagePullSecret(String imagePullSecret) {
        ModelNode secretNodeName = get(SERVICE_ACCOUNT_IMAGE_PULL_SECRETS).add();
        set(secretNodeName, NAME, imagePullSecret);
    }

}
