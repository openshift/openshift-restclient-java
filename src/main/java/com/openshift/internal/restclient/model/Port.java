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

import static com.openshift.internal.util.JBossDmrExtentions.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.dmr.ModelNode;

import com.openshift.restclient.model.IPort;

/**
 * 
 * @author jeff.cantrill
 *
 */
public class Port implements IPort {
	
	private static final String PROPERTY_NAME = "name";
	private static final String PROPERTY_PROTOCOL = "protocol";
	private static final String PROPERTY_CONTAINER_PORT = "containerPort";
	private static final Map<String, String []> KEY_MAP = new HashMap<>();

	static {
		KEY_MAP.put(PROPERTY_NAME, new String[] {PROPERTY_NAME});
		KEY_MAP.put(PROPERTY_PROTOCOL, new String[] {PROPERTY_PROTOCOL});
		KEY_MAP.put(PROPERTY_CONTAINER_PORT, new String[] {PROPERTY_CONTAINER_PORT});
	}

	private ModelNode node;

	public Port(ModelNode node) {
		this.node = node;
	}

	public Port(ModelNode node, IPort port) {
		this(node);
		if(StringUtils.isNotEmpty(port.getName())) {
			setName(port.getName());
		}
		setProtocol(port.getProtocol());
		setContainerPort(port.getContainerPort());
	}
	
	public ModelNode getNode() {
		return this.node;
	}
	
	@Override
	public String getName() {
		return asString(node, KEY_MAP, PROPERTY_NAME);
	}

	public void setName(String name) {
		set(node, KEY_MAP, PROPERTY_NAME, name);
	}

	public void setContainerPort(int port) {
		set(node, KEY_MAP, PROPERTY_CONTAINER_PORT, port);
	}
	
	@Override
	public int getContainerPort() {
		return asInt(node, KEY_MAP, PROPERTY_CONTAINER_PORT);
	}

	public void setProtocol(String name) {
		set(node, KEY_MAP, PROPERTY_PROTOCOL, name);
	}
	
	@Override
	public String getProtocol() {
		return asString(node, KEY_MAP, PROPERTY_PROTOCOL);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getContainerPort();
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result + ((getProtocol() == null) ? 0 : getProtocol().hashCode());
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
		Port other = (Port) obj;
		if (getContainerPort() != other.getContainerPort())
			return false;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		if (getProtocol() == null) {
			if (other.getProtocol() != null)
				return false;
		} else if (!getProtocol().equals(other.getProtocol()))
			return false;
		return true;
	}
	
	
}
