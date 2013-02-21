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

import com.openshift.client.ICartridgeConstraint;
import com.openshift.client.IEmbeddableCartridge;

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
public abstract class AbstractCartridgeConstraint implements ICartridgeConstraint {
	
	public <C extends IEmbeddableCartridge> Collection<C> getMatching(Collection<C> cartridges) {
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
	
	protected abstract <C extends IEmbeddableCartridge> boolean matches(C cartridge);

}
