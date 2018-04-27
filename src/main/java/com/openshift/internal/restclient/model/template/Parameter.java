/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.model.template;

import org.apache.commons.lang.StringUtils;
import org.jboss.dmr.ModelNode;

import com.openshift.restclient.model.template.IParameter;

/**
 * Parameter implementation for a template Foregoing versioned implementation of
 * this type for now since it is unlikely to change and it is not a versioned
 * resource in Kubernetes. Update as needed
 */
public class Parameter implements IParameter {

    private static final String VALUE = "value";

    private ModelNode node;

    public Parameter(ModelNode node) {
        this.node = node;
    }

    @Override
    public IParameter clone() {
        return new Parameter(node.clone());
    }

    @Override
    public String getName() {
        return asString("name");
    }

    @Override
    public String getDescription() {
        return asString("description");
    }

    @Override
    public void setValue(String value) {
        node.get(VALUE).set(value);
    }

    @Override
    public String getValue() {
        return asString(VALUE);
    }

    @Override
    public String getGeneratorName() {
        return StringUtils.defaultIfEmpty(asString("generate"), asString("generator"));
    }

    @Override
    public String getFrom() {
        return asString("from");
    }

    @Override
    public boolean isRequired() {
        if (node.hasDefined("required")) {
            return node.get("required").asBoolean();
        }
        return false;
    }

    private String asString(String key) {
        ModelNode value = node.get(key);
        if (value.isDefined()) {
            return value.asString();
        }
        return "";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getFrom() == null) ? 0 : getFrom().hashCode())
                + ((getGeneratorName() == null) ? 0 : getGeneratorName().hashCode())
                + ((getName() == null) ? 0 : getName().hashCode()) + ((getValue() == null) ? 0 : getValue().hashCode())
                + Boolean.valueOf(isRequired()).hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Parameter other = (Parameter) obj;
        if (getFrom() == null) {
            if (other.getFrom() != null) {
                return false;
            }
        } else if (!getFrom().equals(other.getFrom())) {
            return false;
        }
        if (getGeneratorName() == null) {
            if (other.getGeneratorName() != null) {
                return false;
            }
        } else if (!getGeneratorName().equals(other.getGeneratorName())) {
            return false;
        }
        if (getName() == null) {
            if (other.getName() != null) {
                return false;
            }
        } else if (!getName().equals(other.getName())) {
            return false;
        }
        if (getValue() == null) {
            if (other.getValue() != null) {
                return false;
            }
        } else if (!getValue().equals(other.getValue())) {
            return false;
        }
        if (isRequired() != other.isRequired()) {
            return false;
        }
        return true;
    }

}
