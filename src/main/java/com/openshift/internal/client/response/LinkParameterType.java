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

import com.openshift.client.OpenShiftRequestException;


/**
 * @author Andre Dietisheim
 */
public enum LinkParameterType {
	STRING, BOOLEAN, INTEGER, ARRAY;
	
	public static LinkParameterType valueOfIgnoreCase(String name) throws OpenShiftRequestException {
		if (name == null) {
			// no type provided (this is valid, not an error)
			return null;
		}
		try {
			return	valueOf(name.toUpperCase());
		} catch(IllegalArgumentException e) {
			throw new OpenShiftRequestException("Unknow request parameter type {0}", name);
		}
	}
}
