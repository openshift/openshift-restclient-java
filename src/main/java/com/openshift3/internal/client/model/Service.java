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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift3.client.IClient;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.model.IPod;
import com.openshift3.client.model.IService;


public class Service extends KubernetesResource implements IService {

	private static String [] SELECTOR = {"selector"};
	
	public Service (IClient client) {
		this(new ModelNode(), client);
		setApiVersion("v1beta1");
		set("kind", ResourceKind.Service.toString());
	}
	
	public Service(ModelNode node, IClient client) {
		super(node, client);
	}
	
	@Override
	public void setPort(int port){
		set("port", port);
	}
	
	@Override
	public int getPort(){
		return asInt("port");
	}
	
	@Override
	public Map<String, String> getSelector(){
		return asMap(SELECTOR);
	}
	
	@Override
	public void setSelector(Map<String, String> selector) {
		ModelNode node = new ModelNode();
		for (Map.Entry<String, String> entry : selector.entrySet()) {
			node.get(entry.getKey()).set(entry.getValue());
		}
		getNode().get(SELECTOR).set(node);
	}
	
	
	@Override
	public void setSelector(String key, String value) {
		String[] path = Arrays.copyOf(SELECTOR, SELECTOR.length + 1);
		path[SELECTOR.length] = key;
		getNode().get(path).set(value);
	}

	@Override
	public void setContainerPort(int port){
		set("containerPort", port);
	}
	
	@Override
	public int getContainerPort(){
		return asInt("containerPort");
	}

	@Override
	public String getPortalIP() {
		return asString("portalIP");
	}

	@Override
	public List<IPod> getPods() {
		if(getClient() == null) return new ArrayList<IPod>();
		return getClient().list(ResourceKind.Pod, getNamespace(), getSelector());
	}
}
