/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift3.client.IClient;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.model.IPod;
import com.openshift3.client.model.IService;


public class Service extends KubernetesResource implements IService {

	public Service(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
	}
	
	@Override
	public void setPort(int port){
		set(SERVICE_PORT, port);
	}
	
	@Override
	public int getPort(){
		return asInt(SERVICE_PORT);
	}
	
	@Override
	public Map<String, String> getSelector(){
		return asMap(SERVICE_SELECTOR);
	}
	
	@Override
	public void setSelector(Map<String, String> selector) {
		ModelNode node = new ModelNode();
		for (Map.Entry<String, String> entry : selector.entrySet()) {
			node.get(entry.getKey()).set(entry.getValue());
		}
		get(SERVICE_SELECTOR).set(node);
	}
	
	
	@Override
	public void setSelector(String key, String value) {
		get(SERVICE_SELECTOR).get(key).set(value);
	}

	@Override
	public void setContainerPort(int port){
		set(SERVICE_CONTAINER_PORT, port);
	}
	
	@Override
	public int getContainerPort(){
		return asInt(SERVICE_CONTAINER_PORT);
	}

	@Override
	public String getPortalIP() {
		return asString(SERVICE_PORTALIP);
	}

	@Override
	public List<IPod> getPods() {
		if(getClient() == null) return new ArrayList<IPod>();
		return getClient().list(ResourceKind.Pod, getNamespace(), getSelector());
	}
}
