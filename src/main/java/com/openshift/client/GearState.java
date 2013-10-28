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

public enum GearState {
	/**
	 * @see <a
	 *      href="http://openshift.github.io/documentation/oo_cartridge_developers_guide.html#the-code-status-code-action">OpenShift
	 *      Origin Cartridge Developer’s Guide, 7.10.2. The status Action</a>
	 */
	BUILDING, DEPLOYING, IDLE, NEW, STARTED, STOPPED, UNKNOWN;

	public static GearState safeValueOf(String gearStateString) {
		try {
			if (StringUtils.isEmpty(gearStateString)) {
				return UNKNOWN;
			}
			return valueOf(gearStateString.toUpperCase());
		} catch (IllegalArgumentException e) {
			return UNKNOWN;
		}
	}

}
