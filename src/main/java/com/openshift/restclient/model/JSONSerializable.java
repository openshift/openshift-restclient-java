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
package com.openshift.restclient.model;

/**
 * Something that can be serialized to JSON
 * @author jeff.cantrill
 *
 */
public interface JSONSerializable {

    /**
     * The JSON representation
     * @return
     */
    String toJson();

    /**
     * The JSON representation
     * @param compact true if it should be compact; false otherwise
     * @return
     */
    default String toJson(boolean compact) {
        return toJson();
    }
}
