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

package com.openshift.restclient.model.user;

import java.util.Set;

import com.openshift.restclient.model.IResource;

/**
 * The group definition within OpenShift.
 */
public interface IGroup extends IResource {

    /**
     *
     * @return the group uid as specified in the metadata
     */
    String getUID();

    /**
     *
     * @return the users of this group
     */
    Set<String> getUsers();
}
