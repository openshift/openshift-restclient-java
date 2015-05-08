/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model;

import static com.openshift.internal.restclient.capability.CapabilityInitializer.initializeCapabilities;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.capability.resources.IServiceSinglePortSupport;
import com.openshift.restclient.model.IPod;
import com.openshift.restclient.model.IService;

/**
 * @author Jeff Cantrill
 */
public class Service extends KubernetesResource implements IService {

	private static final String PROPERTY_TARGET_PORT = "targetPort";
	private static final String PROPERTY_PORT = "port";

	public Service(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
		initializeCapabilities(getModifiableCapabilities(), this, getClient());
	}
	
	@Override
	public void setPort(int port){
		if(supports(IServiceSinglePortSupport.class)) {
			IServiceSinglePortSupport capability = getCapability(IServiceSinglePortSupport.class);
			capability.setPort(port);
			return;
		}
		ModelNode nodePort = getLowestPort();
		if(nodePort == null) {
			nodePort = get(SERVICE_PORT).add();
		}
		nodePort.get(PROPERTY_PORT).set(port);
	}
	
	@Override
	public int getPort(){
		if(supports(IServiceSinglePortSupport.class)) {
			IServiceSinglePortSupport capability = getCapability(IServiceSinglePortSupport.class);
			return capability.getPort();
		}
		ModelNode port = getLowestPort();
		return port != null ? port.get(PROPERTY_PORT).asInt() : 0;
	}
	
	private ModelNode getLowestPort() {
		if(get(SERVICE_PORT).getType() == ModelType.UNDEFINED) return null;
		List<ModelNode> ports = new ArrayList<ModelNode>(get(SERVICE_PORT).asList());
		Collections.sort(ports, new Comparator<ModelNode>() {
			@Override
			public int compare(ModelNode node0, ModelNode node1) {
				BigInteger port0 = node0.get(PROPERTY_PORT).asBigInteger();
				BigInteger port1 = node1.get(PROPERTY_PORT).asBigInteger();
				return port0.compareTo(port1);
			}
		});
		return ports.size() == 0 ? null : ports.get(0);
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
		if(supports(IServiceSinglePortSupport.class)) {
			IServiceSinglePortSupport capability = getCapability(IServiceSinglePortSupport.class);
			capability.setContainerPort(port);
			return;
		}
		ModelNode nodePort = getLowestPort();
		if(nodePort == null) {
			nodePort = get(SERVICE_PORT).add();
		}
		nodePort.get(PROPERTY_TARGET_PORT).set(port);
	}
	
	@Override
	public int getContainerPort(){
		if(supports(IServiceSinglePortSupport.class)) {
			IServiceSinglePortSupport capability = getCapability(IServiceSinglePortSupport.class);
			return capability.getContainerPort();
		}
		ModelNode port = getLowestPort();
		return port != null ? port.get(PROPERTY_TARGET_PORT).asInt() : 0;
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
