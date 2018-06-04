/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/

package com.openshift.internal.restclient.capability.server;

import com.openshift.restclient.IApiTypeMapper;
import com.openshift.restclient.IClient;
import com.openshift.restclient.PredefinedResourceKind;
import com.openshift.restclient.capability.server.ITemplateProcessing;
import com.openshift.restclient.model.template.ITemplate;

public class ServerTemplateProcessing implements ITemplateProcessing {

    private IClient client;

    public ServerTemplateProcessing(IClient client) {
        this.client = client;
    }

    @Override
    public boolean isSupported() {
        IApiTypeMapper mapper = client.adapt(IApiTypeMapper.class);
        if (mapper != null) {
            return mapper.isSupported(PredefinedResourceKind.PROCESSED_TEMPLATES.getIdentifier());
        }
        return false;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public ITemplate process(ITemplate template, String namespace) {
        return client.execute("POST", PredefinedResourceKind.PROCESSED_TEMPLATES.getIdentifier(), namespace, null, null, template);

    }

}
