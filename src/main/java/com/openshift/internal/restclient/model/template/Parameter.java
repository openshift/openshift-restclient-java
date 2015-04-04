/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model.template;

import org.jboss.dmr.ModelNode;

import com.openshift.restclient.model.template.IParameter;

/**
 * Parameter implementation for a template
 * Foregoing versioned implementation of this type for now since it is unlikely to change
 *  and it is not a versioned resource in Kubernetes.  Update as needed
 */
public class Parameter implements IParameter{
	
	private static final String VALUE = "value";

	private ModelNode node;

	public Parameter(ModelNode node){
		this.node = node;
	}
	@Override
	public String getName() {
		return node.get("name").asString();
	}

	@Override
	public String getDescription() {
		return node.get("description").asString();
	}

	@Override
	public void setValue(String value) {
		node.get(VALUE).set(value);
	}

	@Override
	public String getValue() {
		return node.get(VALUE).asString();
	}

	@Override
	public String getGeneratorName() {
		return node.get("generator").asString();
	}

	@Override
	public String getFrom() {
		return node.get("from").asString();
	}

}
