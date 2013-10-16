/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.client;


public enum CartridgeType {

	EMBEDDED, STANDALONE;
	
	public static CartridgeType safeValueOf(String type) {
		try {
			if (type == null) {
				return null;
			}
			return valueOf(type.toUpperCase());
		} catch(IllegalArgumentException e) {
			return null;
		}
	}
	
}
