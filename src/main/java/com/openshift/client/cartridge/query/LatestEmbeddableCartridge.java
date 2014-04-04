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
package com.openshift.client.cartridge.query;

import java.util.List;

import com.openshift.client.IApplication;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.internal.client.utils.Assert;

/**
 * A query that shall select the latest version of an embedded cartidge that's given by name.
 * 
 * @author Andre Dietisheim
 * 
 * @see IEmbeddableCartridge
 */
public class LatestEmbeddableCartridge {

	private LatestVersionQuery query;

	public LatestEmbeddableCartridge(final String name) {
		this.query = new LatestVersionQuery(name);
	}
	
	public boolean matches(IEmbeddableCartridge cartridge) {
		return query.matches(cartridge);
	}

	public <C extends IEmbeddableCartridge> C get(List<C> cartridges) {
		Assert.notNull(cartridges);
		return query.get(cartridges);
	}

	public IEmbeddableCartridge get(IOpenShiftConnection connection) {
		Assert.notNull(connection);
		return get(connection.getEmbeddableCartridges());
	}

	public IEmbeddableCartridge get(IApplication application) {
		Assert.notNull(application);
		return get(application.getDomain().getUser().getConnection());
	}

	public IEmbeddableCartridge get(IUser user) {
		Assert.notNull(user);
		return get(user.getConnection());
	}
}
