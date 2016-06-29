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

import static com.openshift.internal.util.JBossDmrExtentions.*;

import java.util.HashMap;

import com.openshift.restclient.model.volume.VolumeType;
import org.apache.commons.lang.StringUtils;
import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.model.ModelNodeAdapter;
import com.openshift.internal.restclient.model.properties.ResourcePropertyKeys;
import com.openshift.restclient.model.volume.IVolumeSource;

/**
 * 
 * @author Jeff Cantrill
 *
 */
public abstract class VolumeSource
	extends ModelNodeAdapter 
	implements IVolumeSource, ResourcePropertyKeys {

	public VolumeSource(ModelNode node) {
		super(node, new HashMap<String, String []>());
	}

	@Override
	public String getName() {
		return asString(getNode(), getPropertyKeys(), NAME);
	}

    @Override
    public void setName(String name) {
        set(getNode(), getPropertyKeys(), NAME, name);
    }

    @Override
    public String toJSONString() {
        if (StringUtils.isBlank(getName())) {
            throw new IllegalArgumentException("Name of volume source is missing");
        }
        return toJson(true);
    }

    public static IVolumeSource create(ModelNode node) {
        if (node.has(VolumeType.EMPTY_DIR)) {
            return new EmptyDirVolumeSource(node);
        } else if (node.has(VolumeType.SECRET)) {
            return new SecretVolumeSource(node);
        } else if (node.has(VolumeType.PERSISTENT_VOLUME_CLAIM)) {
            return new PersistentVolumeClaimVolumeSource(node);
        } else {
            return new VolumeSource(node) {};
        }
    }
}
