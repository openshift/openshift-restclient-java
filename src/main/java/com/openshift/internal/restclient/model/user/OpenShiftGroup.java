/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 *
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 *     Roland T. Lichti - implementation of user.openshift.io/v1/groups
 ******************************************************************************/

package com.openshift.internal.restclient.model.user;

import java.util.Map;
import java.util.Set;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.restclient.IClient;
import com.openshift.restclient.model.user.IGroup;

public class OpenShiftGroup extends KubernetesResource implements IGroup {

    private static final String USERS = "users";

    public OpenShiftGroup(ModelNode node, IClient client, Map<String, String[]> propertyKeys) {
        super(node, client, propertyKeys);
    }

    @Override
    public String getUID() {
        return asString("metadata.uid");
    }

    @Override
    public Set<String> getUsers() {
        //noinspection unchecked
        return (Set<String>) asSet(USERS, ModelType.STRING);
    }
}
