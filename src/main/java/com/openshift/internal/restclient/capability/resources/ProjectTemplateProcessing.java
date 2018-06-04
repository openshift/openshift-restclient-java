/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package com.openshift.internal.restclient.capability.resources;

import java.util.Collection;

import com.openshift.restclient.IClient;
import com.openshift.restclient.PredefinedResourceKind;
import com.openshift.restclient.capability.resources.IProjectTemplateProcessing;
import com.openshift.restclient.capability.server.ITemplateProcessing;
import com.openshift.restclient.model.IList;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.template.ITemplate;

/**
 * Process and apply template implementation for a specific project
 *
 */
public class ProjectTemplateProcessing implements IProjectTemplateProcessing {

    private String namespace;
    private IClient client;
    private ITemplateProcessing serverCapability;

    public ProjectTemplateProcessing(IProject project, IClient client) {
        if (client != null && client.supports(ITemplateProcessing.class)) {
            serverCapability = client.getCapability(ITemplateProcessing.class);
            this.client = client;
            this.namespace = project.getNamespaceName();
        }
    }

    @Override
    public boolean isSupported() {
        return serverCapability != null;
    }

    @Override
    public String getName() {
        return ProjectTemplateProcessing.class.getSimpleName();
    }

    @Override
    public ITemplate process(ITemplate template) {
        return serverCapability.process(template, namespace);
    }

    @Override
    public Collection<IResource> apply(ITemplate template) {
        IList resources = client.getResourceFactory().create(template.getApiVersion(), PredefinedResourceKind.LIST.getIdentifier());
        resources.addAll(template.getObjects());
        return client.create(resources, this.namespace);
    }

}
