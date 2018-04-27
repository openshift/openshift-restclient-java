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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.internal.restclient.model.ModelNodeAdapter;
import com.openshift.internal.restclient.model.ObjectReference;
import com.openshift.internal.util.JBossDmrExtentions;
import com.openshift.restclient.IClient;
import com.openshift.restclient.api.models.IEndpoints;
import com.openshift.restclient.model.IObjectReference;

public class Endpoints extends KubernetesResource implements IEndpoints {

    public Endpoints(ModelNode node, IClient client, Map<String, String[]> overrideProperties) {
        super(node, client, overrideProperties);
    }

    @Override
    public List<IEndpointSubset> getSubSets() {
        List<ModelNode> root = get("subsets").asList();
        ArrayList<IEndpointSubset> subsets = new ArrayList<IEndpointSubset>(root.size());
        for (ModelNode n : root) {
            subsets.add(new EndpointSubset(n, getPropertyKeys()));
        }
        return subsets;
    }

    private static class EndpointSubset extends ModelNodeAdapter implements IEndpointSubset {

        protected EndpointSubset(ModelNode node, Map<String, String[]> propertyKeys) {
            super(node, propertyKeys);
        }

        @Override
        public List<IEndpointAddress> getAddresses() {
            List<ModelNode> root = JBossDmrExtentions.get(getNode(), getPropertyKeys(), "addresses").asList();
            ArrayList<IEndpointAddress> addresses = new ArrayList<IEndpointAddress>(root.size());
            for (ModelNode n : root) {
                addresses.add(new EndpointAddress(n, getPropertyKeys()));
            }
            return addresses;
        }

        @Override
        public List<IEndpointAddress> getNotReadyAddresses() {
            List<ModelNode> root = JBossDmrExtentions.get(getNode(), getPropertyKeys(), "notreadyaddresses").asList();
            ArrayList<IEndpointAddress> addresses = new ArrayList<IEndpointAddress>(root.size());
            for (ModelNode n : root) {
                addresses.add(new EndpointAddress(n, getPropertyKeys()));
            }
            return addresses;
        }

        @Override
        public List<IEndpointPort> getPorts() {
            List<ModelNode> root = JBossDmrExtentions.get(getNode(), getPropertyKeys(), PORTS).asList();
            ArrayList<IEndpointPort> ports = new ArrayList<IEndpointPort>(root.size());
            for (ModelNode n : root) {
                ports.add(new EndpointPort(n, getPropertyKeys()));
            }
            return ports;
        }

    }

    private static class EndpointAddress extends ModelNodeAdapter implements IEndpointAddress {

        private static final String TARGET_REF = "targetRef";

        protected EndpointAddress(ModelNode node, Map<String, String[]> propertyKeys) {
            super(node, propertyKeys);
        }

        @Override
        public String getIP() {
            return JBossDmrExtentions.asString(getNode(), getPropertyKeys(), "ip");
        }

        @Override
        public String getHostName() {
            return JBossDmrExtentions.asString(getNode(), getPropertyKeys(), "hostname");
        }

        @Override
        public String getNodeName() {
            return JBossDmrExtentions.asString(getNode(), getPropertyKeys(), "nodeName");
        }

        @Override
        public String getName() {
            return JBossDmrExtentions.asString(getNode(), getPropertyKeys(), NAME);
        }

        @Override
        public IObjectReference getTargetRef() {
            if (getNode().has(JBossDmrExtentions.getPath(getPropertyKeys(), TARGET_REF))) {
                ModelNode node = JBossDmrExtentions.get(getNode(), getPropertyKeys(), TARGET_REF);
                return new ObjectReference(node);
            }
            return null;
        }

    }

    private static class EndpointPort extends ModelNodeAdapter implements IEndpointPort {

        protected EndpointPort(ModelNode node, Map<String, String[]> propertyKeys) {
            super(node, propertyKeys);
        }

        @Override
        public String getName() {
            return JBossDmrExtentions.asString(getNode(), getPropertyKeys(), NAME);
        }

        @Override
        public int getPort() {
            return JBossDmrExtentions.asInt(getNode(), getPropertyKeys(), "port");
        }

        @Override
        public String getProtocol() {
            return JBossDmrExtentions.asString(getNode(), getPropertyKeys(), PROTOCOL);
        }

    }
}
