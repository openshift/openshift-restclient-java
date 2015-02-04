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
import com.openshift3.client.model.IStatus;

public class Status extends KubernetesResource implements IStatus{

	public Status(ModelNode node, IClient client) {
		super(node, client);
	}

	public Status(String json) {
		super(json);
	}
	
	public String getMessage(){
		return getNode().get("message").asString();
	}

}
