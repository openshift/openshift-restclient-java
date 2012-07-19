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
package com.openshift.client;

/**
 * Interface to designate a cartridge that has been added and configured
 * @author André Dietisheim
 */
public interface IEmbeddedCartridge extends IOpenShiftResource, IEmbeddableCartridge {

	public String getUrl() throws OpenShiftException;
			
	/**
	 * Destroys this cartridge (and removes it from the list of existing cartridges)
	 * 
	 * @throws OpenShiftException
	 */
	public void destroy() throws OpenShiftException;


}