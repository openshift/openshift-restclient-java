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
package com.openshift.client;

import java.util.Collection;

/**
 * @author Andre Dietisheim
 * 
 */
public interface ICartridgeConstraint {

	/**
	 * Returns the cartridge that matches this constraint.
	 * 
	 * @param availableCartridges
	 *            the cartridges that are available that shall get matched against this constraint
	 * @return the embeddable cartridges that match this constraint
	 */
	public <C extends IEmbeddableCartridge> Collection<C> getMatching(Collection<C> availableCartridges);

	/**
	 * Returns <code>true</code> if the given cartridge matches this constraint.
	 * 
	 * @param cartridge the cartridge that shall match this constraint
	 * @return true if matches 
	 */

	public <C extends IEmbeddableCartridge> boolean matches(C cartridge);
}
