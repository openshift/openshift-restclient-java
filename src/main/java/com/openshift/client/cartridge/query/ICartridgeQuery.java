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
package com.openshift.client.cartridge.query;

import java.util.List;

import com.openshift.client.cartridge.ICartridge;

/**
 * A query that shall selected cartridges among the given collection of cartridges.
 * 
 * @author Andre Dietisheim
 *
 */
public interface ICartridgeQuery {

	public <C extends ICartridge> List<C> getAll(List<C> cartridges);

	public <C extends ICartridge> C get(List<C> cartridges);

	public <C extends ICartridge> boolean matches(C cartridge);

}