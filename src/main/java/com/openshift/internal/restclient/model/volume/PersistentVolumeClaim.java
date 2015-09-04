/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.restclient.model.volume;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

import com.openshift.internal.restclient.model.KubernetesResource;
import com.openshift.restclient.IClient;
import com.openshift.restclient.model.volume.IPersistentVolumeClaim;

/**
 * 
 * @author jeff.cantrill
 *
 */
public class PersistentVolumeClaim extends KubernetesResource implements IPersistentVolumeClaim {

	public PersistentVolumeClaim(ModelNode node, IClient client, Map<String, String[]> propertyKeys) {
		super(node, client, propertyKeys);
	}

	@Override
	public Set<String> getAccessModes() {
		Set<String> modes = new HashSet<>();
		ModelNode modelNode = get(PVC_ACCESS_MODES);
		if(!modelNode.isDefined() || !modelNode.getType().equals(ModelType.LIST)) return modes;
		for (ModelNode node : modelNode.asList()) {
			modes.add(node.asString());
		}
		return modes;
	}

	@Override
	public String getRequestedStorage() {
		return asString(PVC_REQUESTED_STORAGE);
	}

	@Override
	public String getStatus() {
		return asString(STATUS_PHASE);
	}

}
