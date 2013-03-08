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


import java.util.Collection;
import java.util.Collections;

import com.openshift.client.IGear;
import com.openshift.client.IGearGroup;
import com.openshift.client.OpenShiftException;
import com.openshift.internal.client.response.GearGroupResourceDTO;

/**
 * @author Andre Dietisheim
 */
public class GearGroupResource extends AbstractOpenShiftResource implements IGearGroup {

	/** The uuid of this gear group. */
	private final String uuid;

	/** the name of this gear group */
	private final String name;

	/** the gears of this gear group resource */
	private Collection<IGear> gears;

	protected GearGroupResource(final String uuid, String name, Collection<IGear> gears, IRestService service) {
		super(service);
		this.uuid = uuid;
		this.name = name;
		this.gears = gears;
	}

	protected GearGroupResource(GearGroupResourceDTO dto, IRestService service) {
		this(dto.getUuid(), dto.getName(), dto.getGears(), service);
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

	@Override
	public void refresh() throws OpenShiftException {
	}

}
