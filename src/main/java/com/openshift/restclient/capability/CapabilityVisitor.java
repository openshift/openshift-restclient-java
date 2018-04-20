/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.restclient.capability;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * A visitor used to access a resources capability
 * 
 * @param <T>
 *            The capability type
 * @param <R>
 *            A context significant to the visitor
 * 
 */
public abstract class CapabilityVisitor<T extends ICapability, R> {

    private Type type;

    /**
     * Visits the capability
     * 
     */
    public abstract R visit(T capability);

    /**
     * Gets the Capability type
     * 
     */
    public final Type getCapabilityType() {
        if (type == null) {
            Type superclass = getClass().getGenericSuperclass();
            if (superclass instanceof Class) {
                throw new RuntimeException("Missing type parameter.");
            }
            this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
        }
        return type;
    }

}
