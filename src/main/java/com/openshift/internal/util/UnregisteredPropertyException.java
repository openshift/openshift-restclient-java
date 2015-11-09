/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.openshift.internal.util;

/**
 * 
 * @author Jeff Cantrill
 *
 */
@SuppressWarnings("serial")
public class UnregisteredPropertyException extends RuntimeException {

	public UnregisteredPropertyException(String property) {
		super(String.format("No path was found for property '%s' in the property map.", property));
	}
}
