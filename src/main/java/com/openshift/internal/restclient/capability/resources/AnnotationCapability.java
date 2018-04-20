/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.capability.resources;

import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.model.IResource;

/**
 * Determine if a resource has a capability if it has the given annotation
 * 
 */
public abstract class AnnotationCapability implements ICapability {

    private final IResource resource;
    private final String name;

    public AnnotationCapability(String name, IResource resource) {
        this.resource = resource;
        this.name = name;
    }

    @Override
    public boolean isSupported() {
        return resource.isAnnotatedWith(getAnnotationKey());
    }

    @Override
    public String getName() {
        return this.name;
    }

    protected IResource getResource() {
        return this.resource;
    }

    /**
     * The annotation key
     * 
     */
    protected abstract String getAnnotationKey();

    /**
     * The annotations value
     * 
     */
    public String getAnnotationValue() {
        return getResource().getAnnotation(getAnnotationKey());
    }
}
