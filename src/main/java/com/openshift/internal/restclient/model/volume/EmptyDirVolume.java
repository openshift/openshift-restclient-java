/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/package com.openshift.internal.restclient.model.volume;

import org.jboss.dmr.ModelNode;

import com.openshift.restclient.model.volume.IVolume;

/**
 * 
 * @author Jeff Cantrill
 *
 */
public class EmptyDirVolume extends AbstractVolume {

	public EmptyDirVolume(ModelNode node, IVolume volume) {
		super(node);
		if(volume != null) {
			setMountPath(volume.getMountPath());
			setReadOnly(volume.isReadOnly());
			setName(volume.getName());
		}
	}

	public EmptyDirVolume(ModelNode node) {
		this(node, null);
	}

}
