/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client.model;

import org.jboss.dmr.ModelNode;

import com.openshift3.client.IClient;

public class OpenShiftResource extends KubernetesResource {
	
	public OpenShiftResource(ModelNode node, IClient client) {
		super(node, client);
		setApiVersion("v1beta1");
	}
	
	public OpenShiftResource(String json){
		super(json);
	}

	private static final String [] NAME = {"metadata", "name"};
	private static final String [] NAMESPACE = {"metadata", "namespace"};

	@Override
	public String getName() {
		return asString(NAME);
	}

	@Override
	public String getNamespace() {
		return asString(NAMESPACE);
	}

	@Override
	public void setName(String name) {
		set(NAME, name);
	}

	@Override
	public void setNamespace(String namespace) {
		set(NAMESPACE, namespace);
	}
}
