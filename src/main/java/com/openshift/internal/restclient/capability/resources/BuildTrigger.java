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
package com.openshift.internal.restclient.capability.resources;

import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.resources.IBuildTriggerable;
import com.openshift.restclient.model.IBuild;
import com.openshift.restclient.model.IBuildConfig;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.build.IBuildRequest;

/**
 * 
 * @author jeff.cantrill
 *
 */
public class BuildTrigger implements IBuildTriggerable {

	private static final String BUILDCONFIG_SUBRESOURCE = "instantiate";
	private static final String BUILD_SUBRESOURCE = "clone";
	private IResource resource;
	private IClient client;
	private final String subresource;

	public BuildTrigger(IBuildConfig buildConfig, IClient client) {
		this.resource = buildConfig;
		this.client = client;
		this.subresource = BUILDCONFIG_SUBRESOURCE;
	}

	public BuildTrigger(IBuild build, IClient client) {
		this.resource = build;
		this.client = client;
		this.subresource = BUILD_SUBRESOURCE;
	}

	@Override
	public boolean isSupported() {
		return resource != null && client != null && (ResourceKind.BUILD.equals(resource.getKind()) || ResourceKind.BUILD_CONFIG.equals(resource.getKind()));
	}

	@Override
	public String getName() {
		return BuildTrigger.class.getSimpleName();
	}

	@Override
	public IBuild trigger() {
		IBuildRequest request = client.getResourceFactory().stub(ResourceKind.BUILD_REQUEST, resource.getName());
		return client.create(resource.getKind(), resource.getNamespace(), resource.getName(), subresource, request);
	}

	@Override
	public IBuild trigger(String commitId) {
		IBuildRequest request = client.getResourceFactory().stub(ResourceKind.BUILD_REQUEST, resource.getName());
		request.setCommitId(commitId);
		return client.create(resource.getKind(), resource.getNamespace(), resource.getName(), subresource, request);
	}
	
	

}
