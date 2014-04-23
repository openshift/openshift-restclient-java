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
package com.openshift.internal.client;

import java.util.List;

import com.openshift.client.cartridge.ICartridge;
import com.openshift.client.cartridge.query.ICartridgeQuery;

/**
 * A list of alternative cartridges that one can choose from. The user
 * may only pick 1 cartridge among the alternatives.
 * 
 * @author Andre Dietisheim
 * 
 */
public class AlternativeCartridges {

	private ICartridgeQuery query;
	private APIResource api;

	AlternativeCartridges(ICartridgeQuery query, APIResource api) {
		this.query = query;
		this.api = api;
	}

	/**
	 * Returns all alternative cartridges that a user may choose its cartridge
	 * from.
	 *
	 * @return the alternative cartridges
	 */
	public List<ICartridge> get() {
		return query.getAll(api.getCartridges());
	}

	@Override
	public String toString() {
		return "AlternativeCartridges [query=" + query + "]";
	}

}