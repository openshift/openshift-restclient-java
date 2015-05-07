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

import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.KubernetesAPIVersion;
import com.openshift.internal.restclient.model.Service;
import com.openshift.internal.restclient.model.properties.ResourcePropertiesRegistry;
import com.openshift.internal.util.JBossDmrExtentions;
import com.openshift.restclient.capability.resources.IServiceSinglePortSupport;

/**
 * @author jeff.cantrill
 */
public class ServiceSinglePortCapability implements IServiceSinglePortSupport {

	private Service service;

	public ServiceSinglePortCapability(Service service) {
		this.service = service;
	}

	@Override
	public boolean isSupported() {
		return KubernetesAPIVersion.v1beta1.toString().equals(service.getApiVersion());
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public int getPort() {
		return asInt(ResourcePropertiesRegistry.SERVICE_PORT);
	}

	@Override
	public int getContainerPort() {
		return asInt(ResourcePropertiesRegistry.SERVICE_CONTAINER_PORT);
	}
	
	private int asInt(String key) {
		ModelNode node =	service.getNode();
		return JBossDmrExtentions.asInt(node, getPropertyKeys(), key);
	}
	
	private Map<String, String[]> getPropertyKeys(){
		return  ResourcePropertiesRegistry.getInstance().get(service.getApiVersion(), service.getKind());
	}

	@Override
	public void setContainerPort(int port) {
		service.getNode().get(getPropertyKeys().get(ResourcePropertiesRegistry.SERVICE_CONTAINER_PORT)).set(port);
	}

	@Override
	public void setPort(int port) {
		service.getNode().get(getPropertyKeys().get(ResourcePropertiesRegistry.SERVICE_PORT)).set(port);
	}

}
