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

	private static final String DESIRED_STATE = "desiredState";
	private static final String [] DESIRED_REPLICA_COUNT = {DESIRED_STATE, "replicas"};
	private static final String [] REPLICA_SELECTOR = {DESIRED_STATE, "replicaSelector"};
	public ReplicationController(ModelNode node, IClient client) {
		super(node, client);
	}

	@Override
	public int getReplicaCount() {
		return asInt(DESIRED_REPLICA_COUNT);
	}

	@Override
	public Map<String, String> getSelector() {
		return asMap(REPLICA_SELECTOR);
	}

}
