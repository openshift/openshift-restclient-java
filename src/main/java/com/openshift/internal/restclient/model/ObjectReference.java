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

import static com.openshift.internal.restclient.model.properties.ResourcePropertyKeys.*;
import static com.openshift.internal.util.JBossDmrExtentions.*;

import java.util.Collections;
import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.util.JBossDmrExtentions;
import com.openshift.restclient.model.IObjectReference;

public class ObjectReference implements IObjectReference {
	
	private static final Map<String, String[]> KEYS = Collections.emptyMap();
	private ModelNode node;

	public ObjectReference(ModelNode node) {
		this.node = node;
	}
	
	@Override
	public String getKind() {
		return asString(node, null, KIND);
	}
	
	public void setKind(String kind) {
		set(node, KEYS, KIND, kind);
	}

	public void setName(String name) {
		set(node, KEYS, NAME, name);
	}

	@Override
	public String getApiVersion() {
		return asString(node, null, APIVERSION);
	}

	@Override
	public String getResourceVersion() {
		return asString(node, null, RESOURCE_VERSION);
	}

	@Override
	public String getName() {
		return asString(node, null, NAME);
	}

	@Override
	public String getNamespace() {
		return asString(node, null, "namespace");
	}

	@Override
	public String getFieldPath() {
		return asString(node, null, "fieldPath");
	}

	@Override
	public String getUID() {
		return asString(node, null, "uid");
	}

	@Override
	public String toJson() {
		return JBossDmrExtentions.toJsonString(node, false);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ObjectReference other = (ObjectReference) obj;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return toJson();
	}

}
