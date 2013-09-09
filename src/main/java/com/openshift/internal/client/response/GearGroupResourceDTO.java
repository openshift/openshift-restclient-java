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

import com.openshift.client.IGear;

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

	private Collection<IGear> gears;

	/**
	 * Instantiates a new gears resource dto.
	 * 
	 * @param uuid
	 *            the uuid
	 * @param components
	 *            the components
	 * @param gitUrl
	 *            the git url
	 */
	GearGroupResourceDTO(final String uuid, final String name, Collection<IGear> gears) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.gears = gears;
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
	public final Collection<IGear> getGears() {
		return gears;
	}
}
