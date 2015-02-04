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

import org.jboss.dmr.ModelNode;

import com.openshift3.client.IClient;
import com.openshift3.client.ResourceKind;
import com.openshift3.client.model.IProject;

public class Project extends OpenShiftResource implements IProject{


	public Project() {
		this(new ModelNode(), null);
		set("kind", ResourceKind.Project.toString());
	}
	
	public Project(ModelNode node, IClient client) {
		super(node, client);
	}
	
	public String getDisplayName(){
		return asString("displayName");
	}
	
	public void setDisplayName(String name) {
		get("displayName").set(name);
	}

	public <T extends KubernetesResource> List<T> getResources(ResourceKind kind){
		if(getClient() == null) return new ArrayList<T>();
		return getClient().list(kind, getNamespace());
	}
}
