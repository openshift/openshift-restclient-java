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
package com.openshift.internal.restclient.model.properties;

/**
 * Keys for deployment configs
 * @author jeff.cantrill
 *
 */
public interface DeploymentConfigPropertyKeys {

	static final String DEPLOYMENTCONFIG_CONTAINERS = "deploymentconfig.containers";
	static final String DEPLOYMENTCONFIG_REPLICAS = "deploymentconfig.replicas";
	static final String DEPLOYMENTCONFIG_REPLICA_SELECTOR = "deploymentconfig.replica.selector";
	static final String DEPLOYMENTCONFIG_TRIGGERS = "deploymentconfig.triggers";
	static final String DEPLOYMENTCONFIG_STRATEGY = "deploymentconfig.strategy";
	static final String DEPLOYMENTCONFIG_TEMPLATE_LABELS = "deploymentconfig.template.labels";
	static final String DEPLOYMENTCONFIG_VOLUMES = "deploymentconfig.volumes";
	
	static final String DEPLOYMENTCONFIG_TRIGGER_CONTAINERS = "deploymentconfig.triggers.imagechange.containers";
	static final String DEPLOYMENTCONFIG_TRIGGER_IMAGECHANGE_AUTO = "deploymentconfig.triggers.imagechange.automatic";
	static final String DEPLOYMENTCONFIG_TRIGGER_FROM = "deploymentconfig.triggers.imagechange.from";
	static final String DEPLOYMENTCONFIG_TRIGGER_FROM_KIND = "deploymentconfig.triggers.imagechange.from.kind";
	
}
