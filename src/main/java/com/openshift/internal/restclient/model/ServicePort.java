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

import static com.openshift.internal.util.JBossDmrExtentions.asInt;
import static com.openshift.internal.util.JBossDmrExtentions.asString;
import static com.openshift.internal.util.JBossDmrExtentions.set;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.dmr.ModelNode;

import com.openshift.restclient.model.IServicePort;

public class ServicePort extends ModelNodeAdapter implements IServicePort {
    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_PORT = "port";
    private static final String PROPERTY_PROTOCOL = "protocol";
    private static final String PROPERTY_TARGET_PORT = "targetPort";
    private static final String PROPERTY_NODE_PORT = "nodePort";
    private static final Map<String, String[]> KEY_MAP = new HashMap<>();

    static {
        KEY_MAP.put(PROPERTY_NAME, new String[] { PROPERTY_NAME });
        KEY_MAP.put(PROPERTY_PORT, new String[] { PROPERTY_PORT });
        KEY_MAP.put(PROPERTY_PROTOCOL, new String[] { PROPERTY_PROTOCOL });
        KEY_MAP.put(PROPERTY_TARGET_PORT, new String[] { PROPERTY_TARGET_PORT });
        KEY_MAP.put(PROPERTY_NODE_PORT, new String[] { PROPERTY_NODE_PORT });
    }

    public ServicePort(ModelNode node) {
        super(node, KEY_MAP);
    }

    /**
     * copy constructor
     * 
     * @param node the node to copy
     * @param port the service ort 
     */
    public ServicePort(ModelNode node, IServicePort port) {
        this(node);
        setName(port.getName());
        setPort(port.getPort());
        setProtocol(port.getProtocol());
        setTargetPort(port.getTargetPort());
        setNodePort(port.getNodePort());
    }

    @Override
    public String getName() {
        return asString(getNode(), KEY_MAP, PROPERTY_NAME);
    }

    @Override
    public void setName(String name) {
        set(getNode(), KEY_MAP, PROPERTY_NAME, name);
    }

    @Override
    public String getTargetPort() {
        return asString(getNode(), KEY_MAP, PROPERTY_TARGET_PORT);
    }

    @Override
    public void setTargetPort(int port) {
        set(getNode(), KEY_MAP, PROPERTY_TARGET_PORT, port);
    }

    @Override
    public void setTargetPort(String name) {
        if (StringUtils.isNumeric(name)) {
            setTargetPort((Integer.parseInt(name)));
            return;
        }
        set(getNode(), KEY_MAP, PROPERTY_TARGET_PORT, name);
    }

    @Override
    public void setPort(int port) {
        set(getNode(), KEY_MAP, PROPERTY_PORT, port);
    }

    @Override
    public int getPort() {
        return asInt(getNode(), KEY_MAP, PROPERTY_PORT);
    }

    @Override
    public String getProtocol() {
        return asString(getNode(), KEY_MAP, PROPERTY_PROTOCOL);
    }

    public void setProtocol(String proto) {
        set(getNode(), KEY_MAP, PROPERTY_PROTOCOL, proto);
    }

    @Override
    public String getNodePort() {
        return asString(getNode(), KEY_MAP, PROPERTY_NODE_PORT);
    }

    @Override
    public void setNodePort(String nodePort) {
        set(getNode(), KEY_MAP, PROPERTY_NODE_PORT, nodePort);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getPort();
        result = prime * result + getTargetPort().hashCode();
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getProtocol() == null) ? 0 : getProtocol().hashCode());
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
        ServicePort other = (ServicePort) obj;
        if (getPort() != other.getPort()) {
            return false;
        }
        if (getTargetPort() == null) {
            if (other.getTargetPort() != null) {
                return false;
            }
        } else if (!getTargetPort().equals(other.getTargetPort())) {
            return false;
        }
        if (getName() == null) {
            if (other.getName() != null) {
                return false;
            }
        } else if (!getName().equals(other.getName())) {
            return false;
        }
        if (getProtocol() == null) {
            if (other.getProtocol() != null) {
                return false;
            }
        } else if (!getProtocol().equals(other.getProtocol())) {
            return false;
        }
        if (getNodePort() == null) {
            if (other.getNodePort() != null) {
                return false;
            }
        } else if (!getNodePort().equals(other.getNodePort())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return toJson(false);
    }

}
