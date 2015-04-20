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
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.resources.IProjectTemplateList;
import com.openshift.restclient.model.IProject;
import com.openshift.restclient.model.template.ITemplate;

public class ProjectTemplateListCapability implements IProjectTemplateList {
	
	private IProject project;
	private IClient client;

	public ProjectTemplateListCapability(IProject project, IClient client) {
		this.project = project;
		this.client = client;
	}

	@Override
	public boolean isSupported() {
		return client != null && project != null;
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public Collection<ITemplate> getTemplates() {
		return client.<ITemplate>list(ResourceKind.Template, project.getNamespace());
	}

}
