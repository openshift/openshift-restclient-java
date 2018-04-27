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

package com.openshift.internal.restclient.model;

import java.util.Collections;
import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.util.JBossDmrExtentions;

/**
 * Builder to streamline the creation of ModelNode trees
 *
 */
public class ModelNodeBuilder {

    private static final Map<String, String[]> PROPS = Collections.emptyMap();
    private ModelNode node;

    public ModelNodeBuilder() {
        this.node = new ModelNode();
    }

    public ModelNodeBuilder set(String path, String value) {
        JBossDmrExtentions.set(node, PROPS, path, value);
        return this;
    }

    public ModelNodeBuilder set(String path, ModelNode child) {
        JBossDmrExtentions.get(node, PROPS, path).set(child);
        return this;
    }

    public ModelNodeBuilder set(String path, int value) {
        JBossDmrExtentions.set(node, PROPS, path, value);
        return this;
    }

    public ModelNodeBuilder set(String path, boolean value) {
        JBossDmrExtentions.set(node, PROPS, path, value);
        return this;
    }

    public ModelNodeBuilder add(String path, ModelNodeBuilder builder) {
        node.get(JBossDmrExtentions.getPath(path)).add(builder.build());
        return this;
    }

    public ModelNode build() {
        return this.node;
    }

}
