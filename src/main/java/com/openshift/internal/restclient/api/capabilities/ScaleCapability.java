/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.restclient.api.capabilities;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.openshift.internal.restclient.capability.AbstractCapability;
import com.openshift.restclient.IClient;
import com.openshift.restclient.ResourceKind;
import com.openshift.restclient.api.ITypeFactory;
import com.openshift.restclient.api.capabilities.IScalable;
import com.openshift.restclient.apis.autoscaling.models.IScale;
import com.openshift.restclient.http.IHttpConstants;
import com.openshift.restclient.model.IReplicationController;

/**
 * Implementation of the scalable interface.  Applies
 * to a deploymentconfig for the moment
 * @author jeff.cantrill
 *
 */
public class ScaleCapability extends AbstractCapability implements IScalable {
	
	private static final Map<String, String> ARG_KINDS = new HashMap<>();
	static {
		ARG_KINDS.put(ResourceKind.DEPLOYMENT_CONFIG, "extensions/v1beta1.Scale");
		ARG_KINDS.put(ResourceKind.REPLICATION_CONTROLLER, "autoscaling/v1.Scale");
	}

	private static final int MIN_VALUE = 0;
	private static final String CAPABILITY = "scale";
	private final IClient client;
	private IReplicationController rc;
	private final ITypeFactory factory;

	public ScaleCapability(IReplicationController rc, IClient client, ITypeFactory factory) {
		super(rc, client, CAPABILITY);
		this.client = client;
		this.rc = rc;
		this.factory = factory;
	}

	@Override
	public String getName() {
		return ScaleCapability.class.getSimpleName();
	}

	@Override
	public IScale scaleTo(int replicas) {
		replicas = replicas >= MIN_VALUE ? replicas : MIN_VALUE; 
		IScale arg = (IScale) factory.stubKind(ARG_KINDS.get(rc.getKind()), Optional.of(rc.getName()), Optional.of(rc.getNamespace()));
		arg.setSpecReplicas(replicas);
		return (IScale) client.execute(factory, IHttpConstants.PUT, rc.getKind(), rc.getNamespace(), rc.getName(), CAPABILITY, null, arg,
            Collections.emptyMap());
	}

}
