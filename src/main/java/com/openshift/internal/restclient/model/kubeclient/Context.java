/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.model.kubeclient;

import java.util.HashMap;
import java.util.Map;

import com.openshift.internal.restclient.model.properties.ResourcePropertyKeys;
import com.openshift.restclient.model.kubeclient.IContext;

/**
 * KubeConfig context
 * @author jeff.cantrill
 *
 */
public class Context implements IContext, ResourcePropertyKeys{

	private static final String USER = "user";
	private static final String CLUSTER = "cluster";
	private Map<String, String> context = new HashMap<>();
	private String name;

	public void setContext(Map<String, String> context) {
		this.context.clear();
		this.context.putAll(context);
	}
	@Override
	public String getCluster() {
		return context.get(CLUSTER);
	}

	@Override
	public String getUser() {
		return context.get(USER);
	}

	@Override
	public String getNamespace() {
		return context.get(NAMESPACE);
	}
	@Override
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
