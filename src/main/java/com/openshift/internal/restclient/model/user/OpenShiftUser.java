/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.internal.restclient.model.user;

import java.util.Map;
import java.util.Set;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.restclient.IClient;
import com.openshift.restclient.model.user.IUser;

public class OpenShiftUser extends KubernetesResource implements IUser {

    private static final String USER_FULLNAME = "fullName";
    private static final String GROUPS = "groups";
    private static final String IDENTITES = "identities";

    public OpenShiftUser(ModelNode node, IClient client, Map<String, String[]> propertyKeys) {
        super(node, client, propertyKeys);
    }

    @Override
    public String getFullName() {
        return asString(USER_FULLNAME);
    }

    @Override
    public String getUID() {
        return asString(METADATA_UID);
    }

    @Override
    public Set<String> getGroups() {
        return asSet(GROUPS, ModelType.STRING);
    }

    @Override
    public Set<String> getIdentities() {
        return asSet(IDENTITES, ModelType.STRING);
    }
}
