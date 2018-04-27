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

package com.openshift.internal.restclient.model.deploy;

import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.restclient.model.deploy.IDeploymentConfigChangeTrigger;

public class ConfigChangeTrigger extends DeploymentTrigger implements IDeploymentConfigChangeTrigger {

    public ConfigChangeTrigger(ModelNode node, Map<String, String[]> propertyKeys) {
        super(node, propertyKeys);
    }

}
