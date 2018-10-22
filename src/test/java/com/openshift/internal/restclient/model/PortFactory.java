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

import org.jboss.dmr.ModelNode;

public class PortFactory {

    public static ServicePort createServicePort(String name, String proto, int port, int targetPort,int nodePort) {
        return createServicePort(name, proto, port, String.valueOf(targetPort),String.valueOf(nodePort));
    }

    public static ServicePort createServicePort(String name, String proto, int port, String targetPort, String nodePort) {
        ModelNode node = new ModelNode();
        node.get("name").set(name);
        node.get("protocol").set(proto);
        node.get("port").set(port);
        node.get("targetPort").set(targetPort);
        node.get("nodePort").set(nodePort);
        return new ServicePort(node);
    }
}
