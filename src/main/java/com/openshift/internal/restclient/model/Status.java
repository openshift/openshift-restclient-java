/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package com.openshift.internal.restclient.model;

import java.util.Map;

import org.jboss.dmr.ModelNode;

import com.openshift.restclient.IClient;
import com.openshift.restclient.model.IStatus;

/**
 * @author Jeff Cantrill
 */
public class Status extends KubernetesResource implements IStatus{
	
	private static final String STATUS_MESSAGE = "message";
	private static final String STATUS_CODE = "code";
	private static final String STATUS_STATUS = "status";


	public Status(ModelNode node, IClient client, Map<String, String []> propertyKeys) {
		super(node, client, propertyKeys);
	}

	public String getMessage(){
		return asString(STATUS_MESSAGE);
	}

	@Override
	public int getCode() {
		return asInt(STATUS_CODE);
	}

	@Override
	public String getStatus() {
		return asString(STATUS_STATUS);
	}

	@Override
	public boolean isFailure() {
		return FAILURE.equalsIgnoreCase(getStatus());
	}

	@Override
	public boolean isSuccess() {
		return SUCCESS.equalsIgnoreCase(getStatus());
	}

}
