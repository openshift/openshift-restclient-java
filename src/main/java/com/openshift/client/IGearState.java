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

/**
 * @author Corey Daley
 */
public interface IGearState {

	public static final String UNKNOWN = "UNKNOWN";
	
	/**
	 * Get the gears state
	 * @return String the gears state
	 */
	public String getState();

	/**
	 * Set the gears state
	 * @param state
	 */
	public void setState(String state);
}
