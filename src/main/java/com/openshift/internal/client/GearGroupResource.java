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
package com.openshift.internal.client;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.openshift.client.GearState;
import com.openshift.client.IGear;
import com.openshift.client.IGearGroup;
import com.openshift.client.OpenShiftException;
import com.openshift.client.cartridge.ICartridge;
import com.openshift.internal.client.response.CartridgeResourceDTO;
import com.openshift.internal.client.response.GearGroupResourceDTO;
import com.openshift.internal.client.response.GearResourceDTO;

/**
 * @author Andre Dietisheim
 * @author Xavier Coulon
 */
public class GearGroupResource extends AbstractOpenShiftResource implements IGearGroup {

	/** The uuid of this gear group. */
	private final String uuid;

	/** the name of this gear group */
	private final String name;

	/** the gears of this gear group resource */
	private final Collection<IGear> gears;
	
	/** the cartridges in this gear group resource */
	private final Collection<ICartridge> cartridges;

	/**
	 * Constructor.
	 * 
	 * @param uuid the gear group's UUID
	 * @param name the gear group's name
	 * @param gearDTOs the gear group's gears
	 * @param cartridgeDTOs the gear group's cartridges, indexed by their name
	 * @param application the gear group's parent application
	 * @param service the underlying REST Service
	 */
	protected GearGroupResource(final String uuid, final String name, final Collection<GearResourceDTO> gearDTOs,
			final Map<String, CartridgeResourceDTO> cartridgeDTOs, final ApplicationResource application, final IRestService service) {
		super(service);
		this.uuid = uuid;
		this.name = name;
		this.gears = new ArrayList<IGear>();
		for(GearResourceDTO dto : gearDTOs) {
			this.gears.add(new Gear(dto.getUuid(), GearState.safeValueOf(dto.getState()), dto.getSshUrl()));
		}
		this.cartridges = new ArrayList<ICartridge>();
		for(Iterator<Entry<String, CartridgeResourceDTO>> iterator = cartridgeDTOs.entrySet().iterator(); iterator.hasNext();) {
			final String cartridgeName = iterator.next().getKey();
			final ICartridge cartridge = application.getCartridge(cartridgeName);
			if(cartridge != null) {
				cartridges.add(cartridge);
			}
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param dto the associated {@link GearGroupResourceDTO} 
	 * @param application the parent application 
	 * @param servicethe underlying REST Service
	 */
	protected GearGroupResource(final GearGroupResourceDTO dto, final ApplicationResource application, final IRestService service) {
		this(dto.getUuid(), dto.getName(), dto.getGears(), dto.getCartridges(), application, service);
	}

	public final String getUUID() {
		return uuid;
	}

	public final String getName() {
		return name;
	}

	public Collection<IGear> getGears() {
		return Collections.unmodifiableCollection(gears);
	}

	/**
	 * @return the cartridges
	 */
	public Collection<ICartridge> getCartridges() {
		return cartridges;
	}

	@Override
	public void refresh() throws OpenShiftException {
	}

}
