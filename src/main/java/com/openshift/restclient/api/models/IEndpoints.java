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

package com.openshift.restclient.api.models;

import java.util.List;

import com.openshift.restclient.model.IObjectReference;
import com.openshift.restclient.model.IResource;

/**
 * Endpoint representation 'api/Endpoint'
 * 
 * @author jeff.cantrill
 *
 */
public interface IEndpoints extends IResource {

    List<IEndpointSubset> getSubSets();

    interface IEndpointSubset {

        List<IEndpointAddress> getAddresses();

        List<IEndpointAddress> getNotReadyAddresses();

        List<IEndpointPort> getPorts();
    }

    interface IEndpointAddress {

        String getIP();

        String getHostName();

        String getNodeName();

        String getName();

        IObjectReference getTargetRef();
    }

    interface IEndpointPort {

        String getName();

        int getPort();

        String getProtocol();
    }

}
