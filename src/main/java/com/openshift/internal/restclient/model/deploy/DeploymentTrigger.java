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

import static com.openshift.internal.util.JBossDmrExtentions.*;
import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.model.properties.ResourcePropertyKeys;
import com.openshift.restclient.model.deploy.IDeploymentTrigger;

/**
 * 
 * @author Jeff Cantrill
 *
 */
public class DeploymentTrigger implements IDeploymentTrigger, ResourcePropertyKeys {
	
	final private ModelNode node;
	final private Map<String, String[]> propertyKeys;
	
	public DeploymentTrigger(ModelNode node, Map<String, String[]> propertyKeys) {
		this.node = node;
		this.propertyKeys = propertyKeys;
	}
	
	@Override
	public String getType() {
		return asString(node, propertyKeys, TYPE);
	}
	
	protected ModelNode getNode() {
		return node;
	}
	
	protected Map<String, String[]> getPropertyKeys(){
		return this.propertyKeys;
	}
}
