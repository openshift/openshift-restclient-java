/*******************************************************************************
 * Copyright (c) 2014-2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model;

import static com.openshift.internal.restclient.capability.CapabilityInitializer.initializeCapabilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

import com.openshift.restclient.IClient;
import com.openshift.restclient.model.IPod;
import com.openshift.restclient.model.IPort;

/**
 * @author Jeff Cantrill
 */
public class Pod extends KubernetesResource implements IPod {

	public Pod(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
		initializeCapabilities(getModifiableCapabilities(), this, client);
	}

	@Override
	public String getIP() {
		return asString(POD_IP);
	}

	@Override
	public String getHost() {
		return asString(POD_HOST);
	}

	@Override
	public Collection<String> getImages() {
		Collection<String> images = new ArrayList<String>();
		ModelNode node = get(POD_CONTAINERS);
		if(node.getType() != ModelType.LIST) return images;
		for (ModelNode entry : node.asList()) {
			images.add(entry.get("image").asString());
		}
		return images;
	}

	@Override
	public String getStatus() {
		return asString(POD_STATUS);
	}

	@Override
	public Set<IPort> getContainerPorts() {
		Set<IPort> ports = new HashSet<IPort>();
		ModelNode node = get(POD_CONTAINERS);
		if(node.getType() == ModelType.LIST) {
			for (ModelNode container : node.asList()) {
				ModelNode containerPorts = container.get(getPath(PORTS));
				if(containerPorts.getType() == ModelType.LIST) {
					for (ModelNode portNode : containerPorts.asList()) {
						ports.add(new Port(asString(portNode, PORTS_NAME),
								asString(portNode, PORTS_PROTOCOL), 
								asInt(portNode, PORTS_CONTAINER_PORT)));
					}
				}
			}
		}
		return Collections.unmodifiableSet(ports);
	}
	
	
}
