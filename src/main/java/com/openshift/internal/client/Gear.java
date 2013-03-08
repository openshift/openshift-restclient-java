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

import com.openshift.client.GearState;
import com.openshift.client.IGear;


/**
 * A gear that a cartridge is running on.
 * 
 * @author Andre Dietisheim
 */
public class Gear implements IGear {
	
	private final String id;
	private GearState state;

	public Gear(String id, GearState state) {
		this.id = id;
		this.state = state;
	}

	public String getId() {
		return id;
	}
	
	public GearState getState() {
		return state;
	}
	
	public String toString() {
		return "Gear [id=" + id + ", state=" + state + "]";
	}
}