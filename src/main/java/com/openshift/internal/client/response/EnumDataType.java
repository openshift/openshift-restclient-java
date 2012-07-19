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

/**
 * The Enum EnumDataType.
 */
public enum EnumDataType {
	/** Links / the root node that allows for navigation amongst resources.*/
	links,
	/** the user type. */
	user,
	/** the user's keys. */
	keys,
	/** one user's key.*/
	key,
	/** The domains type. */
	domains,
	/** The domain type. */
	domain,
	/** The applications type. */
	applications,
	/** The application type. */
	application,
	/** The embedded cartridge type. */
	embedded,
	/** the gears type. */
	gears,
	/** The cartridges type. */
	cartridges,
	/** The cartridge type. */
	cartridge;
	

	/**
	 * Returns the enum value matching the given value (as string), or 'undefined' if null/unknown value.
	 * 
	 * @param value
	 *            as String
	 * @return value as enum
	 */
	static EnumDataType safeValueOf(String value) {
		if (value != null) {
			try {
				return valueOf(value);
			} catch (IllegalArgumentException e) {
				// do nothing, will just return 'undefined'
			}
		}
		return null;
	}
}