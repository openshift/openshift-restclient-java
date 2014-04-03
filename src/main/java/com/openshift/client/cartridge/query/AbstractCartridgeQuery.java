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

import java.util.ArrayList;
import java.util.List;

import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.cartridge.ICartridge;
import com.openshift.internal.client.utils.Assert;

/**
 * A base query class for query that shall match cartridges. 
 * 
 * @author Andre Dietisheim
 */
public abstract class AbstractCartridgeQuery implements ICartridgeQuery {
	
	@Override
	public <C extends ICartridge> List<C> getAll(List<C> cartridges) {
		List<C> matchingCartridges = new ArrayList<C>();

		if (cartridges == null) {
			return matchingCartridges;
		}
		
		for (C cartridge : cartridges) {
			if (matches(cartridge)) {
				matchingCartridges.add(cartridge);
			}
		}

		return matchingCartridges;
	}
	
	@Override
	public <C extends ICartridge> C get(List<C> cartridges) {
		List<C> matchingCartridges = getAll(cartridges);
		 if (matchingCartridges == null
				 || matchingCartridges.size() == 0) {
			 return null;
		 }
		 return matchingCartridges.iterator().next();
	}

	@Override
	public abstract <C extends ICartridge> boolean matches(C cartridge);

	protected IOpenShiftConnection getConnection(IApplication application) {
		Assert.notNull(application);
		return application.getDomain().getUser().getConnection();
	}

	protected IOpenShiftConnection getConnection(IDomain domain) {
		Assert.notNull(domain);
		return domain.getUser().getConnection();
	}
}
