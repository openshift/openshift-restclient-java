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

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;

public class KubeClientConfigConstructor extends Constructor {

    private static final String USERS = "users";
    private static final String CLUSTERS = "clusters";
    private static final String CONTEXTS = "contexts";

    public KubeClientConfigConstructor(PropertyUtils propertyUtils) {
        super(KubeClientConfig.class);

        TypeDescription configTypeDesc = new TypeDescription(KubeClientConfig.class);
        configTypeDesc.addPropertyParameters(CONTEXTS, Context.class);
        configTypeDesc.addPropertyParameters(CLUSTERS, Cluster.class);
        configTypeDesc.addPropertyParameters(USERS, User.class);
        addTypeDescription(configTypeDesc);

        setPropertyUtils(propertyUtils);
    }

}
