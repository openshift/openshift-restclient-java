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
 * @author jeff.cantrill
 *
 */
public abstract class AbstractVolume 
	extends ModelNodeAdapter 
	implements IVolume, ResourcePropertyKeys{
	
	public AbstractVolume(ModelNode node) {
		super(node, new HashMap<String, String []>());
		getPropertyKeys().put(NAME, new String [] {"name"});
	}

	@Override
	public String getName() {
		return asString(getNode(), getPropertyKeys(), NAME);
	}

	@Override
	public void setName(String name) {
		set(getNode(), getPropertyKeys(),NAME, name);
	}
	
	
}
