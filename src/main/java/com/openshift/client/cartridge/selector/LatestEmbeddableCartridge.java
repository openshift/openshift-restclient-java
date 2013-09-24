/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client.cartridge.selector;

import com.openshift.client.IApplication;
import com.openshift.client.IUser;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.internal.client.utils.Assert;

/**
 * A selector that shall select the latest version of an embedded cartidge that's given by name.
 * 
 * @author Andre Dietisheim
 * 
 * @see IEmbeddableCartridge
 */
public class LatestEmbeddableCartridge extends LatestVersionQuery {

	public LatestEmbeddableCartridge(final String name) {
		super(name);
	}
	
	public IEmbeddableCartridge get(IApplication application) {
		return get(getConnection(application).getEmbeddableCartridges());
	}

	public IEmbeddableCartridge get(IUser user) {
		Assert.notNull(user);
		return get(user.getConnection().getEmbeddableCartridges());
	}
}
