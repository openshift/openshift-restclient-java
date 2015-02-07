/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift3.internal.client.model;

import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift3.client.IClient;
import com.openshift3.client.model.IReplicationController;

public class ReplicationController extends KubernetesResource implements IReplicationController{

	public ReplicationController(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
	}

	@Override
	public int getReplicaCount() {
		return asInt(REPLICATION_CONTROLLER_REPLICA_COUNT);
	}

	@Override
	public Map<String, String> getReplicaSelector() {
		return asMap(REPLICATION_CONTROLLER_REPLICA_SELECTOR);
	}

}
