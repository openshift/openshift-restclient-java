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
package com.openshift.internal.restclient.model.deploy;

import static com.openshift.internal.util.JBossDmrExtentions.*;

import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.api.models.TypeMeta;
import com.openshift.restclient.model.deploy.IDeploymentRequest;

/**
 * 
 * @author Gabe Montero
 *
 */
public class DeploymentRequest extends TypeMeta implements IDeploymentRequest {
    
    private static final String LATEST = "latest";
    private static final String FORCE = "force";

    public DeploymentRequest(ModelNode node, Map<String, String[]> overrideProperties) {
        super(node, overrideProperties);
    }

    @Override
    public void setLatest(boolean latest) {
        set(getNode(), getPropertyKeys(), LATEST, latest);
    }

    @Override
    public boolean isLatest() {
        return asBoolean(getNode(), getPropertyKeys(), LATEST);
    }

    @Override
    public void setForce(boolean force) {
        set(getNode(), getPropertyKeys(), FORCE, force);
    }

    @Override
    public boolean isForce() {
        return asBoolean(getNode(), getPropertyKeys(), FORCE);
    }

    @Override
    public String getName() {
        return asString(getNode(), getPropertyKeys(), NAME);
    }

    @Override
    public void setName(String name) {
        set(getNode(), getPropertyKeys(), NAME, name);
    }

}
