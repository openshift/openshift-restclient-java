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
package com.openshift.internal.restclient.model.volume;

import static com.openshift.internal.util.JBossDmrExtentions.*;

import java.util.HashMap;

import org.jboss.dmr.ModelNode;

import com.openshift.internal.restclient.model.ModelNodeAdapter;
import com.openshift.internal.restclient.model.properties.ResourcePropertyKeys;
import com.openshift.restclient.model.volume.IVolume;

/**
 * 
 * @author Jeff Cantrill
 *
 */
public abstract class AbstractVolume 
	extends ModelNodeAdapter 
	implements IVolume, ResourcePropertyKeys{
	
	private static final String READONLY = "readOnly";
	private static final String MOUNT_PATH = "mountPath";
	
	public AbstractVolume(ModelNode node) {
		super(node, new HashMap<String, String []>());
	}

	@Override
	public String getName() {
		return asString(getNode(), getPropertyKeys(), NAME);
	}

	@Override
	public void setName(String name) {
		set(getNode(), getPropertyKeys(), NAME, name);
	}

	@Override
	public void setMountPath(String path) {
		set(getNode(), getPropertyKeys(), MOUNT_PATH, path);
	}

	@Override
	public String getMountPath() {
		return asString(getNode(), getPropertyKeys(), MOUNT_PATH);
	}

	@Override
	public void setReadOnly(boolean readonly) {
		set(getNode(), getPropertyKeys(), READONLY, readonly);
	}

	@Override
	public boolean isReadOnly() {
		return asBoolean(getNode(), getPropertyKeys(), READONLY);
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof AbstractVolume))
			return false;
		AbstractVolume other = (AbstractVolume) obj;
		return getName().equals(other.getName()) &&
				getMountPath().equals(other.getMountPath()) &&
				isReadOnly() == other.isReadOnly();
	}

	@Override
	public int hashCode() {
		int code = isReadOnly() ? 1 : 0;
		code = code + getName().hashCode();
		return code + getMountPath().hashCode();
	}
	
	
	
}
