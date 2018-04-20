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

import java.util.Map;

/**
 * A builder for building up resources
 *
 */
@SuppressWarnings("rawtypes")
public interface IResourceBuilder<T extends IResource, B extends IResourceBuilder> {

    B named(String name);

    B inNamespace(String name);

    B withLabels(Map<String, String> labels);

    T build();

    interface Endable {

        IResourceBuilder<? extends IResource, ?> end();
    }
}
