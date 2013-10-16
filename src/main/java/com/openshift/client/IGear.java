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

public interface IGear {

	
	/**
	 * Returns the id of this gear.
	 * 
	 * @return the id
	 */
	public String getId();

	/**
	 * Returns the state of this gear
	 * 
	 * @return the state
	 */
	public GearState getState();
	
	/**
	 * The URL to use when connecting with SSH in the following form:
	 * {@code ssh://<username>@<host>}
	 * @return the SSH URL
	 */
	public String getSshUrl();
}
