/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.restclient.model.volume;

import static com.openshift.internal.util.JBossDmrExtentions.*;

import org.jboss.dmr.ModelNode;

import com.openshift.restclient.model.volume.IHostPathVolumeSource;

/**
 * Implementation of a hostpath volume source
 * @author jeff.cantrill
 *
 */
public class HostPathVolumeSource extends VolumeSource implements IHostPathVolumeSource{

	private static final String PATH = "hostPath.path";

	public HostPathVolumeSource(ModelNode node) {
		super(node);
	}

	@Override
	public String getPath() {
		return asString(getNode(), getPropertyKeys(), PATH);
	}

	@Override
	public void setPath(String path) {
		set(getNode(), getPropertyKeys(), PATH, path);
	}

}
