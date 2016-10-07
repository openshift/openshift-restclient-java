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
package com.openshift.internal.restclient.model;

import com.openshift.internal.util.JBossDmrExtentions;
import com.openshift.restclient.model.JSONSerializable;
import org.jboss.dmr.ModelNode;

import java.util.Map;

/**
 * Adapter class between what we want 
 * and the backing DMR json store
 * @author Jeff Cantrill
 *
 */
public class ModelNodeAdapter implements JSONSerializable {
	
	private ModelNode node;
	private Map<String, String []> propertyKeys;


	protected ModelNodeAdapter(ModelNode node, Map<String, String []> propertyKeys) {
		this.node = node;
		this.propertyKeys = propertyKeys;
	}

	protected ModelNode getNode(){
		return node;
	}
	
	@Override
	public String toJson() {
		return toJson(false);
	}

	public String toJson(boolean compact) {
		return JBossDmrExtentions.toJsonString(node, compact);
	}

	protected Map<String, String []> getPropertyKeys() {
		return propertyKeys;
	}



	@Override
	public String toString() {
		return toJson(false);
	}

}
