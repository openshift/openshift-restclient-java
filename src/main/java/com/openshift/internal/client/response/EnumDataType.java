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

import java.util.regex.Pattern;

/**
 * The Enum EnumDataType.
 */
public enum EnumDataType {
		
	/** Links / the root node that allows for navigation amongst resources.*/
	links,
	user,
	/** the user's keys. */
	keys,
	/** one user's key.*/
	key,
	domains,
	domain,
	applications,
	application,
	/** The embedded cartridge type. */
	embedded,
	gear_groups,
	/** The standalone cartridges type. */
	cartridges,
	/** The standalone cartridge type. */
	cartridge,
	/** The environment-variables type*/
	environment_variables,
	/** The environmetn-variable type*/
	environment_variable
	;
	
	private static final Pattern pattern = Pattern.compile("-");

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
				 return valueOf(pattern.matcher(value).replaceAll("_"));
			} catch (IllegalArgumentException e) {
				// do nothing, will just return 'undefined'
			}
		}
		return null;
	}
}