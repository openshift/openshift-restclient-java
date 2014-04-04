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

import com.openshift.client.IApplication;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.IUser;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.internal.client.utils.Assert;

/**
 * A query that shall select the latest version of a standalone cartidge that's given by name.
 * 
 * @author Andre Dietisheim
 * 
 * @see IStandaloneCartridge
 */
public class LatestStandaloneCartridge {

	private LatestVersionQuery query;

	public LatestStandaloneCartridge(final String name) {
		this.query = new LatestVersionQuery(name);
	}
	
	public boolean matches(IStandaloneCartridge cartridge) {
		return query.matches(cartridge);
	}
	
	public IStandaloneCartridge get(IOpenShiftConnection connection) {
		Assert.isTrue(connection != null);
		return query.get(connection.getStandaloneCartridges());
	}

	public IStandaloneCartridge get(IApplication application) {
		return get(application.getDomain().getUser().getConnection());
	}

	public IStandaloneCartridge get(IUser user) {
		Assert.notNull(user);
		return get(user.getConnection());
	}
}
