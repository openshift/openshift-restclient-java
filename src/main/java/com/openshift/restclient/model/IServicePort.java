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

package com.openshift.restclient.model;

public interface IServicePort {

    /**
     * The name of the port
     * 
     * @return the name or null if undefined.
     */
    String getName();

    void setName(String name);

    /**
     * Port exposed by the service
     * 
     */
    int getPort();

    void setPort(int port);

    /**
     * The target port on the pod it services. An integer or named port on the pod
     * spec
     * 
     */
    String getTargetPort();

    void setTargetPort(int port);

    void setTargetPort(String name);

    /**
     * IP protocol (TCP, UDP)
     * 
     */
    String getProtocol();

    void setProtocol(String proto);

    /**
     * External service port
     */

    String getNodePort();

    void setNodePort(String nodePort);
}
