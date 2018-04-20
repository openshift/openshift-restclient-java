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

package com.openshift.internal.restclient.api.models;

import static com.openshift.internal.util.JBossDmrExtentions.asString;

import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.model.ModelNodeAdapter;
import com.openshift.internal.restclient.model.properties.ResourcePropertyKeys;
import com.openshift.restclient.api.models.ITypeMeta;

public class TypeMeta extends ModelNodeAdapter implements ITypeMeta, ResourcePropertyKeys {

    /**
     * 
     * @param node the node
     * @param propertyKeys
     *            overrides based on version
     */
    public TypeMeta(ModelNode node, Map<String, String[]> propertyKeys) {
        super(node, propertyKeys);
    }

    @Override
    public String getApiVersion() {
        return asString(getNode(), getPropertyKeys(), APIVERSION);
    }

    @Override
    public String getKind() {
        return asString(getNode(), getPropertyKeys(), KIND);
    }

}
