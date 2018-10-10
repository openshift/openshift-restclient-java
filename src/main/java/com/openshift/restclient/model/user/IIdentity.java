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
 *     Roland T. Lichti - implementation of user.openshift.io/v1/identities
 ******************************************************************************/

package com.openshift.restclient.model.user;

import java.util.Map;

import com.openshift.restclient.model.IObjectReference;
import com.openshift.restclient.model.IResource;

/**
 * The identity as specified by the identity provider.
 */
public interface IIdentity extends IResource {

    /**
     *
     * @return the username as specified by the idententity provider.
     */
    String getUserName();

    /**
     *
     * @return the identity uid as specified in the metadata
     */
    String getUID();

    /**
     *
     * @return the name of the identity provider
     */
    String getProviderName();

    /**
     *
     * @return a map of the identity provider specific data.
     */
    Map<String, String> getExtra();

    /**
     *
     * @return A resource link to the user.
     */
    IObjectReference getUser();
}
