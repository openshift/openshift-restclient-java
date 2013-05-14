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
package com.openshift.client.cartridge;

/**
 * @author André Dietisheim
 */
public interface ICartridge {

	public static final char NAME_VERSION_DELIMITER = '-';

	/**
	 * Returns the name of this cartridge
	 * 
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * Returns a (human readable, nice) display name for this cartridge
	 * 
	 * @return the display name
	 */
	public String getDisplayName();
	
	/**
	 * Returns a description for this cartridge
	 * 
	 * @return the description
	 */
	public String getDescription();

}