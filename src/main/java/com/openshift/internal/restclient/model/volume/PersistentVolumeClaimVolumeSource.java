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

package com.openshift.internal.restclient.model.volume;

import static com.openshift.internal.util.JBossDmrExtentions.asBoolean;
import static com.openshift.internal.util.JBossDmrExtentions.asString;
import static com.openshift.internal.util.JBossDmrExtentions.set;

import org.jboss.dmr.ModelNode;

import com.openshift.restclient.model.volume.IPersistentVolumeClaimVolumeSource;
import com.openshift.restclient.model.volume.VolumeType;

/**
 * @author Ulf Lilleengen
 */
public class PersistentVolumeClaimVolumeSource
    extends VolumeSource
    implements IPersistentVolumeClaimVolumeSource {

    private static final String PROP_CLAIM_NAME = "claimName";
    private static final String PROP_READ_ONLY = "readOnly";
    private final ModelNode node;

    public PersistentVolumeClaimVolumeSource(ModelNode node) {
        super(node);
        this.node = node.get(VolumeType.PERSISTENT_VOLUME_CLAIM);
    }

    public PersistentVolumeClaimVolumeSource(String name) {
        this(new ModelNode());
        setName(name);
    }

    @Override
    public String getClaimName() {
        return asString(node, getPropertyKeys(), PROP_CLAIM_NAME);
    }

    @Override
    public void setClaimName(String claimName) {
        set(node, getPropertyKeys(), PROP_CLAIM_NAME, claimName);
    }

    @Override
    public boolean isReadOnly() {
        return asBoolean(node, getPropertyKeys(), PROP_READ_ONLY);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        set(node, getPropertyKeys(), PROP_READ_ONLY, readOnly);
    }
}
