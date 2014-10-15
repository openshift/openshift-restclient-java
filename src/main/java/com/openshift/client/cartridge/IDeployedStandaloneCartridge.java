/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.client.cartridge;

import com.openshift.client.IGearGroup;

/**
 * Represents a standalone cartridge that has been deployed as opposed to IStandaloneCartridge
 * which really represents the metadata about a standalone cartridge
 * 
 * @author Jeff Cantrill
 * @author Andre Dietisheim
 */
public interface IDeployedStandaloneCartridge extends IStandaloneCartridge{

	/**
	 * set the additional gear storage for the cartridge to the given
	 * size.
	 * 
	 * @param size  The total additional gear storage for the cartridge
	 *              in gigabytes
	 */
	public void setAdditionalGearStorage(int size);

	/**
	 * Returns the additional gear storage for this cartridge
	 * @return
	 */
	public int getAdditionalGearStorage();
	
	/**
	 * Returns the gear group that this cartridge is deployed on
	 * @return
	 */
	public IGearGroup getGearGroup();
}
