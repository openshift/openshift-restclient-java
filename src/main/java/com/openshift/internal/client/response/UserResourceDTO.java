/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package com.openshift.internal.client.response;

import java.util.Map;

public class UserResourceDTO extends BaseResourceDTO {

	/** the user's login on rhcloud. */
	private final String rhLogin;
	
	private final int maxGears;
	private final int consumedGears;
	
	UserResourceDTO(final String rhLogin, final int maxGears, final int consumedGears, final Map<String, Link> links) {
		super(links, null);
		this.rhLogin = rhLogin;
		this.maxGears = maxGears;
		this.consumedGears = consumedGears;
	}

	/**
	 * @return the rhLogin
	 */
	public String getRhLogin() {
		return rhLogin;
	}
	
	/**
	 * @return the maxGears
	 */
	public int getMaxGears() {
		return maxGears;
	}
	
	/**
	 * @return the consumedGears
	 */
	public int getConsumedGears() {
		return consumedGears;
	}
	

}
