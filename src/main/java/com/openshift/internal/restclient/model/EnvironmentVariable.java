/*******************************************************************************
 * Copyright (c) 2016-2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.internal.restclient.model;

import static com.openshift.internal.util.JBossDmrExtentions.asString;

import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.model.properties.ResourcePropertyKeys;
import com.openshift.restclient.model.IConfigMapKeySelector;
import com.openshift.restclient.model.IEnvironmentVariable;
import com.openshift.restclient.model.IObjectFieldSelector;
import com.openshift.restclient.model.ISecretKeySelector;

public class EnvironmentVariable extends ModelNodeAdapter implements IEnvironmentVariable, ResourcePropertyKeys {

    private static final String PROP_VALUE_FROM = "valueFrom";
    private static final String PROP_FIELD_REF = "fieldRef";
    private static final String PROP_CONFIG_MAP_KEY_REF = "configMapKeyRef";
    private static final String PROP_SECRET_KEY_REF = "secretKeyRef";

    public EnvironmentVariable(ModelNode node, Map<String, String[]> propertyKeys) {
        super(node, propertyKeys);
    }

    @Override
    public String getName() {
        return asString(getNode(), getPropertyKeys(), NAME);
    }

    @Override
    public String getValue() {
        return asString(getNode(), getPropertyKeys(), VALUE);
    }

    @Override
    public IEnvVarSource getValueFrom() {
        ModelNode valueFrom = getNode().get(PROP_VALUE_FROM);
        if (valueFrom == null) {
            return null;
        }
        if (valueFrom.hasDefined(PROP_FIELD_REF)) {
            return createObjectFieldSelector(valueFrom);
        } else if (valueFrom.hasDefined(PROP_CONFIG_MAP_KEY_REF)) {
            return createConfigMapKeySelector(valueFrom);
        } else if (valueFrom.hasDefined(PROP_SECRET_KEY_REF)) {
            return createSecretKeySelector(valueFrom);
        }
        return null;
    }

    private IEnvVarSource createSecretKeySelector(ModelNode valueFrom) {
        return new ISecretKeySelector() {

            @Override
            public String getName() {
                return asString(valueFrom, getPropertyKeys(), PROP_SECRET_KEY_REF + ".name");
            }

            @Override
            public String getKey() {
                return asString(valueFrom, getPropertyKeys(), PROP_SECRET_KEY_REF + ".key");
            }
        };
    }

    private IEnvVarSource createConfigMapKeySelector(ModelNode valueFrom) {
        return new IConfigMapKeySelector() {

            @Override
            public String getName() {
                return asString(valueFrom, getPropertyKeys(), PROP_CONFIG_MAP_KEY_REF + ".name");
            }

            @Override
            public String getKey() {
                return asString(valueFrom, getPropertyKeys(), PROP_CONFIG_MAP_KEY_REF + ".key");
            }
        };
    }

    private IEnvVarSource createObjectFieldSelector(ModelNode valueFrom) {
        return new IObjectFieldSelector() {

            @Override
            public String getFieldPath() {
                return asString(valueFrom, getPropertyKeys(), PROP_FIELD_REF + ".fieldPath");
            }

        };
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof IEnvironmentVariable)) {
            return false;
        }

        IEnvironmentVariable otherVar = (IEnvironmentVariable) object;
        String thisName = getName();
        String otherName = otherVar.getName();
        if (thisName == null) {
            if (otherName != null) {
                return false;
            }
        } else {
            if (!thisName.equals(otherName)) {
                return false;
            }
        }

        String thisValue = getValue();
        String otherValue = otherVar.getValue();
        if (thisValue == null) {
            if (otherValue != null) {
                return false;
            }
        } else {
            if (!thisValue.equals(otherValue)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        String name = getName();
        String value = getValue();
        return 37 * (name == null ? 0 : name.hashCode()) + (value == null ? 0 : value.hashCode());
    }
}
