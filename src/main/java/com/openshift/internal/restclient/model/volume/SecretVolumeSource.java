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

import com.openshift.restclient.model.volume.ISecretVolumeSource;
import com.openshift.restclient.model.volume.VolumeType;
import org.jboss.dmr.ModelNode;

import static com.openshift.internal.util.JBossDmrExtentions.asString;
import static com.openshift.internal.util.JBossDmrExtentions.set;

/**
 * @author Ulf Lilleengen
 */
public class SecretVolumeSource
        extends VolumeSource
        implements ISecretVolumeSource {

    private static final String PROP_SECRET_NAME = "secretName";
    private final ModelNode node;

    public SecretVolumeSource(ModelNode node) {
        super(node);
        this.node = node.get(VolumeType.SECRET);
    }

    public SecretVolumeSource(String name) {
        this(new ModelNode());
        setName(name);
    }

    @Override
    public String getSecretName() {
        return asString(node, getPropertyKeys(), PROP_SECRET_NAME);
    }

    @Override
    public void setSecretName(String secretName) {
        set(node, getPropertyKeys(), PROP_SECRET_NAME, secretName);
    }
}
