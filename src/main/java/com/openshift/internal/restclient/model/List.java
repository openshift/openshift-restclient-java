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
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.dmr.ModelNode;

import com.openshift.restclient.IClient;
import com.openshift.restclient.IResourceFactory;
import com.openshift.restclient.model.IList;
import com.openshift.restclient.model.IResource;

/**
 * @author Jeff Cantrill
 */
public class List extends KubernetesResource implements IList{

	private static final String ITEMS = "items";
	
	private String kind;
	private Collection<IResource> items;
	public List(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
		String listKind = asString(KIND);
		if(StringUtils.isNotBlank(listKind)) {
			kind = listKind.substring(0, listKind.length() - "List".length());
		}
	}
	
	@Override
	public Collection<IResource> getItems(){
		if(items == null) {
			ModelNode listNode = get(ITEMS);
			if (listNode.isDefined()) {
				Collection<ModelNode> nodes = listNode.asList();
				items = new ArrayList<>(nodes.size());
				IResourceFactory factory = getClient().getResourceFactory();
				if (factory != null) {
					for (ModelNode node : nodes) {
						if (kind != null && !node.get(KIND).isDefined()) {
							set(node, KIND, kind);
						}
						if(!node.get(APIVERSION).isDefined()) {
							set(node, APIVERSION, getApiVersion());
						}
						IResource resource = factory.create(node.toJSONString(true));
						items.add(resource);
					}
				}
			} else {
				items = Collections.emptyList();
			}
		}
		return Collections.unmodifiableCollection(items);
	}

	@Override
	public void addAll(Collection<IResource> items) {
		if(this.items == null) {
			this.items = new ArrayList<>();
		}
		ModelNode itemNode = get(ITEMS);
		for (IResource resource : items) {
			itemNode.add(ModelNode.fromJSONString(resource.toString()));
			this.items.add(resource);
		}
	}

}
