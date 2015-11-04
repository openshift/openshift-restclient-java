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

import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.restclient.IClient;
import com.openshift.restclient.model.IResourceQuota;

public class ResourceQuota extends KubernetesResource implements IResourceQuota {
	
	private static final String RESOURCE_QUOTA_CPU = "spec.hard.cpu";
	private static final String RESOURCE_QUOTA_MEMORY = "spec.hard.memory";
	private static final String RESOURCE_QUOTA_PODS = "spec.hard.pods";
	private static final String RESOURCE_QUOTA_SERVICES = "spec.hard.services";
	private static final String RESOURCE_QUOTA_REPLICATIONCONTROLLERS = "spec.hard.replicationcontrollers";
	private static final String RESOURCE_QUOTA_RESOURCEQUOTAS = "spec.hard.resourcequotas";

	public ResourceQuota(ModelNode node, IClient client, Map<String, String[]> propertyKeys) {
		super(node, client, propertyKeys);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getCpu() {
		return asString(RESOURCE_QUOTA_CPU);
	}

	@Override
	public void setCpu(String cpu) {
		get(RESOURCE_QUOTA_CPU).set(cpu);
	}

	@Override
	public String getMemory() {
		return asString(RESOURCE_QUOTA_MEMORY);
	}

	@Override
	public void setMemory(String memory) {
		get(RESOURCE_QUOTA_MEMORY).set(memory);
		
	}

	@Override
	public String getPods() {
		return asString(RESOURCE_QUOTA_PODS);
	}

	@Override
	public void setPods(String pods) {
		get(RESOURCE_QUOTA_PODS).set(pods);
	}

	@Override
	public String getServices() {
		return asString(RESOURCE_QUOTA_SERVICES);
	}

	@Override
	public void setServices(String services) {
		get(RESOURCE_QUOTA_SERVICES).set(services);
	}

	@Override
	public String getReplicationcontrollers() {
		return asString(RESOURCE_QUOTA_REPLICATIONCONTROLLERS);
	}

	@Override
	public void setReplicationcontrollers(String replicationcontrollers) {
		get(RESOURCE_QUOTA_REPLICATIONCONTROLLERS).set(replicationcontrollers);
	}

	@Override
	public String getResourcequotas() {
		return asString(RESOURCE_QUOTA_RESOURCEQUOTAS);
	}

	@Override
	public void setResourcequotas(String resourcequotas) {
		get(RESOURCE_QUOTA_RESOURCEQUOTAS).set(resourcequotas);
	}
	
	
}
