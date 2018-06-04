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

package com.openshift.internal.restclient.capability.resources;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.restclient.IClient;
import com.openshift.restclient.OpenShiftException;
import com.openshift.restclient.PredefinedResourceKind;
import com.openshift.restclient.capability.resources.IDeployCapability;
import com.openshift.restclient.http.IHttpConstants;
import com.openshift.restclient.model.IDeploymentConfig;
import com.openshift.restclient.model.IReplicationController;

public class DeployCapability implements IDeployCapability {

    private static final List<String> COMPLETED_STATES = Arrays.asList("Complete", "Failed");
    private static final Logger LOG = LoggerFactory.getLogger(IDeployCapability.class);

    private final IClient client;
    private final IDeploymentConfig config;

    public DeployCapability(IDeploymentConfig config, IClient client) {
        this.config = config;
        this.client = client;

    }

    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public String getName() {
        return DeployCapability.class.getSimpleName();
    }

    @Override
    public void deploy() {
        try {
            final String deploymentName = getLatestDeploymentName();
            LOG.debug("Attempting to deploy latest deployment for config %s.  Loading deployment: %s", config.getName(),
                    deploymentName);
            IReplicationController deployment = client.get(PredefinedResourceKind.REPLICATION_CONTROLLER.getIdentifier(), deploymentName,
                    config.getNamespaceName());
            final String status = getStatusFor(deployment);
            if (!COMPLETED_STATES.contains(status)) {
                LOG.debug("Skipping deployment because deployment status %s for %s is not in %s",
                        new Object[] { status, deploymentName, COMPLETED_STATES });
                return;
            }
        } catch (OpenShiftException e) {
            if (e.getStatus() == null || e.getStatus().getCode() != IHttpConstants.STATUS_NOT_FOUND) {
                // swallow exception like cli
                throw e;
            }
        }

        // bumping as currently not supporting 'retry'
        int version = config.getLatestVersionNumber();
        config.setLatestVersionNumber(++version);
        client.update(config);

    }

    private String getLatestDeploymentName() {
        return String.format("%s-%d", config.getName(), config.getLatestVersionNumber());
    }

    private String getStatusFor(IReplicationController rc) {
        if (rc.isAnnotatedWith(IReplicationController.DEPLOYMENT_PHASE)) {
            return rc.getAnnotation(IReplicationController.DEPLOYMENT_PHASE);
        }
        return "";
    }
}
