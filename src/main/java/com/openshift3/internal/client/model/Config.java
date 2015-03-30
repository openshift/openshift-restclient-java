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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift3.client.IClient;
import com.openshift3.client.model.IConfig;
import com.openshift3.client.model.IResource;
import com.openshift3.internal.client.IResourceFactory;
import com.openshift3.internal.client.model.KubernetesResource;

public class Config extends KubernetesResource implements IConfig{

	public Config(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
	}
	
	@Override
	public Collection<IResource> getItems() {
		Collection<ModelNode> nodes = get(TEMPLATE_ITEMS).asList();
		List<IResource> resources = new ArrayList<IResource>(nodes.size());
		IResourceFactory factory = getClient().getResourceFactory();
		if(factory != null){
			for (ModelNode node : nodes) {
				resources.add(factory.create(node.toJSONString(true)));
			}
		}
		return resources;
	}

}
