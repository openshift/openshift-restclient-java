/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package com.openshift.restclient.api.models;

import java.util.Map;

/**
 * A resource that can be annotated
 *
 */
public interface IAnnotatable {

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
     * Retrieves the annotations associated with the resource
     * 
     */
    Map<String, String> getAnnotations();

}
