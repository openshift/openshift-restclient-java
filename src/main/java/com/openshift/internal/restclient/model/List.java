/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.model.IList;
import com.openshift.restclient.model.IResource;

/**
 * @author Jeff Cantrill
 */
public class List extends KubernetesResource implements IList{

	public List(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
	}
	
	@Override
	public Collection<IResource> getItems(){
		String key = getNode().has(OBJECTS) ? OBJECTS : "items";
		Collection<ModelNode> nodes = get(key).asList();
		java.util.List<IResource> resources = new ArrayList<IResource>(nodes.size());
		IResourceFactory factory = getClient().getResourceFactory();
		if(factory != null){
			for (ModelNode node : nodes) {
				resources.add(factory.create(node.toJSONString(true)));
			}
		}
		return resources;
	}

	@Override
	public void addAll(Collection<IResource> items) {
		ModelNode itemNode = get(OBJECTS);
		for (IResource resource : items) {
			itemNode.add(ModelNode.fromJSONString(resource.toString()));
		}
	}

}
