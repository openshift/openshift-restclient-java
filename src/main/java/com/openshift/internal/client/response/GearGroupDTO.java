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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.openshift.client.ApplicationScale;
import com.openshift.client.IGearGroup;
import com.openshift.client.IGearProfile;

/**
 * The Class ApplicationDTO.
 *
 * @author Xavier Coulon
 */
public class GearGroupDTO extends BaseResourceDTO {
	
	private String name;
	private String gearProfile;
	private List<GearDTO> gears;

	/**
	 * Instantiates a new gearGroup dto.
	 *
	 */
	public GearGroupDTO(String name, String gearProfile, List<GearDTO> gears, List<Message> creationLog) {
		super(new HashMap<String, Link>(), creationLog);
		this.name = name;
		this.gearProfile = gearProfile;
		this.gears = gears;
	}
	
	public String getName() {
		return name;
	}
	
	public String getGearProfile() {
		return gearProfile;
	}
	
	@Override
	public String toString() {
		return "GearGroupDTO";
	}
	
	public List<GearDTO> getGears() {
		return gears;
	}

}
