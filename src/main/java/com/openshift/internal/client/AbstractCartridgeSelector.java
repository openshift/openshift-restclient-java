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
package com.openshift.internal.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IOpenShiftConnection;
import com.openshift.client.cartridge.ICartridge;
import com.openshift.client.cartridge.IEmbeddableCartridge;
import com.openshift.internal.client.utils.Assert;

/**
 * A base class for a constraint that shall match available embeddable
 * cartridges (on the platform). Among several matching ones, the one with the
 * highest version is chosen. application.
 * 
 * @author Andre Dietisheim
 * 
 * @see IEmbeddableCartridge for cartridges that have already been added and
 *      configured to an application.
 */
public abstract class AbstractCartridgeSelector {
	
	protected <C extends ICartridge> Collection<C> getAllMatching(Collection<C> cartridges) {
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
