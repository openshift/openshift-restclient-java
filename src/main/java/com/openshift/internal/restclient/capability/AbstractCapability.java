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

package com.openshift.internal.restclient.capability;

import com.openshift.restclient.IApiTypeMapper;
import com.openshift.restclient.IClient;
import com.openshift.restclient.UnsupportedEndpointException;
import com.openshift.restclient.capability.ICapability;
import com.openshift.restclient.model.IResource;

/**
 * Capability base
 *
 */
public abstract class AbstractCapability implements ICapability {

    private IApiTypeMapper mapper;
    private IResource resource;
    private final String capability;

    protected AbstractCapability(IResource resource, IClient client, String capability) {
        this.capability = capability;
        this.resource = resource;
        this.mapper = client.adapt(IApiTypeMapper.class);
    }

    @Override
    public boolean isSupported() {
        if (mapper != null) {
            try {
                return mapper.getEndpointFor(resource.getApiVersion(), resource.getKind()).isSupported(capability);
            } catch (UnsupportedEndpointException e) {
                // endpoint not found for version/kind
            }
        }
        return false;
    }

}
