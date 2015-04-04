/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model;

import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.restclient.IClient;
import com.openshift.restclient.model.IBuild;

public class Build extends KubernetesResource implements IBuild{

	public Build(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
	}

	@Override
	public String getStatus() {
		return asString(BUILD_STATUS);
	}

	@Override
	public String getMessage() {
		return asString(BUILD_MESSAGE);
	}

	@Override
	public String getPodName() {
		return asString(BUILD_PODNAME);
	}

	
}
