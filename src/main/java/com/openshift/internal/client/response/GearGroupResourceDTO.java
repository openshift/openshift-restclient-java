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
package com.openshift.internal.client.response;

import java.util.Collection;
import java.util.Map;

/**
 * The DTO for a gear groups
 * 
 * @author Andre Dietisheim
 */
public class GearGroupResourceDTO extends BaseResourceDTO {

	/** The gear groups uuid. */
	private final String uuid;

	/** the gear groups name */
	private final String name;

	/** The gears part of this group. */
	private final Collection<GearResourceDTO> gears;
	
	/** The cartridges part of this group, indexed by their name. */
	private final Map<String, CartridgeResourceDTO> cartridges;
	
	/**
	 * Instantiates a new gears resource dto.
	 * 
	 * @param uuid
	 *            the uuid
	 * @param name
	 *            the gear group name
	 * @param gears
	 *            the gears in this group
	 * @param cartridges
	 *            the cartridges in this group
	 *            
	 */
	GearGroupResourceDTO(final String uuid, final String name, final Collection<GearResourceDTO> gears, final Map<String, CartridgeResourceDTO> cartridges) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.gears = gears;
		this.cartridges = cartridges;
	}

	/**
	 * Returns the name of this gear group
	 * 
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Returns the uuid of this gear group.
	 * 
	 * @return the uuid
	 */
	public final String getUuid() {
		return uuid;
	}

	/**
	 * Returns the gears within this gear group.
	 * 
	 * @return the gears
	 */
	public final Collection<GearResourceDTO> getGears() {
		return gears;
	}

	/**
	 * Returns the cartridges within this gear group.
	 * @return the cartridges
	 */
	public Map<String, CartridgeResourceDTO> getCartridges() {
		return cartridges;
	}

	/**
	 * Returns the cartridge identified by the given name.
	 * @return the cartridge or null if none exists in this group
	 */
	public CartridgeResourceDTO getCartridge(final String name) {
		return cartridges.get(name);
	}

}
