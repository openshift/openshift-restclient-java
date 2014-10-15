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

import com.openshift.client.IApplication;
import com.openshift.client.IOpenShiftResource;
import com.openshift.client.OpenShiftException;
import com.openshift.internal.client.response.CartridgeResourceProperties;

/**
 * Interface to designate a cartridge that has been added and configured
 * 
 * @author André Dietisheim
 * @author Jeff Cantrill
 */
public interface IEmbeddedCartridge extends IOpenShiftResource, IEmbeddableCartridge {

	/**
	 * Destroys this cartridge (and removes it from the list of existing cartridges)
	 * 
	 * @throws OpenShiftException
	 */
	public void destroy() throws OpenShiftException;

	
	/**
	 * Returns the application this cartridge is embedded into.
	 * 
	 * @return application this is embedded into
	 */
	public IApplication getApplication();

	/**
	 * Returns the properties for this embedded cartridge
	 * 
	 * @return the resource properties
	 */
	public CartridgeResourceProperties getProperties();
	
	/**
	 * set the additional gear storage for the cartridge to the given
	 * size.
	 * 
	 * @param size  The total additional gear storage for the cartridge
	 *              in gigabytes
	 */
	public void setAdditionalGearStorage(int size);
}