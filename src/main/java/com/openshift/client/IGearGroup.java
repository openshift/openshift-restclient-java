/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
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

import com.openshift.client.cartridge.ICartridge;

public interface IGearGroup {

	/**
	 * Returns the uuid of this gear groups.
	 * 
	 * @return the uuid
	 */
	public String getUUID();

	/**
	 * Returns the name of this gear groups
	 * 
	 * @return the name
	 */
	public String getName();

	/**
	 * Returns the gears in this gear group resource
	 * 
	 * @return the gears
	 */
	public Collection<IGear> getGears();

	/**
	 * Returns the cartridges in this gear group resource
	 * 
	 * @return the gears
	 */
	public Collection<ICartridge> getCartridges();
}
