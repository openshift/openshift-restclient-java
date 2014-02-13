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

import com.openshift.internal.client.utils.StringUtils;

public class GearState implements IGearState {
	/**
	 * @see <a
	 *      href="http://openshift.github.io/documentation/oo_cartridge_developers_guide.html#the-code-status-code-action">OpenShift
	 *      Origin Cartridge Developer’s Guide, 7.10.2. The status Action</a>
	 */
	private String state;

	public GearState(String state) {
		this.state = state.toUpperCase();
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state.toUpperCase();
	}

	public static GearState safeValueOf(String gearStateString) {
		try {
			if (StringUtils.isEmpty(gearStateString)) {
				return new GearState("unknown");
			}
			return new GearState(gearStateString);
		} catch (IllegalArgumentException e) {
			return new GearState("unknown");
		}
	}

	public String toString() {
		return this.state;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GearState other = (GearState) obj;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}
}
