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

import static com.openshift.internal.util.JBossDmrExtentions.asString;
import static com.openshift.internal.util.JBossDmrExtentions.set;

import org.jboss.dmr.ModelNode;

import com.openshift.restclient.model.volume.IEmptyDirVolumeSource;
import com.openshift.restclient.model.volume.VolumeType;

/**
 * @author Ulf Lilleengen
 */
public class EmptyDirVolumeSource extends VolumeSource implements IEmptyDirVolumeSource {
    private static final String PROP_MEDIUM = "medium";

    private final ModelNode node;

    public EmptyDirVolumeSource(ModelNode node) {
        super(node);
        this.node = node.get(VolumeType.EMPTY_DIR);
    }

    public EmptyDirVolumeSource(String name) {
        this(new ModelNode());
        setName(name);
    }

    @Override
    public String getMedium() {
        return asString(node, getPropertyKeys(), PROP_MEDIUM);
    }

    @Override
    public void setMedium(String medium) {
        set(node, getPropertyKeys(), PROP_MEDIUM, medium);
    }
}
