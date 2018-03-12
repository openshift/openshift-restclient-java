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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.resources.IBuildTriggerable;
import com.openshift.restclient.model.IBuild;
import com.openshift.restclient.model.IBuildConfig;
import com.openshift.restclient.model.IResource;
import com.openshift.restclient.model.build.IBuildRequest;

/**
 * 
 * @author Jeff Cantrill
 *
 */
public class BuildTrigger implements IBuildTriggerable {

	private static final String BUILDCONFIG_SUBRESOURCE = "instantiate";
	private static final String BUILD_SUBRESOURCE = "clone";
	private IResource resource;
	private IClient client;
	private final String subresource;
	private String commitId;
	private List<String> causes;
	private HashMap<String,String> envVars = new HashMap<String,String>();

	public BuildTrigger(IBuildConfig buildConfig, IClient client) {
		this.resource = buildConfig;
		this.client = client;
		this.subresource = BUILDCONFIG_SUBRESOURCE;
		this.causes = new ArrayList<>();
	}

	public BuildTrigger(IBuild build, IClient client) {
		this.resource = build;
		this.client = client;
		this.subresource = BUILD_SUBRESOURCE;
		this.causes = new ArrayList<>();
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
		if(StringUtils.isNotEmpty(commitId))
			request.setCommitId(commitId);
		causes.forEach(c->request.addBuildCause(c));
		envVars.forEach((name, value)->request.setEnvironmentVariable(name, value));
		return client.create(resource.getKind(), resource.getNamespaceName(), resource.getName(), subresource, request);
	}

	@Override @Deprecated
	public IBuild trigger(String commitId) {
		IBuildRequest request = client.getResourceFactory().stub(ResourceKind.BUILD_REQUEST, resource.getName());
		request.setCommitId(commitId);
		return client.create(resource.getKind(), resource.getNamespaceName(), resource.getName(), subresource, request);
	}

	@Override
	public void setCommitId(String commitId) {
		this.commitId = commitId;
	}

	@Override
	public String getCommitId() {
		return commitId;
	}

	@Override
	public void addBuildCause(String cause) {
		causes.add(cause);
	}

	@Override
	public List<String> getBuildCauses() {
		return new ArrayList<>(causes);
	}

	@Override
	public void setEnvironmentVariable(String name, String value) {
		envVars.put(name, value);
	}


}
