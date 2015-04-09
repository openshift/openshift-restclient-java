/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.restclient.http;

/**
 * @author Andre Dietisheim
 */
public enum HttpMethod {
	GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS;

	/**
	 * hasValue determines if enum can safely convert string to enum value
	 * 
	 * @param  value  the value to inspect
	 * @return true if the value can be safely converted to the enum; false otherwise
	 */
	public static boolean hasValue(String value) {
		HttpMethod[] enumConstants = HttpMethod.class.getEnumConstants();
		for (int i = 0; i < enumConstants.length; i++) {
			HttpMethod httpMethod = enumConstants[i];
			if(httpMethod.name().equalsIgnoreCase(value)){
				return true;
			}
		}
		return false;
	}
}
