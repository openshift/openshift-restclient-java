/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.restclient.model;

import java.util.Map;
import java.util.Set;

import com.openshift.restclient.api.models.IAnnotatable;
import com.openshift.restclient.api.models.ITypeMeta;
import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.capability.ICapable;

/**
 * IResource is a representation of a Kubernetes resource (e.g. Service, Pod,
 * ReplicationController)
 * 
 */
public interface IResource extends ICapable, Annotatable, IAnnotatable, JSONSerializeable, ITypeMeta {

    Map<String, String> getMetadata();

    /**
     * @return the list of capabilities supported by this resource
     */
    Set<Class<? extends ICapability>> getCapabilities();

    /**
     * Returns the timestamp of when this resource was created
     * 
     */
    String getCreationTimeStamp();

    /**
     * Returns the identifier for this resource
     * 
     */
    String getName();

    /**
     * Returns the scope of this resource
     * 
     */
    String getNamespaceName();

    /**
     * Return the project of the resource
     * 
     */
    IProject getProject();

    /**
     * Return the namespace of the resource
     * 
     */
    INamespace getNamespace();

    /**
     * Retrieves the labels associated with the resource
     * 
     */
    Map<String, String> getLabels();

    /**
     * Add or update a label;
     * 
     */
    void addLabel(String key, String value);

    /**
     * Returns <code>true</code> if the resource is annotated with the given key
     * 
     * @return true if the annotation key exists
     */
    boolean isAnnotatedWith(String key);

    /**
     * Retrieves the annotated value for the given key
     * 
     */
    String getAnnotation(String key);

    /**
     * Set the resource annotation
     * 
     */
    void setAnnotation(String key, String value);

    /**
     * Removes the resource annotation
     * 
     */
    void removeAnnotation(String key);

    /**
     * Retrieves the annotations associated with the resource
     * 
     */
    Map<String, String> getAnnotations();

    String getResourceVersion();

}
