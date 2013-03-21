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
public class GearDTO extends BaseResourceDTO {

	/**
	 * Instantiates a new gearGroup dto.
	 *
	 */
	
	String uuid;
	String state;
	
	public GearDTO(String uuid, String state, List<Message> creationLog) {
		super(new HashMap<String, Link>(), creationLog);
		this.uuid = uuid;
		this.state = state;
	}
	
	@Override
	public String toString() {
		return "GearDTO";
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public String getState() {
		return state;
	}

}
