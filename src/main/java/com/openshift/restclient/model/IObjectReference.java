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

package com.openshift.restclient.model;

/**
 * An OpenShift object reference to an OpenShift object
 *
 */
public interface IObjectReference {

    /**
     * Returns the resource kind
     * 
     */
    String getKind();

    /**
     * The obj ref kind
     * 
     */
    void setKind(String kind);

    /**
     * returns the api version of this resource
     * 
     */
    String getApiVersion();

    /**
     * returns the resource version of this resource
     * 
     */
    String getResourceVersion();

    /**
     * Returns the identifier for this resource
     * 
     */
    String getName();

    /**
     * The name of the obj ref
     * 
     */
    void setName(String name);

    /**
     * Returns the scope of this resource
     * 
     */
    String getNamespace();

    /**
     * The namespace for the object ref
     * 
     */
    void setNamespace(String namespace);

    String getFieldPath();

    String getUID();

    String toJson();
}
